package com.copystagram.api.post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.copystagram.api.global.config.GlobalConfig;
import com.copystagram.api.global.file.LocalFileUtil;

import lombok.RequiredArgsConstructor;

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

		System.out.println("come in here?");
	}

	private void saveRowImageFiles(String imageDirName, PostCreationDto postCreationDto) throws IOException {
		String postRawSourceName = "/" + globalConfig.getRootImageDirName() + "/" + imageDirName + "/raw";
		Path postRawSourcePath = localFileUtil.getStaticFilePath(postRawSourceName);

		Files.createDirectories(postRawSourcePath);

		Map<Path, MultipartFile> fileInfoMap = new HashMap<Path, MultipartFile>();

		for (int i = 0; i < postCreationDto.imageFiles.length; i++) {
			MultipartFile imageFile = postCreationDto.imageFiles[i];
			String imageName = "/" + i + "_" + imageFile.getOriginalFilename();
			Path imagePath = localFileUtil.getStaticFilePath(postRawSourceName + imageName);
			fileInfoMap.put(imagePath, imageFile);
		}

		localFileUtil.write(fileInfoMap);
	}

	@KafkaListener(topics = "post-creation", groupId = "post-creation", containerFactory = "postCreationKafkaListener")
	private void consumePostCreation(PostCreationKafkaDto message) throws java.io.IOException {
		System.out.println("receive message: " + message);
		System.out.println("message.getDescription()" + message.getDescription());
		System.out.println("message.ImageDirName()" + message.getImageDirName());
	}
}
