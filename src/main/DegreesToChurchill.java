//Cole Adams
//CS 30 William Aberhart High School
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import javax.security.auth.login.FailedLoginException;
import javax.swing.JPanel;

import fastily.jwiki.core.NS;
import fastily.jwiki.core.Wiki;
import tree.Tree;
import utilities.Button;

public class DegreesToChurchill extends JPanel implements Runnable, MouseListener, ActionListener{

	//dimensions
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 700;

	//app thread
	private Thread thread;
	private boolean running;
	private int FPS;
	private long targetTime;
	private Finder finder;

	//drawing states
	private volatile boolean loading;
	private volatile boolean saving;
	private volatile boolean finishedSaving;
	private volatile boolean found;

	//Graphics
	private Graphics2D g;
	private BufferedImage image;
	private Image loadingGif;
	private Font titleFont;
	private Font churchillFont;
	private Font buttonFont;

	//utilies
	private TextField textBox;
	private Button[] buttons = {new Button(WIDTH - 150, 100, 100, 100, "FIND", new Color(242, 236, 188), new Color(245, 182, 89)),
			new Button(50, HEIGHT - 150, WIDTH - 100, 100, "SAVE DATABASE", new Color(242, 236, 188), new Color(245, 182, 89))};
	private final int FIND_BUTTON = 0;
	private final int SAVE_BUTTON = 1;

	//found path
	private volatile ArrayList<String> path;

	public DegreesToChurchill(){  // Game constructor
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();

		FPS = 30;
		targetTime = 1000/FPS;
		setBounds(0, 0, WIDTH, HEIGHT);
		finder = new Finder();

		loading = true;
		saving = false;
		finishedSaving = false;
		found = false;

		textBox = new TextField(30);
		setLayout(null);
		textBox.setBounds(50, 100, WIDTH - 200, 100);
		textBox.setVisible(false);
		textBox.setEditable(true);
		add(textBox);

		path = new ArrayList<String>();
	}

	private void init(){

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		running = true;

		try {	//Loads loading gif
			Toolkit tk = Toolkit.getDefaultToolkit();
			loadingGif = tk.createImage("src/resources/loader.gif");
			tk.prepareImage(loadingGif, -1, -1, null);
		} catch(Exception e) {
		}
	}

	public void addNotify(){	// declares parent status and adds listeners
		super.addNotify();
		if(thread == null){
			thread = new Thread(this);
			addMouseListener(this);
			textBox.addActionListener(this);
			thread.start();
		}
	}

	public void run(){ // runs game
		init();

		long start, elapsed, wait; //Variables to keep track of game's run times

		finder.setTask(Finder.INIT);
		finder.start();

		while (running){
			start = System.nanoTime();

			if (loading){
				drawLoading();
			} else {
				draw();
			}
			drawToScreen();

			elapsed = System.nanoTime() - start;
			wait = targetTime - elapsed/100000;

			if (wait < 0){
				wait = 0;
			}

			try {
				Thread.sleep(wait);
			}
			catch(Exception e){
			}
		}


	}

	private void drawToScreen(){ //scales and draws game with formating
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		g2.dispose();
	}

	private void draw(){
		super.paintComponent(g);

		g.setColor(new Color(242, 236, 188)); //background
		g.fillRect(0, 0, WIDTH, HEIGHT);

		//title
		g.setFont(titleFont);
		g.setColor(new Color(92, 75, 81));
		g.drawString("Degrees to", 50, 80);
		g.setFont(churchillFont);
		g.setColor(new Color(241, 98, 92));
		g.drawString("Churchill", 600, 80);

		//buttons
		g.setFont(buttonFont);
		for (Button b : buttons){
			b.draw(g);
		}

		//bottom third of the screen
		if (found && !saving){
			for (int i = 0; i < path.size(); i++){
				
				int size = path.size();
				
				g.setFont(buttonFont);

				//width and height of the information rectangles
				double width = ((WIDTH - 100 - ((size - 1) * 30))/size);
				double height = HEIGHT - 450;

				//draws information rectangles
				g.setColor(new Color(138, 190, 178));
				g.fillRect((int) ((width * i) + 50 + (i * 30)), 250, (int)width, (int)height);

				//draws arrows between the information cards
				if (i != size - 1){

					g.setColor(new Color(241, 98, 92));

					int[] xPoints = {(int) (((width * i) + 50 + (i * 30)) + width + 5), 
							(int) ((width * i) + 50 + (i * 30) + width + 5), 
							(int) ((width * i) + 50 + (i * 30) + width + 20)};
					int[] yPoints = {(int) (250 + (height/2) - 5), 
							(int) (250 + (height/2) + 5), 
							(int) (250 + (height/2))};

					g.fillPolygon(xPoints, yPoints, 3);
				}
				
				if (i >= path.size()) {
					i--;
				}

				String[] str = path.get(i).split(" ");

				//finds longest word in the title
				double stringWidth = g.getFontMetrics().stringWidth(str[0]);

				for (int x = 1; x < str.length; x++){
					if (g.getFontMetrics().stringWidth(str[x]) > stringWidth){
						stringWidth = g.getFontMetrics().stringWidth(str[x]);
					}
				}

				//adjusts font size to fit the longest word on one line
				if (stringWidth > (width - 20)){
					double ratio = (width - 20)/stringWidth;

					g.setFont(buttonFont.deriveFont((float) (buttonFont.getSize() * ratio)));
				}

				g.setColor(Color.BLACK);

				String output = "";
				int lines = 0;

				//starts a new line if the string extends past the end of the card
				for (int x = 0; x < str.length; x++){

					if (g.getFontMetrics().stringWidth(output + str[x]) <= (width - 18)){
						output+= (output.equals("")?"":" ") + str[x];
					} else {
						g.drawString(output, (int) ((width * i) + (30 * i) + 60), 300 + (lines * g.getFontMetrics().getHeight()));
						output = str[x];
						lines++;
					}
				}

				g.drawString(output, (int) ((width * i) + (30 * i) + 60), 300 + (lines * g.getFontMetrics().getHeight()));
			}
		} else if (saving){
			// while saving
			g.setFont(titleFont);
			g.setColor(new Color(92, 75, 81));

			g.drawString("Saving...", (WIDTH/2) - (g.getFontMetrics().stringWidth("Saving...")/2),
					250 + (g.getFontMetrics().getHeight()/2));

			g.drawImage(loadingGif, (WIDTH/2) - (loadingGif.getWidth(null)/2), 250 + (g.getFontMetrics().getHeight()/2) + 30, null);
		} else if (finishedSaving) {
			//when finished saving
			g.setFont(titleFont);
			g.setColor(new Color(92, 75, 81));

			g.drawString("Saved", (WIDTH/2) - (g.getFontMetrics().stringWidth("Saved")/2),
					250 + (g.getFontMetrics().getHeight()/2));
		}

		textBox.setVisible(true);
	}

	private void drawLoading(){ //loading animation
		super.paintComponent(g);

		g.setColor(new Color(242, 236, 188));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.drawImage(loadingGif, (WIDTH/2) - (loadingGif.getWidth(null)/2), (HEIGHT/2) - (loadingGif.getHeight(null)/2), null);

		textBox.setVisible(false);
	}

	public void mouseClicked(MouseEvent e){
		for (int i = 0; i < buttons.length; i++){
			if (buttons[i].containsPoint(e.getX(), e.getY())){
				select(i);
			}
		}
	}

	public void mousePressed(MouseEvent e){
	}

	public void mouseReleased(MouseEvent e){
	}

	public void mouseEntered(MouseEvent e){
	}

	public void mouseExited(MouseEvent e){
	}

	private void select(int button){ // button actions
		if (button == FIND_BUTTON){
			finder.setPageToFind(textBox.getText());
			finder.setTask(Finder.FIND);
			finder.start();
		} else if (button == SAVE_BUTTON){
			saving = true;
			finder.setTask(Finder.SAVE);
			finder.start();
		}
	}

	public void actionPerformed(ActionEvent arg0) { //search on enter
		finder.setPageToFind(textBox.getText());
		finder.setTask(Finder.FIND);
		finder.start();
	}

	private class Finder implements Runnable {

		//Thread actions
		public static final int FIND = 0;
		public static final int SAVE = 1;
		public static final int INIT = 2;

		//current task
		private int task;

		//Finding thread
		public Thread t;

		//wiki
		private Wiki wiki;
		private String alreadyVisited;
		private String pageToFind;

		//database
		private Tree<String> dataBase;

		public Finder(){   //finder constructor

			wiki = new Wiki("en.wikipedia.org");
			alreadyVisited = "";

			pageToFind = "";
		}

		public void init() throws IOException, ClassNotFoundException{ // initializes most graphical items && files so the loading graphic can be displayed

			//fonts
			try {
				File fontFile = new File("src/resources/Montserrat-Bold.ttf");
				titleFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, 90f);

				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(titleFont);
			} catch(Exception e) {
				titleFont = new Font("SansSerif", Font.BOLD, 48);
			}

			textBox.setFont(titleFont);

			try {
				File fontFile = new File("src/resources/MotionPicture_PersonalUseOnly.ttf");
				churchillFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.BOLD, 90f);

				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(churchillFont);
			} catch(Exception e) {
				churchillFont = new Font("SansSerif", Font.BOLD, 48);
			}

			try {
				File fontFile = new File("src/resources/Montserrat-Regular.ttf");
				buttonFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, 36f);

				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(buttonFont);
			} catch(Exception e) {
				buttonFont = new Font("SansSerif", Font.BOLD, 48);
			}


			wiki.login("DegreesOfWiki", "5orless");

			//loading database
			File tmpFile = new File("src/databases/WinstonChurchill.txt");

			if (tmpFile.exists()){

				FileInputStream fileIn = new FileInputStream("src/databases/WinstonChurchill.txt");
				ObjectInputStream inFile = new ObjectInputStream(fileIn);
				dataBase = (Tree<String>) inFile.readObject();
				inFile.close();
				fileIn.close();
			} else { // if data base doesnt exist

				tmpFile.createNewFile();

				dataBase = new Tree<String>("Winston Churchill");

				ArrayList<String> linksToChurchill = wiki.whatLinksHere("Winston Churchill", false);

				for (int i = 0; i < linksToChurchill.size(); i++){
					dataBase.addNode(linksToChurchill.get(i), "Winston Churchill");
				}
			}

			loading = false;
		}

		public void start(){ //starts thread
			if (t == null){
				t = new Thread(this);
				t.setDaemon(true);
				t.start();
			}
		}

		@Override
		public void run() { // runs operation
			found = true;
			if (task == FIND){
				find(pageToFind);
			} else if (task == SAVE){
				try {
					save();
				} catch (IOException e) {
				}
			} else if (task == INIT){
				try {
					init();
				} catch (ClassNotFoundException e) {
				} catch (IOException e) {
				}
			}
			t = null;
		}

		private void find(String firstPage){

			path.clear();
			
			firstPage = wiki.resolveRedirect(firstPage);

			try {
				findChurchill(firstPage, 5);
			} catch (IOException e) {
			}

			if (!path.isEmpty()){ //checks if a path was found
				found = true;

				for (int i = path.size() - 2; i >= 0; i--){ // adds nodes to database
					if (!dataBase.contains(path.get(i))){
						dataBase.addNode(path.get(i), path.get(i + 1));
					}
				}
			} else {
				found = false;
			}

			alreadyVisited = "";



		}

		public boolean findChurchill(String page, int degrees) throws IOException{

			path.add(page); 

			if (degrees == 0){
				path.remove(path.size() - 1);
				return false;
			}

			boolean found = false;

			ArrayList<String> links = wiki.getLinksOnPage(page, NS.MAIN);

			links = wiki.filterByNS(links, NS.MAIN); //removes links not in the default namespace

			if (searchDataBase(degrees, page, links)){ //checks if the page is already in the database
				return true;
			}

			if (degrees == 1){
				path.remove(path.size() - 1); //No link to churchill here means this is not a path
				return false;
			} else {

				alreadyVisited+="~" + page + "~"; //adds page to the already visited string

				for (int i = 0; i < links.size() && !found; i++){
					if (!alreadyVisited.contains("~" + links.get(i) + "~")){ //checks if the page has already been visited
						if (findChurchill(links.get(i), degrees - 1)){
							found = true;
						} else {
							alreadyVisited+="~" + links.get(i) + "~";
						}
					}
				}
				if (!found){
					path.remove(path.size() - 1);
				}
				return found;
			}

		}

		private boolean searchDataBase(int degrees, String page, ArrayList<String> links){

			boolean found = false;

			for (int i = 1; i <= degrees && !found; i++){
				ArrayList<String> databasePath = dataBase.findPath(page, i + 1); //returns a path if it does contain the page

				if (!databasePath.isEmpty()){
					for (int x = 0; x < databasePath.size(); x++){
						path.add(databasePath.get(x));
					}
					path.remove(page);
					found = true;
				}

				for (int j = 0; j < links.size() && !found && i!=1; j++){
					databasePath = dataBase.findPath(links.get(j), i); // returns a path if it finds the link

					if (!databasePath.isEmpty()){
						for (int x = 0; x < databasePath.size(); x++){
							path.add(databasePath.get(x));
						}
						found = true;
					}
				}
			}

			return found;
		}

		private void save() throws IOException{ //serializes and saves the database
			FileOutputStream fileOut = 
					new FileOutputStream("src/databases/WinstonChurchill.txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(dataBase);
			out.close();
			fileOut.close();

			saving = false;
			found = false;
			finishedSaving = true;
		}

		public void setPageToFind(String pageToFind) {
			this.pageToFind = pageToFind;
		}

		public void setTask(int task) {
			this.task = task;
		}

	}
	
	
	public static void main(String[] args) {
		DegreesToChurchill d = new DegreesToChurchill();
		Finder f = d.new Finder();
		
		try {
			f.init();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> randPages = f.wiki.getRandomPages(1000, NS.MAIN);
		int counter = 1;
		for (String s : randPages) {
			System.out.println("Finding page " + counter + " of 1000");
			f.find(s);
			counter++;
			if (counter % 50 == 0) {
				try {
					System.out.println("Saving");
					f.save();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(printPath(d.path));
		}
		System.out.println("Saving");
		try {
			f.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String printPath(ArrayList<String> str) {
		StringBuilder sb = new StringBuilder();
		
		for (String s : str) {
			sb.append(s);
			sb.append("->");
		}
		return sb.toString();
	}
}
