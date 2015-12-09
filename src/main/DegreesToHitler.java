package main;

import java.io.IOException;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

public class DegreesToHitler {

	public static void main(String[] args) throws IOException {
		Wiki wiki = new Wiki("en.wikipedia.org");
		
		wiki.setThrottle(0);
	
		try {
			wiki.login("DegreesOfWiki", "5orless");
		} catch (FailedLoginException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] links = wiki.getLinksOnPage("Adolf Hitler");
		
		for (String link : links){
			System.out.println(link);
		}
		
		wiki.logout();
		

	}

}
