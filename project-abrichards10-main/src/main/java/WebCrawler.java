import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Web crawler class which holds the same
 * logic as the build methods --> as well as 
 * an inner Runnable Tasks class
 * @author angelarichards261
 */
public class WebCrawler {
	
	/** Initialize a thread-safe inverted index */
	ThreadSafeInvertedIndex invertedIndex;
	
	/** WorkQueue we need to build the inverted index from a seed URL */
	private final WorkQueue queue;
	
	/** Stores the maximum number of urls */
	private int max;
	
	/** Object used to lock our code */
	private final ArrayList<URL> lock;
	
	/**
	 * Constructor that initializes our work queue
	 * @param invertedIndex the index we use
	 * @param queue the single WorkQueue we use for our WebCrawler
	 * @param max the maximum number 
	 */
	public WebCrawler(ThreadSafeInvertedIndex invertedIndex, WorkQueue queue, int max) {
		this.invertedIndex = invertedIndex;
		this.queue = queue;
		this.lock = new ArrayList<>();
		this.max = 0;
	}
	
	/**
	 * Build method that takes in a URL to crawl through 
	 * We want a FINITE crawl, which means we need a max 
	 * number of urls to go through
	 * @param url the URL we are using
	 * @param max the maximum number of URLs we can use
	 * @throws URISyntaxException if URISyntaxException occurs
	 * @throws MalformedURLException if MalformedURLException occurs
	 */
	public void traverseDirectory(URL url, int max) throws MalformedURLException, URISyntaxException {
		this.max += max;
		lock.add(url);
		queue.execute(new WebCrawlerTasks(url));
		queue.finish();
	}
	
	/**
	 * Inner Tasks class that implements Runnable in order
	 * to hold our run method
	 * @author angelarichards261
	 */
	private class WebCrawlerTasks implements Runnable {

		/** The String of HTML we need to clean and parse */
		private final URL url;
		
		/**
		 * Constructor needed to call tasks
		 * @param url the url we are crawling through
		 */
		public WebCrawlerTasks(URL url) {
			this.url = url;
		}
		
		@Override
		public void run() {
						
			String html = HtmlFetcher.fetch(url, 10);	
			html = HtmlCleaner.stripBlockElements(html);
			
			ArrayList<URL> validLinks = LinkParser.getValidLinks(url, html);
					
			if(html == null) {
				 return;
			}

			synchronized(lock) {
				for (URL match : validLinks) {			
				
					if(lock.size() < max && !lock.contains(match)) {
						lock.add(match);
						queue.execute(new WebCrawlerTasks(match));
					}
				}
			}	
			
			ThreadSafeInvertedIndex index = new ThreadSafeInvertedIndex();
			
			String cleaned = HtmlCleaner.stripHtml(html);
			Stemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			String[] parsed = TextParser.parse(cleaned);
			int count = 1;
					
			for(String word : parsed) {
				String stemmed_words = stemmer.stem(word).toString();
				index.addElement(stemmed_words, url.toString(), count);
				count++;
			}
			invertedIndex.addAll(index);	
		} 
	}
}