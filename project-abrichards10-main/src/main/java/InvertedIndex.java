import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

/**
 * Processes all text files in a directory
 * @version Spring 2021
 * @author angelarichards261
 */
public class InvertedIndex {

	/** The Map which stores the invertedIndex **/
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;
	
	/** The count we store in a TreeMap **/ 
	private final TreeMap<String, Integer> countMap; 

	/**
	 * Constructor that initializes both Maps
	 */
	public InvertedIndex() { 
		invertedIndex = new TreeMap<>();
		countMap = new TreeMap<>(); 
	}
	
	/**
	 * Adds an element to a position, and relates it to a path
	 * @param word the word we are adding
	 * @param path the path it will be linked to
	 * @param pos the position it goes in
	 */
	public void addElement(String word, String path, int pos) { 
		invertedIndex.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		invertedIndex.get(word).putIfAbsent(path, new TreeSet<Integer>());
		boolean modified = invertedIndex.get(word).get(path).add(pos);

		if(modified) {
			countMap.putIfAbsent(path, 0);
			countMap.put(path, countMap.get(path) + 1);
		}
	}

	/**
	 * Adds all the words at once, as long 
	 * as the first word in the array is at the first position
	 * @param words the words to add
	 * @param name filename of the origin of the words
	 */
	public void addAll(String[] words, String name) {
		addAll(words, 1, name);
	}
	
	/**
	 * Adds all the words at once, as long
	 * as the first word in the array is at the starting position
	 * @param words the words to add
	 * @param start where to start
	 * @param name the filename of the origin of the words
	 */
	public void addAll(String[] words, int start, String name) {
		for(int i = 0; i < words.length; i++) {
			addElement(words[i], name, start);
			start++;
		}
	}

	/**
	 * Searches for Results that match the inverted index 
	 * and returns a list of (sorted) matches
	 * @param terms cleaned and stemmed search terms
	 * @param matches if it matches exactly, return true
	 * @return a sorted list of Results
	 */
	public List<Results> search(Set<String> terms, boolean matches) {
		Map<String, Results> searchResultMap = new HashMap<>();
		ArrayList<Results> results = new ArrayList<>();
		
		if(matches) {
			exactSearch(terms, searchResultMap, results);
		} else {
			partialSearch(terms, searchResultMap, results);
		}
		
		Collections.sort(results);
		return results;
	}
	
	/**
	 * Creates a map of Results of searchTerms 
	 * that match exactly from the inverted index
	 * @param terms the cleaned and stemmed search terms
	 * @param searchResults the Results Map
	 * @param results the list of results 
	 */
	private void exactSearch(Set<String> terms, Map<String, Results> searchResults, ArrayList<Results> results) {
		for(String searchTerm : terms) {
			if(invertedIndex.containsKey(searchTerm)) {
				searchInput(searchTerm, searchResults, results);
			}
		}
	}

	/**
	 * Creates a map of Results of searchTerms 
	 * that match partially from the inverted index
	 * @param terms the cleaned and stemmed search terms
	 * @param searchResults the Results Map
	 * @param results the list of results 
	 */
	private void partialSearch(Set<String> terms, Map<String, Results> searchResults, ArrayList<Results> results) {
		for(String searchTerm : terms) {
			for(String matchedTerm : invertedIndex.tailMap(searchTerm).keySet()) {
				if(!matchedTerm.startsWith(searchTerm)) {
					break;
				}
				searchInput(matchedTerm, searchResults, results);
			}
		}
	}
	
	/**
	 * Creates a map of Results with either an exact
	 * or partial match from the inverted index
	 * @param terms the cleaned and stemmed search terms
	 * @param searchResults the Results Map
	 * @param results the list of results
	 */
	private void searchInput(String terms, Map<String, Results> searchResults, ArrayList<Results> results) {
		for(String location : invertedIndex.get(terms).keySet()) {
			if (!searchResults.containsKey(location)) {
				Results result = new Results(terms, location);
				results.add(result);
				searchResults.put(location, result);
			} else {
				searchResults.get(location).update(terms);
			}
		}
	}

	/**
	 * Returns true if the invertedIndex contains the key
	 * @param word the String key we use
	 * @return true if the invertedIndex contains the word
	 */
	public boolean contains(String word) {
		return invertedIndex.containsKey(word);
	}
	
	/**
	 * Returns true if a map paired with a key word in invertedIndex contains
	 * the location of the String
	 * @param word the word we are testing 
	 * @param location the location we are testing on the element
	 * @return true if it contains a specified location
	 */
	public boolean contains(String word, String location) {
		return contains(word) && invertedIndex.get(word).containsKey(location);
	}
	
	/**
	 * Return true if a set paired with a key location in a 
	 * map contains the map paired with the key word in invertedIndex
	 * contains the position
	 * @param word the word to be tested
	 * @param location the location to be tested on invertedIndex
	 * @param position the position to be tested on the element
	 * @return true if the invertedIndex contains the position
	 */
	public boolean contains(String word, String location, Integer position) {
		return contains(word, location) && invertedIndex.get(word).get(location).contains(position);
	}
	
	/**
	 * Prints the index as a doubleNested object 
	 * @param index the index we are getting the path from
	 * @throws IOException if an IO Exception occurs
	 */
	public void printIndex(String index) throws IOException { 
		JSONWriter.asDoubleNested(invertedIndex, Path.of(index));
	}
	
	/**
	 * Write the data structure to 
	 * the given path 
	 * @param path where to write
	 * @throws IOException if IOException occurs
	 */
	public void toJSON(Path path) throws IOException {
		JSONWriter.asObject(invertedIndex, path);
	}
	
	/**
	 * Write the counts data structure to 
	 * the given path 
	 * @param counts where to write
	 * @throws IOException if IOException occurs
	 */
	public void countsToJSON(Path counts) throws IOException {
		JSONWriter.asObject(countMap, counts);
	}
	
	/**
	 * Returns the size of the invertedIndex
	 * (the number of words inside it)
	 * @return number of words
	 */
	public int indexSize() {
		return invertedIndex.size();
	}
	
	/**
	 * Returns the count
	 * @return count
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(countMap);
	}
	
	/**
	 * Gets an unmodifiable Set of the invertedIndex locations
	 * @param word the word we search for
	 * @return an unmodifiable set of the locations
	 */
	public Set<String> getLocations(String word) {
		if (contains(word)) {
			return Collections.unmodifiableSet(invertedIndex.get(word).keySet());
		}
		return Collections.emptySet();
	}
	
	/**
	 * Gets an unmodifiable Set of the invertedIndex positions
	 * @param word the word we search for
	 * @param location the location of the word
	 * @return an unmodifiable set of the positions
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (contains(word, location)) {
			return Collections.unmodifiableSet(invertedIndex.get(word).get(location));
		}
		return Collections.emptySet();
	}

	/**
	 * Inner Class that gives us the results of the location of the 
	 * String, the count, and the score
	 * @author angelarichards261
	 */
	public class Results implements Comparable<Results>, JSONObject {
			
		/**
		 * Key with a quoted text value that is the relative 
		 * path to the text file matching one or more of the 
		 * query words
		 */
		private final String where;
		
		/**
		 * Stores the count
		 */
		private long count;
		
		/**
		 * Stores the score
		 */
		private double score;
		
		/**
		 * Results constructor that initializes
		 * location, count and score
		 * @param words the words we are storing
		 * @param where the location of the String
		 */
		public Results(String words, String where) {
			this.where = where;
			this.count = 0;
			this.score = 0.0;
			update(words);
		}
	
		/**
		 * Updates the count by adding the size
		 * of the location of the word
		 * @param word the word we use
		 */
		private void update(String word) {
			this.count += invertedIndex.get(word).get(where).size();
			this.score = (double) count / countMap.get(where);
		}
		
		/**
		 * Returns where the String is
		 * @return the location of the String
		 */
		public String getWhere() {
			return where;
		}
		
		/**
		 * Returns the count
		 * @return the count
		 */
		public long getCount() {
			return count;
		}
		
		/**
		 * Returns the score
		 * @return the score
		 */
		public double getScore() {
			return score;
		}
		
		@Override
		public String toString() {
			return this.toJSONString(0);
		}

		@Override
		public int compareTo(Results results) {
			int temp;
			if ((temp = -Double.compare(this.score, results.score)) == 0) {
				if ((temp = -Long.compare(this.count, results.count)) == 0) {
					return this.where.compareToIgnoreCase(results.where);
				}
			}
			return temp;
		}
		
		@Override
		public void toJSONObject(Writer writer, int times) throws IOException {
			String formatted = "%.8f";
			
			writer.write("{\n");
			JSONWriter.indent(writer, times + 2);
			writer.write("\"where\": ");
			JSONWriter.quote(where, writer);
			writer.write(",\n");
			JSONWriter.indent(writer, times + 2);
			writer.write("\"count\": ");
			writer.write(Long.toString(count));
			writer.write(",\n");
			JSONWriter.indent(writer, times + 2);
			writer.write("\"score\": ");
			writer.write(String.format(formatted, score));
			writer.write('\n');
			JSONWriter.indent(writer, times + 1);
			writer.write('}');
		}
	}
	
	/**
	 * addAll method that merges inverted indices
	 * @param index the index we use
	 */
	public void addAll(InvertedIndex index) {
		Set<String> indexKey = index.invertedIndex.keySet();
		Set<String> countKey = index.countMap.keySet();
		
		for(String key : indexKey) {
			if(!invertedIndex.containsKey(key)) {
				invertedIndex.put(key, index.invertedIndex.get(key));
			}
			else {
				for(String path : index.invertedIndex.get(key).keySet()) {
					if(invertedIndex.get(key).containsKey(path)) {
						invertedIndex.get(key).get(path).addAll(index.invertedIndex.get(key).get(path));
					}
					else {
						invertedIndex.get(key).put(path, index.invertedIndex.get(key).get(path));
					}
				}
			}
		}
		for (String key : countKey) {
			int count = index.countMap.get(key) + this.countMap.getOrDefault(key, 0);
			this.countMap.put(key, count);
		}
	}
	
	@Override
	public String toString() {
		return invertedIndex.toString();
	}
}