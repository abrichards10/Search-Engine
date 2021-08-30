import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A thread-safe version of {@link InvertedIndex} using a read/write lock.
 *
 * @author CS 212 Software Development
 * @author angelarichards261
 * @version Spring 2021
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;

	/**
	 * Initializes a thread-safe indexed set.
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new SimpleReadWriteLock();
	}

	@Override
	public void addElement(String word, String path, int pos) {
		lock.writeLock().lock();
		try {
			super.addElement(word, path, pos);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(String[] words, String name) {
		lock.writeLock().lock();
		try {
			super.addAll(words, name);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(String[] words, int start, String name) {
		lock.writeLock().lock();
		try {
			super.addAll(words, start, name);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override // I can't believe this was the problem how tf did I forget this
	public void addAll(InvertedIndex index){
		lock.writeLock().lock();
		try {
			super.addAll(index);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public List<Results> search(Set<String> terms, boolean matches) {
		lock.readLock().lock();
		try {
			return super.search(terms, matches);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean contains(String word) { 
		lock.readLock().lock();
		try {
			return super.contains(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();
		try {
			return super.contains(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean contains(String word, String location, Integer position) {
		lock.readLock().lock();
		try {
			return super.contains(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void printIndex(String index) throws IOException {
		lock.readLock().lock();
		try {
			super.printIndex(index);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void toJSON(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.toJSON(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void countsToJSON(Path counts) throws IOException {
		lock.readLock().lock();
		try {
			super.countsToJSON(counts);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int indexSize() {
		lock.readLock().lock();
		try {
			return super.indexSize();
		}
		finally {
			lock.readLock().unlock();
		}	
	}
	
	@Override
	public Map<String, Integer> getCount() {
		lock.readLock().lock();
		try {
			return super.getCount();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
		
	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}
}
