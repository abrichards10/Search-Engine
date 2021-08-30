import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class that stems lines 
 * @author angelarichards261
 */
public class TextFileStemmer {
		
	/** Default algorithm we are storing **/
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer) {
		ArrayList<String> wordList = new ArrayList<String>();
		stemLine(line, stemmer, wordList);
		line.toLowerCase();
		
		return wordList;
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a list of cleaned and stemmed words
	 */
	public static ArrayList<String> listStems(String line) {
		return listStems(line, new SnowballStemmer(DEFAULT));
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 */
	public static ArrayList<String> listStems(Path inputFile) throws IOException {
		ArrayList<String> stemmed = new ArrayList<String>();
		
		try(BufferedReader readLine = Files.newBufferedReader(inputFile);) {
			String line = null;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);

			while((line = readLine.readLine()) != null) {
				stemLine(line, stemmer, stemmed);
			}
		}
		return stemmed;
	}

	/**
	 * Helper method that stems a line from a Collection
	 * of Strings
	 * 
	 * @param line the line we stem
	 * @param stemmer the Stemmer we use
	 * @param stems the Collection of stemmed lines
	 */
	public static void stemLine(String line, Stemmer stemmer, Collection<String> stems) {
		String[] words = TextParser.parse(line);
		for (String word : words) {
			String stem = stemmer.stem(word).toString();
			stems.add(stem);
		}
	}
	
	/**
	 * Gives us a list of unique stems from a line
	 * 
	 * @param line the line to return
	 * @return a set of uniqueStems with the default algorithm
	 */
	public static TreeSet<String> uniqueStems(String line) {
		return uniqueStems(line, new SnowballStemmer(DEFAULT));
	}
	
	/**
	 * Ultimately puts all stemmed words in a set
	 * Creates an array to hold parsed lines
	 * Then cycles through that array adding the stemmed words to the Set
	 * 
	 * @param line the line we are stemming
	 * @param stemmer the Stemmer we use
	 * @return a Set of stemmed words
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> stemmed = new TreeSet<String>(); 
		stemLine(line, stemmer, stemmed);
		return stemmed;
	}
	
	/**
	 * Stems the lines of a file line by line
	 * 
	 * @param inputFile the file we are stemming
	 * @return a Set of strings that have been stemmed from the file
	 * @throws IOException if an IO Exception occurs
	 */
	public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		TreeSet<String> stemmedWords = new TreeSet<>();
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line = reader.readLine();

			while (line != null) {
				stemLine(line, stemmer, stemmedWords);
				line = reader.readLine();
			}			
		}
		return stemmedWords;
	}	
}