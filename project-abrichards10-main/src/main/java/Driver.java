import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Angela Richards
 * @version Spring 2021
 */
public class Driver {
	
	/**
	 * Text flag stored in a more readable, usable String
	 */
	private static final String TEXT_FLAG = "-text";
	
	/**
	 * Index flag stored in a more readable, usable String
	 */
	private static final String INDEX_FLAG = "-index";

	/**
	 *  Query flag stored in a more readable, usable String
	 */
	private static final String QUERY_FLAG = "-query";
	
	/**
	 *  Exact flag stored in a more readable, usable String
	 */
	private static final String EXACT_FLAG = "-exact";
	
	/**
	 *  Results flag stored in a more readable, usable String
	 */
	private static final String RESULTS_FLAG = "-results";

	/**
	 * Counts flag stored in a more readable, usable String
	 */
	private static final String COUNTS_FLAG = "-counts";

	/**
	 *  Results default output in a more readable, usable String
	 */
	private static final Path RESULTS_DEFAULT = Path.of("results.json");
	
	/**
	 * Index default stored in a more readable, usable String
	 */
	private static final Path INDEX_DEFAULT = Path.of("index.json");
	
	/**
	 * Count flag default stored in a more readable, usable String
	 */
	private static final Path COUNTS_DEFAULT = Path.of("counts.json");
	
	/**
	 * Threads flag
	 */
	private static final String THREADS_FLAG = "-threads"; 
	
	/**
	 * If a number is missing or invalid, default to 5 threads
	 */
	private static final int DEFAULT_THREADS = 5;
	
	/**
	 * Create a new Logger to use
	 */
	private static final Logger log = LogManager.getLogger();

	/**
	 * HTML flag
	 */
	private static final String HTML_FLAG = "-html";
	
	/**
	 * Max flag
	 */
	private static final String MAX_FLAG = "-max";
	
	/**
	 * Server flag
	 */
	private static final String SERVER_FLAG = "-server";
	
	/** The hard-coded port to run this server. */
	public static final int PORT = 8080;
	
	/**
	 * Takes in an argument and builds the inverted index from that argument
	 * @param args argument we are given
	 * @throws Exception if exception occurs
	 */
	public static void main(String[] args) throws Exception {
		// Yes I know I'm not supposed to do this ^ I'll fix it later
		
		Instant start = Instant.now();
		InvertedIndex invertedIndex; 
		ArgumentParser argument = new ArgumentParser(args);
		
		//**** Multithreaded *****// 
		int threads = 5; 
		QueryParseFileInterface query;
		InvertedIndexBuilder indexBuilder; 
		WorkQueue queue = null;
		ThreadSafeInvertedIndex threadSafe = new ThreadSafeInvertedIndex();
		
		/** Web Crawler **/
		WebCrawler crawler;
		
		URL seed; 
		int max = 3;
		
		/** Servlet Stuff*/
		SearchServlet servlet;
				
		if(argument.hasFlag(THREADS_FLAG) || argument.hasFlag(HTML_FLAG) || argument.hasFlag(SERVER_FLAG)) {
			log.info(argument);
		
			try {
				threads = Integer.parseInt(argument.getString(THREADS_FLAG, "5"));
				
				if(threads <= 0) {
					threads = DEFAULT_THREADS;
				}
			}
			catch(NumberFormatException e) {
				System.out.println("NumberFormatException");
				threads = DEFAULT_THREADS;
			}
			
			queue = new WorkQueue(threads);
			invertedIndex = threadSafe;
			indexBuilder = new ThreadSafeInvertedIndexBuilder(threadSafe, queue);
			query = new ThreadSafeQueryParseFile(threadSafe, queue);
			crawler = new WebCrawler(threadSafe, queue, max);
		}
		else {
			invertedIndex = new InvertedIndex();
			indexBuilder = new InvertedIndexBuilder(invertedIndex);
			query = new QueryParseFile(invertedIndex);
			crawler = null;
		}
		
		if(argument.hasFlag(HTML_FLAG)) {
			 						
			try {
				seed = new URL(argument.getString(HTML_FLAG));
				
				if(argument.hasFlag(MAX_FLAG)) {
					max = Integer.parseInt(argument.getString(MAX_FLAG, "50"));
				}
				
				System.out.println("Before traversing\n");
				try {
					crawler.traverseDirectory(seed, max);	
					System.out.println(max);
										
				} catch (MalformedURLException e) {
					System.out.println("MalformedURLException");
				} catch (URISyntaxException e) {
					System.out.println("URISyntaxException");
				}
								
				System.out.println("\nAfter traversing");
				
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException");
				max = 1;
			}
		}
		
		/**
		 * We also need to handle all other occurrences
		 * So we need -html seed (seed is the seed URL the web crawler should
		 * initially crawl to build the index)
		 * We also need to transfer over HtmlFetcher, HtmlCleaner, and use
		 * LinkParser to parse links
		 * 
		 * We also need a web crawler class that is multithreaded and synchronized
		 */
		
		/**
		 * Okay now I'm going to work on displaying everything
		 */			
		
		if(argument.hasFlag(SERVER_FLAG)) {
			servlet = new SearchServlet(query, threadSafe, crawler);
//			
//			System.setProperty("org.eclipse.jetty.LEVEL", "DEBUG");
//
			Server server = new Server(PORT);
//
			ResourceHandler resourceHandler = new ResourceHandler();
			resourceHandler.setDirectoriesListed(true);
//
			resourceHandler.setResourceBase("./src");
//
			ServletHandler servletHandler = new ServletHandler();
			
			servletHandler.addServletWithMapping(new ServletHolder(servlet), "/search");
//
			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { resourceHandler, servletHandler });
//
			server.setHandler(handlers);
//			server.setHandler(servletHandler);
			server.start();
			server.join();
		}
		
		log.debug("Thread count: ", threads);
		log.info("Started");
		
		if(argument.hasFlag(TEXT_FLAG) && argument.getPath(TEXT_FLAG) != null) {
			Path path = argument.getPath(TEXT_FLAG);
			
			try {
				indexBuilder.traverseDirectory(path);
			}
			catch (IOException e) {
				System.out.println("Unable to traverse directory");
			}
		}
		
		if (argument.hasFlag(INDEX_FLAG)) {
			Path path = argument.getPath(INDEX_FLAG, INDEX_DEFAULT);
			
			try {
				invertedIndex.toJSON(path);
			}
			catch (IOException e) {
				log.error("Unable to write the index to path: " + path);
			}
		}
		
		if(argument.hasFlag(COUNTS_FLAG)) {
			Path counts = argument.getPath(COUNTS_FLAG, COUNTS_DEFAULT);
			try {
				invertedIndex.countsToJSON(counts);
			}
			catch (IOException e){
				System.out.println("Unable to output path for counts");
				System.out.println("Path arguments: " + counts);
			}
		}
		
		if(argument.getPath(QUERY_FLAG) != null) {
			Path path = argument.getPath(QUERY_FLAG);
			
			try {
				query.parseQueriesInFile(path, argument.hasFlag(EXACT_FLAG));
			}
			catch (IOException e) {
				System.out.println("Path for index could not be read");
			}
		}
		
		if(argument.hasFlag(RESULTS_FLAG)) {
			Path results = argument.getPath(RESULTS_FLAG, RESULTS_DEFAULT);
			try {
				query.queryToJSON(results);
			}
			catch (IOException e) {
				System.out.println("Unable to write index file output: ");
				System.out.println("Path arguments: " + argument.getString(TEXT_FLAG));
			}
		}
				
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
		
		if(queue != null) {
			queue.shutdown();
		}
	}
}