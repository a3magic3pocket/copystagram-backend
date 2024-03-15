package com.copystagram.api.post;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.validation.constraints.Size;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.copystagram.api.global.config.GlobalConfig;
import com.copystagram.api.global.file.LocalFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Service
public class PostService {
	public final LocalFileUtil localFileUtil;
	public final GlobalConfig globalConfig;
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

		// - 이미지 처리
		// 이미지 jpg로 변경(jpg로 퀄리티 설정)
		// 이미지 orientation 조정
		// 이미지 가로:세로 = 1:1이 되도록 crop
		// 본문용 이미지로 크키 조정 후 저장
		// thumbnail 이미지로 크키 조정 후 저장
		if (true) {
			return;
		}

		String postPrefix = "/" + globalConfig.getRootImageDirName() + "/" + message.getImageDirName();
		Path postRawDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getRawDirName());
		Path postThumbDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getThumbDirName());
		Path postContentDirPath = localFileUtil.getStaticFilePath(postPrefix + "/" + globalConfig.getContentDirName());

		Files.createDirectories(postThumbDirPath);
		Files.createDirectories(postContentDirPath);

		System.out.println("postThumbDirPath +" + postThumbDirPath);
		System.out.println("postContentDirPath +" + postContentDirPath);

		Set<Path> rawImagePaths = new HashSet<Path>();
		try (Stream<Path> stream = Files.list(postRawDirPath)) {
			rawImagePaths = stream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());
		}

		System.out.println("rawImagePaths+" + rawImagePaths);

		int j = 0;
		for (Path rawImagePath : rawImagePaths) {
			byte[] bytes = Files.readAllBytes(rawImagePath);

			try (InputStream is = new ByteArrayInputStream(bytes)) {
				BufferedImage rawImage = ImageIO.read(is);
				System.out.println("theImage+" + rawImage);

				int w = rawImage.getWidth();
				int h = rawImage.getHeight();
				boolean isWidthLonger = w > h;
				int diff = isWidthLonger ? w - h : h - w;
				int smallSide = isWidthLonger ? h : w;
				BufferedImage resultImage = new BufferedImage(smallSide, smallSide, 1);

				if (isWidthLonger) {
					resultImage = rawImage.getSubimage(diff / 2, 0, smallSide, smallSide);
				} else {
					resultImage = rawImage.getSubimage(0, diff / 2, smallSide, smallSide);
				}

				File outputfile = new File(localFileUtil.getStaticFilePath("/result" + j + ".jpeg").toString());

				// Writing image in new file created
				ImageIO.write(resultImage, "jpeg", outputfile);

				j++;
			}

		}
	}
}
