package com.copystagram.api.global.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.copystagram.api.global.config.GlobalConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LocalFileUtil implements FileUtil {
	public final GlobalConfig globalConfig;

	@Override
	public FileUtilResultDto write(MultipartFile input, Path filePath) {
		FileUtilResultDto fileUtilResultDto = new FileUtilResultDto();
		List<Path> failedPaths = new ArrayList<Path>();
		fileUtilResultDto.setOk(true);

		try {
			try (InputStream inputStream = input.getInputStream()) {
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("LocalFileUtil.write e: " + e);

			failedPaths.add(filePath);
			fileUtilResultDto.setOk(false);
		}

		fileUtilResultDto.setFailedPaths(failedPaths);

		return fileUtilResultDto;
	}

	@Override
	public FileUtilResultDto write(byte[] input, Path filePath) {
		FileUtilResultDto fileUtilResultDto = new FileUtilResultDto();
		List<Path> failedPaths = new ArrayList<Path>();
		fileUtilResultDto.setOk(true);

		try {
			try (FileOutputStream fos = new FileOutputStream(filePath.toString())) {
				fos.write(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("LocalFileUtil.write e: " + e);

			failedPaths.add(filePath);
			fileUtilResultDto.setOk(false);
		}

		fileUtilResultDto.setFailedPaths(failedPaths);

		return fileUtilResultDto;
	}

	@Override
	public FileUtilResultDto write(Map<Path, byte[]> fileInfoMap) {
		FileUtilResultDto fileUtilResultDto = new FileUtilResultDto();
		List<Path> failedPaths = new ArrayList<Path>();
		fileUtilResultDto.setOk(true);

		for (Path filePath : fileInfoMap.keySet()) {
			byte[] input = fileInfoMap.get(filePath);
			FileUtilResultDto resultDto = write(input, filePath);
			if (!resultDto.isOk) {
				failedPaths.add(filePath);
				fileUtilResultDto.setOk(false);
			}
		}

		fileUtilResultDto.setFailedPaths(failedPaths);

		return fileUtilResultDto;
	}

	@Override
	public FileUtilResultDto delete(Path filePath) {
		FileUtilResultDto fileUtilResultDto = new FileUtilResultDto();
		List<Path> failedPaths = new ArrayList<Path>();

		File file = new File(filePath.toString());
		boolean isDeleted = file.delete();
		if (!isDeleted) {
			failedPaths.add(filePath);
		}

		fileUtilResultDto.setOk(isDeleted);
		fileUtilResultDto.setFailedPaths(failedPaths);

		return fileUtilResultDto;
	}

	@Override
	public FileUtilResultDto delete(Path[] filesPaths) {
		FileUtilResultDto fileUtilResultDto = new FileUtilResultDto();
		List<Path> failedPaths = new ArrayList<Path>();
		fileUtilResultDto.setOk(true);

		for (Path filePath : filesPaths) {
			FileUtilResultDto resultDto = delete(filePath);
			if (!resultDto.isOk) {
				failedPaths.add(filePath);
				fileUtilResultDto.setOk(false);
			}
		}

		fileUtilResultDto.setFailedPaths(failedPaths);

		return fileUtilResultDto;
	}

	@Override
	public FileUtilResultDto deleteDir(Path filePath) {
		FileUtilResultDto fileUtilResultDto = new FileUtilResultDto();
		List<Path> failedPaths = new ArrayList<Path>();
		fileUtilResultDto.setOk(true);

		try {
			FileSystemUtils.deleteRecursively(filePath);
		} catch (Exception e) {
			System.out.println("e: " + e);
			failedPaths.add(filePath);
			fileUtilResultDto.setOk(false);
		}

		fileUtilResultDto.setFailedPaths(failedPaths);

		return fileUtilResultDto;
	}

	@Override
	public List<Path> getFilePaths(Path dirPath) throws IOException {
		List<Path> filePaths = new ArrayList<Path>();
		try (Stream<Path> stream = Files.list(dirPath)) {
			filePaths = stream.filter(file -> !Files.isDirectory(file)).sorted().collect(Collectors.toList());
		}

		return filePaths;
	}

	public Path getStaticFilePath(String filePath) {
		return Paths.get(new ClassPathResource(globalConfig.getStaticDirPath()).getPath() + filePath);
	}
}
