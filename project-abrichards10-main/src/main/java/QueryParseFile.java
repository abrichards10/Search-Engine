import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


/**
 * This class implements the BuildQueryInterface 
 * in order to read data into a query, determine what 
 * type of search to take, search, then write the results to a file
 * @author angelarichards261
 */
public class QueryParseFile implements QueryParseFileInterface {
	
	/**
	 * Map that stores our query entries
	 */
	private final Map<String, List<InvertedIndex.Results>> map;
	
	/**
	 * Index used to search for queries
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Default SnowballStemmer algorithm
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT_LANG = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Constructor that initializes map and invertedIndex
	 * @param invertedIndex the index used
	 */
	public QueryParseFile(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
		map = new TreeMap<>();
	}
		
	/**
	 * Adds a query into both the index and the query map
	 * 
	 * @param query the String of search terms
	 * @param match returns true if matches exactly 
	 */
	@Override
	public void parseQuery(String query, boolean match) {
		Set<String> usedPhrases = TextFileStemmer.uniqueStems(query);
		if (usedPhrases.isEmpty()) {
			return;
		}
		String lineFinal = String.join(" ", usedPhrases);
		if (map.containsKey(lineFinal)) {
			return;
		}
		map.put(lineFinal, invertedIndex.search(usedPhrases, match));
	}
	
	/**
	 * Writes a query path to JSON file
	 * @param path path to write
	 * @throws IOException if IOException occurs
	 */
	@Override
	public void queryToJSON(Path path) throws IOException {
		JSONWriter.asObject(map, path); 
	}
}