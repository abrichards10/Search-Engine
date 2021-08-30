import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class implements the BuildQueryInterface 
 * in order to read data into a query, determine what 
 * type of search to take, search, then write the results to a file
 * @author angelarichards261
 */
public class ThreadSafeQueryParseFile implements QueryParseFileInterface {
	
	/**
	 * Initialize the logger
	 */
	public static final Logger log = LogManager.getLogger();
	
	/**
	 * Create a new lock from our SimpleLock 
	 */
	private final TreeMap<String, List<InvertedIndex.Results>> lock;
	
	/**
	 * Keeps track of the number of threads
	 */
	private final WorkQueue queue;
	
	/**
	 * Declares the index we will use
	 */
	private final ThreadSafeInvertedIndex invertedIndex;
	
	/**
	 * Constructor that initializes the index, lock and number of threads
	 * @param invertedIndex the index
	 * @param queue the WorkQueue we initialize
	 */
	public ThreadSafeQueryParseFile(ThreadSafeInvertedIndex invertedIndex, WorkQueue queue) {
		this.invertedIndex = invertedIndex;
		this.queue = queue; 
		this.lock = new TreeMap<>();
	}

	@Override
	public void parseQueriesInFile(Path input, boolean match) throws IOException {
		QueryParseFileInterface.super.parseQueriesInFile(input, match);
		queue.finish();
	}	
	
	@Override
	public void parseQuery(String query, boolean match) {
		queue.execute(new QueryParseFileTasks(query, match));
	}
	
	@Override
	public void queryToJSON(Path path) throws IOException {
		synchronized(lock) {
			JSONWriter.asObject(lock, path); 
		}
	}
	
	/**
	 * Inner class to deal with the query tasks 
	 * @author angelarichards261
	 */
	private class QueryParseFileTasks implements Runnable {
		
		/**
		 * The line to search
		 */
		private final String query;
		
		/**
		 * Determines if the line matches exactly 
		 */
		private final boolean matches;
		
		/**
		 * Constructor for inner Tasks class
		 * @param query holds the String of phrases to search
		 * @param matches returns true if the result matches exactly
		 */
		public QueryParseFileTasks(String query, boolean matches) {
			this.query = query;
			this.matches = matches; 
		}
		
		@Override
		public void run() {
			Set<String> usedPhrases = TextFileStemmer.uniqueStems(query);
			String lineFinal = String.join(" ", usedPhrases);
			
			synchronized(lock) {
				if (usedPhrases.isEmpty()) {
					return;
				}
				if (lock.containsKey(lineFinal)) {
					return;
				}
			}
			var local = invertedIndex.search(usedPhrases, matches);
			
			synchronized(lock) {
				lock.put(lineFinal, local);
			}
		}
	}
}