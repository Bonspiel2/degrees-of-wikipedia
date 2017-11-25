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
		Queue<Page> s = new LinkedList<Page>();
		ArrayList<Page> g = new ArrayList<Page>();
		ArrayList<String> alreadyChecked = new ArrayList<String>();

		int levelTotal = 1;
		int nextLevelTotal = 0;

		int endLayerTotal = 1;
		int nextEndLayerTotal = 0;

		s.add(new Page(page));
		alreadyChecked.add(page);
		g.add(new Page(goal));

		boolean found = false; 

		while(!found) {
			Page curPage = s.poll();
			levelTotal--;
			//Checks to see if we have a link to the goal from the current page
			for (Page p : g) {
				if (curPage.equals(p)) {
					found = true;
					return print(curPage, p);
				}
			}

			//Adds all linked pages to Queue
			String[] linkP = wiki.getLinksOnPage(curPage.title);
			for (String link : linkP) {
				if (!link.contains(":") && !alreadyChecked.contains(link)) {
					s.add(new Page(link, curPage, null));
					alreadyChecked.add(link);
					nextLevelTotal++;
				}
			}

			//Adds pages another layer of linked pages
			if (levelTotal == 0) {
				levelTotal = nextLevelTotal;
				nextLevelTotal = 0;
				//If not first iteration
				if (!(nextEndLayerTotal == 0)) {
					//Removes irrelevant layer
					for (int i = 0; i < endLayerTotal; i++) {
						g.remove(0);
					}
					endLayerTotal = nextEndLayerTotal;
				}

				//Adds next layer to end list
				int counter = 0;
				ArrayList<Page> newPages = new ArrayList<Page>();
				for (Page p : g) {
					String[] prevLayer = wiki.whatLinksHere(p.getTitle(), Wiki.MAIN_NAMESPACE);

					for (String newP : prevLayer) {
						newPages.add(new Page(newP, null, p));
						counter++;
					}
				}

				nextEndLayerTotal = counter;
				g.addAll(newPages);
			}

		}


		return goal;

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
			System.out.println(hitler.find("Europe"));
		} catch (IOException ioe) {

		}
	}

}
