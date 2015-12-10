package main;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

public class DegreesToHitler {
	
	private static String path = "";
	
	private static String alreadyVisited = "";
	static Wiki wiki = new Wiki("en.wikipedia.org");
	
	private static String[] linksToHitler;

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		
		wiki.setThrottle(0);
	
		try {
			wiki.login("DegreesOfWiki", "5orless");
		} catch (FailedLoginException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		linksToHitler = wiki.whatLinksHere("Adolf Hitler");
		
		System.out.println("Enter a page to start from: ");
		String firstPage = in.nextLine();
		
		String[] links = wiki.getLinksOnPage(firstPage);
		
		links = clean(links);
		
		//findHitler(firstPage, 5);
		
		//System.out.println(path);
		
		for (String link : links){
			System.out.println(link);
		}
		
		
		wiki.logout();
		

	}
	
	public static boolean findHitler(String page, int degrees) throws IOException{
		if (degrees == 0){
			return false;
		}
		
		String[] links = wiki.getLinksOnPage(page);
		
		links = clean(links);
	
		
		if (contains(links, "Adolf Hitler")){
			path+= page + " > Adolf Hitler";
			return true;
		} else if (degrees == 1){
			return false;
		} else {
			String linkToClick = closeToHitler(links);
			
			if (!linkToClick.isEmpty()){
				path+= page + " >" + linkToClick + " > Adolf Hitler";
				return true;
			}
			for (int i = 0; i < links.length; i++){
				if (!alreadyVisited.contains("~" + links[i] + "~")){
					if (findHitler(links[i], degrees - 1)){
						path = page + " >" + path;
						return true;
					} else {
						alreadyVisited+="~" + links[i] + "~";
					}
				}
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
	
	private static String closeToHitler(String[] links){
		for (int i = 0; i < links.length; i++){
			if (contains(linksToHitler, links[i])){
				return links[i];
			}
		}
		return "";
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
