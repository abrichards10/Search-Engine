import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Class that cleans, splits and parses text
 * @author angelarichards261
 */
public class TextParser {

	/** Splits text into words by whitespace **/
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");

	/** Removes special characters from text **/
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");
	
	/**
	 * Cleans the text
	 * @param line the line of text to clean
	 * @return a cleaned version of the text toLowerCase()
	 */
	private static String clean(String line) {
		String cleaned = Normalizer.normalize(line, Normalizer.Form.NFD);
		cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
		return cleaned.toLowerCase();
	}

	/**
	 * Splits the text given a line
	 * @param line the line we are splitting up
	 * @return an array of Strings that have been split
	 */
	private static String[] split(String line) {
		return line.isBlank() ? new String[0] : SPLIT_REGEX.split(line.strip());
	}
	
	/**
	 * Parses the given line
	 * @param line the line to parse
	 * @return a line that has been cleaned and split
	 */
	public static String[] parse(String line) {
		return split(clean(line));
	}
}