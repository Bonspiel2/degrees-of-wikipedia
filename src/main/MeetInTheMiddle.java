package main;

import java.io.IOException;

import org.wikipedia.Wiki;

public class MeetInTheMiddle {
	private String goal;
	private String start;
	
	private Wiki wiki;
	
	public MeetInTheMiddle(String goal, Wiki wiki) {
		this.goal = goal;
		this.wiki = wiki;
	}
	
	public String find(String page) throws IOException {
		String[] links = wiki.getLinksOnPage(page);
		
		for (String s : links) {
			
		}
		
		
		return goal;
		
	}

}
