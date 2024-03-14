package com.copystagram.api.global.file;

import java.nio.file.Path;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUtilResultDto {
	public boolean isOk;
	public List<Path> failedPaths;
}
