package utilities;

import java.awt.Graphics2D;

public class Button {

	private int x;
	private int y;
	private int width;
	private int height;
	private String text;

	public Button(int x, int y, int width, int height, String text){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
	}

	public boolean containsPoint(int x, int y){
		if (x >= this.x && x <= (this.x + this.width)){
			if( y >= this.y && y <= (this.y + this.height)){
				return true;
			}
		}

		return false;

	}

	public void draw(Graphics2D g){
		int stringWidth = g.getFontMetrics().stringWidth(text);
		int stringHeight = g.getFontMetrics().getHeight();
		g.drawString(text, (width/2) - (stringWidth/2) + x , y + (height/2) + (stringHeight/2));
		
		g.drawRect(x, y, width, height);
	}

	public void setText(String text){
		this.text = text;
	}



}
