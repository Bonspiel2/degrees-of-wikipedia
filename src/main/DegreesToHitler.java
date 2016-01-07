package main;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

import javax.security.auth.login.FailedLoginException;
import javax.swing.JPanel;

import org.wikipedia.Wiki;

import tree.Tree;

public class DegreesToHitler extends JPanel implements Runnable, KeyListener, MouseListener, MouseWheelListener, MouseMotionListener{
	
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 700;
	
	private Thread thread;
	private boolean running;
	private int FPS;
	private long targetTime;
	
	private BufferedImage image;
	private Graphics2D g;
	
	
	private ArrayList<String> path;
	
	private String alreadyVisited;
	private Wiki wiki;
	
	private static Tree<String> dataBase;
	
	public DegreesToHitler(){
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		FPS = 30;
		targetTime = 1000/FPS;
		
		path = new ArrayList<String>();
		alreadyVisited = "";
		
		wiki = new Wiki("en.wikipedia.org");
		
	}
	
	private void init() throws IOException, ClassNotFoundException{
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
		
		wiki.setThrottle(0);
		//wiki.setLogLevel(Level.OFF);
	
		try {
			wiki.login("DegreesOfWiki", "5orless");
		} catch (FailedLoginException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wiki.setResolveRedirects(true);
		
		File tmpFile = new File("src/databases/AdolfHitler.txt");
		
		if (tmpFile.exists()){
		
			FileInputStream fileIn = new FileInputStream("src/databases/AdolfHitler.txt");
			ObjectInputStream inFile = new ObjectInputStream(fileIn);
			dataBase = (Tree<String>) inFile.readObject();
			inFile.close();
			fileIn.close();
		} else {
			
			tmpFile.createNewFile();
		
			dataBase = new Tree<String>("Adolf Hitler");
			
			String[] linksToHitler = wiki.whatLinksHere("Adolf Hitler", Wiki.MAIN_NAMESPACE);
			
			for (int i = 0; i < linksToHitler.length; i++){
				dataBase.addNode(linksToHitler[i], "Adolf Hitler");
			}
		}
	}
	
	public void run(){
		try {
			init();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long start, elapsed, wait;
		
		
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Scanner in = new Scanner(System.in);
		
		boolean running = true;
		
		while (running){
			System.out.println("Enter a page to start from: ");
			String firstPage = in.nextLine();
			
			if (firstPage.equals("~exit")){
				running = false;
			} else {
			
				//String[] links = wiki.getLinksOnPage(firstPage);
				
				//links = clean(links);
				
				findHitler(firstPage, 5);
				
				if (!path.isEmpty()){
					for (int i = 0; i < path.size(); i++){
						System.out.print(path.get(i) + " > ");
					}
					System.out.println();
					
					
					
					for (int i = path.size() - 2; i >= 0; i--){
						if (!dataBase.contains(path.get(i))){
							dataBase.addNode(path.get(i), path.get(i + 1));
						}
					}
				} else {
					System.out.println("This page does not exist");
				}
				
				path.clear();
				alreadyVisited = "";
			
			}
		}
		
//		for (String link : links){
//			System.out.println(link);
//		}
		
		
		FileOutputStream fileOut = 
		new FileOutputStream("src/databases/AdolfHitler.txt");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(dataBase);
		out.close();
		fileOut.close();
		
		wiki.logout();
		

	}
	
	public static boolean findHitler(String page, int degrees) throws IOException{
		if (degrees == 0){
			return false;
		}
		
		boolean found = false;
		
		String[] links = wiki.getLinksOnPage(page);
		
		links = clean(links);
		
		if (searchDataBase(degrees, page, links)){
			return true;
		}
	
		if (degrees == 1){
			return false;
		} else {
			
			alreadyVisited+="~" + page + "~";
			
			for (int i = 0; i < links.length && !found; i++){
				if (!alreadyVisited.contains("~" + links[i] + "~")){
					if (findHitler(links[i], degrees - 1)){
						path.add(0, page);
						found = true;
					} else {
						alreadyVisited+="~" + links[i] + "~";
					}
				}
			}
			
			return found;
		}
		
	}
	
	private static boolean searchDataBase(int degrees, String page, String[] links){
		
		boolean found = false;
		
		for (int i = 1; i <= degrees && !found; i++){
			ArrayList<String> path = dataBase.findPath(page, i + 1);
			
			if (!path.isEmpty()){
				DegreesToHitler.path = path;
				found = true;
			}
			
			for (int j = 0; j < links.length && !found && i!=1; j++){
				path = dataBase.findPath(links[j], i);
				
				if (!path.isEmpty()){
					path.add(0, page);
					DegreesToHitler.path = path;
					found = true;
				}
			}
		}
		
		return found;
	}
	
	private static boolean contains(String[] links, String page){
		for (String link : links){
			if (link.equals(page)){
				return true;
			}
		}
		return false;
	}
	
	private static String[] clean(String[] links) throws IOException{
		
		for (int i = 0; i < links.length; i++){
			if (wiki.namespace(links[i]) != Wiki.MAIN_NAMESPACE){
				links = Arrays.copyOf(links, i);
			}
		}
		return links;
		
	}
	
	
	

}
