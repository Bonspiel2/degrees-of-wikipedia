package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

import tree.Tree;
import tree.Tree.Node;

public class DegreesToHitler {
	
	private static ArrayList<String> path;
	
	private static String alreadyVisited = "";
	static Wiki wiki = new Wiki("en.wikipedia.org");
	
	private static Tree dataBase;

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		
		wiki.setThrottle(0);
		//wiki.setLogLevel(Level.OFF);
		
		path = new ArrayList<String>();
	
		try {
			wiki.login("DegreesOfWiki", "5orless");
		} catch (FailedLoginException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wiki.setResolveRedirects(true);
		
		dataBase = new Tree<String>("Adolf Hitler");
		
		String[] linksToHitler = wiki.whatLinksHere("Adolf Hitler", Wiki.MAIN_NAMESPACE);
		
		for (int i = 0; i < linksToHitler.length; i++){
			Node<String> root = dataBase.getRoot();
			root.addChild(linksToHitler[i]);
		}
		
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
					
					Node<String> child = dataBase.getRoot().findChild(path.get(path.size() - 2));
					
					
					for (int i = path.size() - 3; i >= 0; i--){
						if (!child.hasChild(path.get(i))){
							child.addChild(path.get(i));
							System.out.println("hi");
						}
						child = child.findChild(path.get(i));
					}
				} else {
					System.out.println("This page does not exist");
				}
				
				path.clear();
				alreadyVisited = "";
				
				System.out.println(dataBase.getRoot().findChild("Federalism").hasChild("Canada"));
			
			}
		}
		
//		for (String link : links){
//			System.out.println(link);
//		}
		
		
		wiki.logout();
		

	}
	
	public static boolean findHitler(String page, int degrees) throws IOException{
		if (degrees == 0){
			return false;
		}
		
		boolean found = false;
		
		String[] links = wiki.getLinksOnPage(page);
		
		links = clean(links);
		
		Node<String> root = dataBase.getRoot();
		
		if (searchDataBase(degrees, degrees, root, page, links)){
			return true;
		}
	
		if (degrees == 1){
			return false;
		} else {
			
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
	
	private static boolean searchDataBase(int degrees, int originalDegrees, Node<String> root, String page, String[] links){
		
		if (degrees < 0){
			return false;
		}
		
		int difference = originalDegrees - degrees;
		
//		if (degrees == 0){
//			return false;
//		}
		
		if (difference == 0){
			
			if(root.getData().equals(page)){
				path.add(0,root.getData());
				return true;
			} else {
				return searchDataBase(degrees-1, originalDegrees, root, page, links);
			}
			
		} else if (difference == 1){

			if (contains(links, root.getData())){
				path.add(0, root.getData());
				path.add(0, page);
				return true;
			} else {
				return searchDataBase(degrees-1, originalDegrees, root, page, links);
			}
				
		} else if (difference >= 2){
			List<Node<String>> children = root.getChildren();
			
			boolean found = false;
			
			for (int i = 0; i < children.size() && !found; i++){
				found = searchDataBase(degrees, degrees, children.get(i), page, links);
			}
			
			if (found){
				path.add(root.getData());
			}
			return found;
		}
		return false;
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
