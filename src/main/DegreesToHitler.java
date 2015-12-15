package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

import tree.Tree;
import tree.Tree.Node;

public class DegreesToHitler {
	
	private static ArrayList<Node<String>> path;
	
	private static String alreadyVisited = "";
	static Wiki wiki = new Wiki("en.wikipedia.org");
	
	private static Tree dataBase;

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		
		wiki.setThrottle(0);
		
		path = new ArrayList<Node<String>>();
	
		try {
			wiki.login("DegreesOfWiki", "5orless");
		} catch (FailedLoginException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wiki.setResolveRedirects(true);
		
		dataBase = new Tree<String>("Adolf Hitler");
		
		String[] linksToHitler = wiki.whatLinksHere("Adolf Hitler");
		
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
						System.out.print(path.get(i).getData() + " > ");
					}
					System.out.println();
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
		
		if (searchDataBase(degrees, root, page, links)){
			return true;
		} else {
			
		}
	
		
		if (contains(links, (String) root.getData())){
			path.add(new Node<String>(page, root));
			path.add(root);
			return true;
		} else if (degrees == 1){
			return false;
		} else {
			
			List<Node<String>> rootChildren = root.getChildren();
			
			for (int i = 0; i < rootChildren.size() && !found; i++){
				if (contains(links, rootChildren.get(i).getData())){
					path.add(new Node<String>(page, rootChildren.get(i)));
					path.add(rootChildren.get(i));
					path.add(root);
					found = true;
				}
			}
			
			for (int i = 0; i < links.length && !found; i++){
				if (!alreadyVisited.contains("~" + links[i] + "~")){
					if (findHitler(links[i], degrees - 1)){
						path.add(0, new Node<String>(page, rootChildren.get(i)));
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
		if (degrees == 0){
			return false;
		}
		
		if (degrees == originalDegrees){
			if(contains(links, root.getData())){
				path.add(new Node<String>(page, root));
				path.add(root);
				return true;
			} else {
				return searchDataBase(degrees-1, originalDegrees, root, page, links);
			}
		} else if (degrees == originalDegrees - 1){
			List<Node<String>> children = root.getChildren();
			
			for (int i = 0; i < children.size(); i++){
				searchDataBase(degrees, degrees, children.get(i), page, links);
			}
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
	
	private static String[] clean(String[] links){
		
		for (int i = 0; i < links.length; i++){
			if (links[i].contains("Wikipedia:")){
				links = Arrays.copyOf(links, i);
			}
		}
		return links;
		
	}
	
	
	

}
