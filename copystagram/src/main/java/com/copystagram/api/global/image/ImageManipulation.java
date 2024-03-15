package com.copystagram.api.global.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

@Component
public class ImageManipulation {
	public BufferedImage cropSquare(InputStream is) throws IOException {
		BufferedImage rawImage = ImageIO.read(is);

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

		return resultImage;
	}

	public BufferedImage resize(BufferedImage inputImage, int wantedLength) {
		Image imageInstance = inputImage.getScaledInstance(wantedLength, wantedLength, Image.SCALE_SMOOTH);
		BufferedImage resultImage = new BufferedImage(wantedLength, wantedLength, BufferedImage.TYPE_INT_RGB);
		resultImage.getGraphics().drawImage(imageInstance, 0, 0, null);

		return resultImage;
	}
}
