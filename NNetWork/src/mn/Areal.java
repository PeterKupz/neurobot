package mn;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Areal {

	public double[][] food;

	public Areal(int width, int height) {
		food = new double[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				food[i][j] = Math.random()/10;
			}
		}
	}
	
	
	public Areal(String s) {
		
		BufferedImage b = null;
		try {
			b = ImageIO.read(new File(s));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		food = new double[b.getWidth()][b.getHeight()];
		for (int i = 0; i < b.getWidth(); i++) {
			for (int j = 0; j < b.getHeight(); j++) {
				food[i][j] = (new Color(b.getRGB(i, j))).getBlue()*.001;
			}
		}
	}

	public boolean in(Bot bot) {
		return bot.x >5 && bot.y >5 && bot.x< food.length - 5 && bot.y < food[0].length - 5;
	}

	public void returnFood(Bot bot, double mass) {
//		food[(int)Math.round(bot.x+2*Math.random())][(int)Math.round(bot.y+2*Math.random())]+=mass;
		food
		[Math.max(Math.min(x()-1,(int)Math.round(bot.x + 50*(.5-Math.random()))),0)]
		[Math.max(Math.min(y()-1,(int)Math.round(bot.y + 50*(.5-Math.random()))),0)]+=mass;
	}
	
	public int x() {
		return food.length;
	}
	
	public int y() {
		return food[0].length;
	}

	public void renew() {
		for (int i = 0; i < x(); i++) {
			for (int j = 0; j < y(); j++) {
				food[i][j] = Math.random()/15;
			}
		}
		
	}
	
}
