package raytracer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Film {
	int[][] picture;
	String name;
	
	public Film(int width, int height, String name) {
		picture = new int[height][width];
		this.name = name;
	}
	void commit(Sample samp, Color c) {
		int col = (int) (samp.getX() * picture[0].length);
		int row = picture.length - ((int) (samp.getY() * picture.length)) - 1;
		int red = (int) (c.getR() * 255);
		int green = (int) (c.getG() * 255);
		int blue = (int) (c.getB() * 255);
		picture[row][col] = (255 << 24 ) | (red << 16 ) | (green <<  8) | blue;
	}
	void writeImage() {
		
		BufferedImage image = ImageExport.convertRGBImageWithHeader(picture,"Iteration: 1");
		try {
			ImageExport.exportImageToFile(name,image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
   
