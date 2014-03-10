package raytracer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Film {
	int[][] picture;
	public Film(int width, int height) {
		picture = new int[height][width];
	}
	void commit(Sample samp, Color c) {
		int col = picture[0].length - (int) (samp.getX() * picture[0].length) - 1;
		int row = (int) (samp.getY() * picture.length);
		int red = (int) (c.getR() * 255);
		int green = (int) (c.getG() * 255);
		int blue = (int) (c.getB() * 255);
		picture[row][col] = (255 << 24 ) | (red << 16 ) | (green <<  8) | blue;
	}
	void writeImage() {
		
		BufferedImage image = ImageExport.convertRGBImageWithHeader(picture,"Iteration: 1");
		try {
			ImageExport.exportImageToFile("test2.jpg",image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
