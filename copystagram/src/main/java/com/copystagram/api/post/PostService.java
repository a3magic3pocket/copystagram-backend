package com.copystagram.api.post;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.copystagram.api.global.config.GlobalConfig;
import com.copystagram.api.global.file.LocalFileUtil;
import com.copystagram.api.global.image.ImageManipulation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
	public final LocalFileUtil localFileUtil;
	public final GlobalConfig globalConfig;
	public final ImageManipulation imageManipulation;
	public final KafkaTemplate<String, Object> kafkaTemplate;

	public void create(PostCreationDto postCreationDto) {
		System.out.println("postCreationDto: " + postCreationDto);
		CompletableFuture.runAsync(() -> {
			System.out.println("inner async start");
			try {
				String imageDirName = UUID.randomUUID().toString();
				saveRowImageFiles(imageDirName, postCreationDto);

				PostCreationKafkaDto postCreationKafkaDto = new PostCreationKafkaDto();
				postCreationKafkaDto.setDescription(postCreationDto.description);
				postCreationKafkaDto.setImageDirName(imageDirName);

				this.kafkaTemplate.send("post-creation", postCreationKafkaDto);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("inner async end");
		}).exceptionally((e) -> {
			System.out.println("e: " + e);
			System.out.println("come in exceptionally");
			return null;
		});

		System.out.println("end create PostService");
	}

	private void saveRowImageFiles(String imageDirName, PostCreationDto postCreationDto) throws IOException {
		String postRawDirName = "/" + globalConfig.getRootImageDirName() + "/" + imageDirName + "/"
				+ globalConfig.getRawDirName();
		Path postRawDirPath = localFileUtil.getStaticFilePath(postRawDirName);

		Files.createDirectories(postRawDirPath);

		Map<Path, byte[]> fileInfoMap = new HashMap<Path, byte[]>();

		Map<Integer, PostCreationImageDto> imageMap = postCreationDto.getImageMap();
		for (Integer i : imageMap.keySet()) {
			PostCreationImageDto postCreationImageDto = imageMap.get(i);
			String imageName = "/" + i + "_" + postCreationImageDto.getOriginalFilename();
			Path imagePath = localFileUtil.getStaticFilePath(postRawDirName + imageName);

			fileInfoMap.put(imagePath, postCreationImageDto.getImageBytes());
		}

		localFileUtil.write(fileInfoMap);
	}

	@KafkaListener(topics = "post-creation", groupId = "post-creation", containerFactory = "postCreationKafkaListener")
	private void consumePostCreation(PostCreationKafkaDto message) throws java.io.IOException {
		System.out.println("receive message: " + message);
		System.out.println("message.getDescription()" + message.getDescription());
		System.out.println("message.ImageDirName()" + message.getImageDirName());

		String postPrefix = "/" + globalConfig.getRootImageDirName() + "/" + message.getImageDirName();
		Path postRawDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getRawDirName());
		Path postThumbDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getThumbDirName());
		Path postContentDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getContentDirName());

		Files.createDirectories(postThumbDirPath);
		Files.createDirectories(postContentDirPath);

		System.out.println("postThumbDirPath +" + postThumbDirPath);
		System.out.println("postContentDirPath +" + postContentDirPath);

		Set<Path> rawImagePaths = localFileUtil.getFilePaths(postRawDirPath);
		System.out.println("rawImagePaths+" + rawImagePaths);

		int i = 0;
		String imageExt = "jpeg";
		for (Path rawImagePath : rawImagePaths) {
			byte[] bytes = Files.readAllBytes(rawImagePath);

			try (InputStream is = new ByteArrayInputStream(bytes)) {
				// 이미지 가로:세로 = 1:1이 되도록 crop
				BufferedImage resultImage = imageManipulation.cropSquare(is);
				// thumbnail 이미지 resize
				BufferedImage thumbImage = imageManipulation.resize(resultImage, 144);
				// content 이미지 resize
				BufferedImage contentImage = imageManipulation.resize(resultImage, 430);

				File thumbfile = new File(postThumbDirPath + "/" + i + "." + imageExt);
				ImageIO.write(thumbImage, "jpeg", thumbfile);

				File contentfile = new File(postContentDirPath + "/" + i + "." + imageExt);
				ImageIO.write(contentImage, imageExt, contentfile);
			}

			i++;
		}
	}
}
