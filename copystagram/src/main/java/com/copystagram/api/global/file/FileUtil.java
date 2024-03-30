package com.copystagram.api.global.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface FileUtil {
	public FileUtilResultDto write(MultipartFile input, Path filePath);

	public FileUtilResultDto write(byte[] input, Path filePath);

	public FileUtilResultDto write(Map<Path, byte[]> fileInfoMap);

	public FileUtilResultDto delete(Path filePath);

	public FileUtilResultDto delete(Path[] filesPaths);

	public FileUtilResultDto deleteDir(Path dirPath);

	public List<Path> getFilePaths(Path dirPath) throws IOException;
}
