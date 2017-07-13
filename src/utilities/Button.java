package utilities;

import java.awt.Color;
import java.awt.Graphics2D;

public class Button {

	private int x;
	private int y;
	private int width;
	private int height;
	private String text;
	private Color textColor;
	private Color buttonColor;

	public Button(int x, int y, int width, int height, String text, Color textColor, Color buttonColor){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.textColor = textColor;
		this.buttonColor = buttonColor;
	}
	
	public Button(int x, int y, int width, int height, String text){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.textColor = Color.WHITE;
		this.buttonColor = Color.BLACK;
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
		
		g.setColor(buttonColor);
		g.fillRect(x, y, width, height);
		
		g.setColor(textColor);
		g.drawString(text, (width/2) - (stringWidth/2) + x , y + (height/2) + (stringHeight/2));
	}

	public void setText(String text){
		this.text = text;
	}



}
