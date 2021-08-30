import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class that builds an inverted index that the Driver can output
 * @author angelarichards261
 */
public class InvertedIndexBuilder {
	
	/** The default Stemmer algorithm **/
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH; 
	
	/**
	 * Index used by the multithreaded version
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Inverted Index constructor (to be used in the multithreaded version) 
	 * @param invertedIndex the index we use
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}
	
	/**
	 * Build method that takes an invertedIndex, and an argument to parse,
	 * iterates through the sub paths, checks if the File is a file or directory 
	 * If it is a File, add the path, otherwise iterate through the elements again
	 * This time checking for .txt or .text before adding the path
	 * @param path the file path
	 * @throws IOException if IOException occurs
	 */
	public void traverseDirectory(Path path) throws IOException {
		try(Stream<Path> subPaths = Files.walk(path, FileVisitOption.FOLLOW_LINKS)) {
			var iterator = subPaths.iterator();

			if(Files.isRegularFile(path)) {
				addPath(path);
			}
			else {
				while(iterator.hasNext()) {
					var nextPath = iterator.next();
					
					if(isText(nextPath)) {
						addPath(nextPath);
					}	
				}
			}
		}
	}

	/**
	 * Helper method for traverseDirectory that
	 * determines if a file is a .txt or .text file
	 * @param nextPath the path we check
	 * @return a boolean that checks if the file is a .txt or .text
	 */
	public static boolean isText(Path nextPath) {
		String location = nextPath.toString().toLowerCase();
		return Files.isRegularFile(nextPath) && location.endsWith(".txt") || location.endsWith(".text");
	}
	
	/**
	 * Adds a path to the given file
	 * @param path the file we are adding the path to
	 * @throws IOException if IOException occurs
	 */	
	public void addPath(Path path) throws IOException {
		addPath(path, this.invertedIndex);
	}
	
	/**
	 * Adds a path to the given file
	 * @param file the file we are adding the path to
	 * @param index the inverted index 
	 * @throws IOException if an IO Exception occurs
	 */
	public static void addPath(Path file, InvertedIndex index) throws IOException { 
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
		
			String line = reader.readLine();
			int i = 0;
			
			while(line != null) {
				String[] parsedLine = TextParser.parse(line);
					
				for(String words : parsedLine) {
					String data = stemmer.stem(words).toString();
					index.addElement(data, file.toString(), ++i);
				}
				line = reader.readLine();
			}
		}
	}
}