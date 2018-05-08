package main;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

public class MeetInTheMiddle {
	//Left hand queue, right hand list
	//Hash map database? Potentially a tree


	private String goal;
	private String start;

	private Wiki wiki;

	public MeetInTheMiddle(String goal, Wiki wiki) {
		this.goal = goal;
		this.wiki = wiki;
	}

	public String find(String page) throws IOException{
		ArrayList<ArrayList<Page>> s = new ArrayList<ArrayList<Page>>();
		ArrayList<ArrayList<Page>> g = new ArrayList<ArrayList<Page>>();
		ArrayList<String> alreadyCheckedL = new ArrayList<String>();
		ArrayList<String> alreadyCheckedR = new ArrayList<String>();

		int currentLevel = 0;

		s.add(new ArrayList<Page>());
		s.get(0).add(new Page(page));

		alreadyCheckedL.add(page);

		g.add(new ArrayList<Page>());
		g.get(0).add(new Page(goal));

		alreadyCheckedR.add(goal);

		boolean found = false;
		boolean equal = true;

		while(!found) {
			ArrayList<Page> workingStartSet = s.get(currentLevel);
			ArrayList<Page> workingEndSet = g.get(0);

			//Checks to see if we have a link to the goal from the current page
			int startCounter = 0;
			
			for (Page start : workingStartSet) {
				int endCounter = 0;
				startCounter++;
				for (Page end : workingEndSet) {
					endCounter++;
					if (start.equals(end)) {
						found = true;
						return print(start, end);
					}
				}
			}



			if (equal) {
				//Adds all linked pages to next set
				currentLevel++;
				ArrayList<Page> nextLevel = new ArrayList<Page>();
				for (Page p : workingStartSet) {
					String[] linkP = wiki.getLinksOnPage(p.title);
					for (String link : linkP) {
						if (!link.contains(":") && !alreadyCheckedL.contains(link)) {
							nextLevel.add(new Page(link, p, null));
							alreadyCheckedL.add(link);
						}
					}
				}
				s.add(nextLevel);
				equal = false;
			} else {
				//Adds pages another layer of linked pages
				ArrayList<Page> nextLevel = new ArrayList<Page>();
				for (Page e : workingEndSet) {
					String[] prevLayer = wiki.whatLinksHere(e.getTitle(), Wiki.MAIN_NAMESPACE);

					for (String newP : prevLayer) {
						nextLevel.add(new Page(newP, null, e));
						alreadyCheckedR.add(newP);
					}
				}
				g.add(0,nextLevel);
				equal = true;
			}
		}
		
		return null;

	}


	public String print(Page s, Page e) {
		StringBuilder out = new StringBuilder();
		out.append(s.getTitle());
		while(s.getLinkFrom()!=null) {
			s = s.getLinkFrom();
			out.insert(0, "-");
			out.insert(0, s.getTitle());
		}
		while(e.getLinkTo()!=null) {
			e = e.getLinkTo();
			out.append("-");
			out.append(e.getTitle());
		}
		return out.toString();
	}


	private class Page{
		Page linkFrom;
		Page linkTo;
		String title;

		public Page(String title) {
			this.title = title;
			this.linkFrom = null;
			this.linkTo = null;
		}

		public Page(String title, Page linkFrom, Page linkTo) {
			this.title = title;
			this.linkFrom = linkFrom;
			this.linkTo = linkTo;
		}

		public Page getLinkFrom() {
			return linkFrom;
		}

		public void setLinkFrom(Page linkFrom) {
			this.linkFrom = linkFrom;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Page getLinkTo() {
			return linkTo;
		}

		public void setLinkTo(Page linkTo) {
			this.linkTo = linkTo;
		}

		public boolean equals(Page p) {
			return p.getTitle().equals(this.title);

		}
	}


	public static void main(String[] args) {
		//Loading wiki api

		Wiki wiki = new Wiki("en.wikipedia.org");

		wiki.setThrottle(0);
		wiki.setLogLevel(Level.OFF);

		try {
			wiki.login("DegreesOfWiki", "5orless");
		} catch (FailedLoginException | IOException e) {
			System.out.println("failed to login");
		}

		wiki.setResolveRedirects(true); 

		MeetInTheMiddle hitler = new MeetInTheMiddle("Adolf Hitler", wiki);

		try {
			System.out.println(hitler.find("Quinn"));
		} catch (IOException ioe) {

		}
	}

}
