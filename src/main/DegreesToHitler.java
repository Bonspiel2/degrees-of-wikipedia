package main;

import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

public class DegreesToHitler {
	
	private static String path = "";
	
	private static String alreadyVisited = "";
	static Wiki wiki = new Wiki("en.wikipedia.org");

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		
		wiki.setThrottle(0);
	
		try {
			wiki.login("DegreesOfWiki", "5orless");
		} catch (FailedLoginException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Enter a page to start from: ");
		String firstPage = in.nextLine();
		
		String[] links = wiki.getLinksOnPage(firstPage);
		
		path = firstPage + " >";
		
		findHitler(firstPage, 5);
		
		System.out.println(path);
		
//		for (String link : links){
//			System.out.println(link);
//		}
		
		
		wiki.logout();
		

	}
	
	public static boolean findHitler(String page, int degrees) throws IOException{
		if (degrees == 0){
			return false;
		}
		
		String[] links = wiki.getLinksOnPage(page);
	
		
		if (contains(links, "Adolf Hitler")){
			path+= " Adolf Hitler";
			return true;
		} else {
			
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
	
	

}
