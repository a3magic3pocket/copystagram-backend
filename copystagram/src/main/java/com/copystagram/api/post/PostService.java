package com.copystagram.api.post;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import com.copystagram.api.global.config.GlobalConfig;
import com.copystagram.api.global.encryption.HashUtil;
import com.copystagram.api.global.file.LocalFileUtil;
import com.copystagram.api.global.image.ImageManipulation;
import com.copystagram.api.noti.NotiService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
	public final LocalFileUtil localFileUtil;
	public final GlobalConfig globalConfig;
	public final ImageManipulation imageManipulation;
	public final PostRepository postRepository;
	public final NotiService notiService;
	public final HashUtil hashUtil;
	public final KafkaTemplate<String, Object> kafkaTemplate;

	public void create(PostCreationDto postCreationDto) {
		System.out.println("postCreationDto: " + postCreationDto);
		String imageDirName = UUID.randomUUID().toString();
		String ownerId = postCreationDto.getOwnerId();

		CompletableFuture.runAsync(() -> {
			System.out.println("inner async start");
			try {
				saveRawImageFiles(imageDirName, postCreationDto);

				PostCreationKafkaDto postCreationKafkaDto = new PostCreationKafkaDto();
				postCreationKafkaDto.setDescription(postCreationDto.getDescription());
				postCreationKafkaDto.setImageDirName(imageDirName);
				postCreationKafkaDto.setOwnerId(ownerId);
				postCreationKafkaDto.setCreatedAt(LocalDateTime.now());

				this.kafkaTemplate.send("post-creation", postCreationKafkaDto);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("inner async end");
		}).exceptionally((e) -> {
			System.out.println("e: " + e);
			System.out.println("come in exceptionally");

			// 실패 시 이미지 디렉토리 삭제
			String imageDirPrefix = "/" + globalConfig.getRootImageDirName() + "/" + imageDirName;
			Path imageDirPath = localFileUtil.getStaticFilePath(imageDirPrefix);
			localFileUtil.deleteDir(imageDirPath);

			notiService.createNoti(ownerId, "POSTC-FAILURE");

			return null;
		});

		System.out.println("end create PostService");
	}

	private void saveRawImageFiles(String imageDirName, PostCreationDto postCreationDto) throws IOException {
		String postRawDirName = "/" + globalConfig.getRootImageDirName() + "/" + imageDirName + "/"
				+ globalConfig.getRawDirName();
		Path postRawDirPath = localFileUtil.getStaticFilePath(postRawDirName);

		Files.createDirectories(postRawDirPath);

		Map<Path, byte[]> fileInfoMap = new HashMap<Path, byte[]>();

		Map<Integer, PostCreationImageDto> imageMap = postCreationDto.getImageMap();
		for (Integer i : imageMap.keySet()) {
			PostCreationImageDto postCreationImageDto = imageMap.get(i);
			System.out.println(i + "     " + postCreationImageDto.getOriginalFilename());
			String imageName = "/" + i + "_" + postCreationImageDto.getOriginalFilename();
			Path imagePath = localFileUtil.getStaticFilePath(postRawDirName + imageName);

			fileInfoMap.put(imagePath, postCreationImageDto.getImageBytes());
		}

		localFileUtil.write(fileInfoMap);
	}

	@KafkaListener(topics = "post-creation", groupId = "post-creation", containerFactory = "postCreationKafkaListener")
	private void consumePostCreation(PostCreationKafkaDto message, Acknowledgment acknowledgment) {
		System.out.println("receive message: " + message);
		System.out.println("message.getDescription()" + message.getDescription());
		System.out.println("message.ImageDirName()" + message.getImageDirName());

		String ownerId = message.getOwnerId();
		String imageDirName = message.getImageDirName();
		String postPrefix = "/" + globalConfig.getRootImageDirName() + "/" + imageDirName;
		Path postRawDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getRawDirName());

		try {
			// 이미지 조작
			Path postThumbDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getThumbDirName());
			Path postContentDirPath = localFileUtil
					.getStaticFilePath(postPrefix + "/" + globalConfig.getContentDirName());

			String thumbImagePath = "";
			List<String> contentImagePaths = new ArrayList<String>();

			Files.createDirectories(postThumbDirPath);
			Files.createDirectories(postContentDirPath);

			List<Path> rawImagePaths = localFileUtil.getFilePaths(postRawDirPath);
			System.out.println("rawImagePaths+" + rawImagePaths);

			int i = 0;
			String imageExt = "jpeg";
			for (Path rawImagePath : rawImagePaths) {
				System.out.println("     " + rawImagePath);
				byte[] bytes = Files.readAllBytes(rawImagePath);

				try (InputStream is = new ByteArrayInputStream(bytes)) {
					// 이미지 가로:세로 = 1:1이 되도록 crop
					BufferedImage resultImage = imageManipulation.cropSquare(is);

					// content 이미지 resize
					BufferedImage contentImage = imageManipulation.resize(resultImage, 430);

					String contentFileName = "/" + i + "." + imageExt;
					File contentfile = new File(postContentDirPath + contentFileName);
					ImageIO.write(contentImage, imageExt, contentfile);

					String contentImagePath = "/" + imageDirName + "/" + globalConfig.getContentDirName()
							+ contentFileName;

					contentImagePaths.add(contentImagePath);

					System.out.println("contentImageUri" + contentImagePath);

					if (i == 0) {
						// thumbnail 이미지 resize
						BufferedImage thumbImage = imageManipulation.resize(resultImage, 144);

						String thumbFileName = "/" + i + "." + imageExt;
						File thumbfile = new File(postThumbDirPath + thumbFileName);
						ImageIO.write(thumbImage, "jpeg", thumbfile);

						thumbImagePath = "/" + imageDirName + "/" + globalConfig.getThumbDirName() + thumbFileName;

						System.out.println("thumbImageUri" + thumbImagePath);
					}
				}

				i++;
			}

			// DB 처리
			String source = message.getDescription() + message.getImageDirName() + thumbImagePath + contentImagePaths
					+ message.getCreatedAt();

			Post newPost = new Post();
			newPost.setOwnerId(ownerId);
			newPost.setDescription(message.getDescription());
			newPost.setImageDirName(message.getImageDirName());
			newPost.setThumbImagePath(thumbImagePath);
			newPost.setContentImagePaths(contentImagePaths);
			newPost.setCreatedAt(message.getCreatedAt());
			newPost.setDocHash(hashUtil.getSha256Hash(source));
			postRepository.save(newPost);

			notiService.createNoti(ownerId, "POSTC-SUCCESS");

			acknowledgment.acknowledge();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("e: " + e);

			// 실패 시 이미지 디렉토리 삭제
			Path imageDirPath = localFileUtil.getStaticFilePath(postPrefix);
			localFileUtil.deleteDir(imageDirPath);

			notiService.createNoti(ownerId, "POSTC-FAILURE");

			acknowledgment.nack(Duration.ofMinutes(4));
		} finally {
			System.out.println("come in final?" + postRawDirPath);
			// raw 이미지 디렉토리 삭제
			localFileUtil.deleteDir(postRawDirPath);
		}
	}

	public PostListDto getLatestAllPosts(int pageNum, int pageSize) {
		int skip = (pageNum - 1) * pageSize;

		List<Post> posts = postRepository.getLatestAllPosts(skip, pageSize);

		PostListDto postListDto = new PostListDto();
		postListDto.setPageNum(pageNum);
		postListDto.setPageSize(posts.size());
		postListDto.setPostRetrDtos(posts);

		return postListDto;
	}

	public PostListDto getLatestPosts(int pageNum, int pageSize, String id) {
		int skip = (pageNum - 1) * pageSize;

		List<Post> posts = postRepository.getLatestPosts(skip, pageSize, id);

		PostListDto postListDto = new PostListDto();
		postListDto.setPageNum(pageNum);
		postListDto.setPageSize(posts.size());
		postListDto.setPostRetrDtos(posts);

		return postListDto;
	}
}
