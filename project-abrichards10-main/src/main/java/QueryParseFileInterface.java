import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An interface to hold the default methods
 * for the QueryParseFiles
 * @author angelarichards261
 *
 */
public interface QueryParseFileInterface {

	/**
	 * Holds resusable methods for both
	 * the single-threaded and multithreaded versions
	 * @param input the path we take in
	 * @param match the boolean that determines if it matches exactly
	 * @throws IOException if IOException occurs
	 */
	public default void parseQueriesInFile(Path input, boolean match) throws IOException{
		if(Files.isDirectory(input)) {
			throw new IOException("Wrong file for query path");
		}
		try(BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
			String line; 
			while((line = reader.readLine()) != null) {
				parseQuery(line, match);
			}
		}
	}
	
	/**
	 * Holds the parseQuery method 
	 * @param query the query we use
	 * @param match the boolean that determines if it matches exactly
	 */
	public void parseQuery(String query, boolean match);

	/**
	 * Holds the queryToJSON method
	 * @param path the path we take in
	 * @throws IOException if IOException occurs
	 */
	public void queryToJSON(Path path) throws IOException;
}