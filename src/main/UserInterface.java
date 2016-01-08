package main;

import javax.swing.JFrame;

public class UserInterface {

	public static void main(String[] args) {
		
		JFrame window = new JFrame("DegreesToHitler");
		
		window.setContentPane(new DegreesToHitler());
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack(); 
		window.setVisible(true);

	}

}