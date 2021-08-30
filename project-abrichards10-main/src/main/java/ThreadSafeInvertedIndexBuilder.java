import java.io.IOException;
import java.nio.file.Path;

/**
 * Thread safe inverted index that the Driver can output
 * 
 * @version Spring 2021
 * @author angelarichards261
 */
public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {
	
	/**
	 * Stores the number of threads
	 */
	private final WorkQueue queue;
		
	/**
	 * The index we traverse
	 */
	private final ThreadSafeInvertedIndex invertedIndex;
	
	/**
	 * Constructor for this thread-safe inverted index
	 * @param invertedIndex the index we initialize
	 * @param queue the WorkQueue we initialize
	 */
	public ThreadSafeInvertedIndexBuilder(ThreadSafeInvertedIndex invertedIndex, WorkQueue queue) {
		super(invertedIndex);
		this.invertedIndex = invertedIndex;
		this.queue = queue;
	}
	
	@Override
	public void traverseDirectory(Path path) throws IOException {
		super.traverseDirectory(path);
		queue.finish();
	}
	
	@Override
	public void addPath(Path file) throws IOException {
		queue.execute(new IndexBuilderTasks(file));
	}
	
	/**
	 * This class extends ThreadSafeInvertedIndexBuilder
	 * and implements Runnable as a way to run tasks
	 * @author angelarichards261
	 */
	public class IndexBuilderTasks implements Runnable {
	
		/**
		 * The file we are messing with
		 */
		private final Path file;
			
		/**
		 * Constructor that, when called, creates a Runnable to 
		 * add to the index
		 * @param file the path we are initializing
		 */
		public IndexBuilderTasks(Path file) { 
			this.file = file;
		}
		
		@Override
		public void run() {
			InvertedIndex index = new InvertedIndex();

			try {
				InvertedIndexBuilder.addPath(file, index);
			} catch (IOException e) {
				System.out.println("Cannot invoke run() method in ThreadSafeInvertedIndexBuilder");
			}
			invertedIndex.addAll(index);
		}
	}
}