
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for finding all text files in a directory using lambda
 * functions and streams.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class TextFileFinder {
	
	/**
	 * A lambda function that returns true if the path is a file that ends in a
	 * .txt or .text extension (case-insensitive). Useful for
	 * {@link Files#walk(Path, FileVisitOption...)}.
	 *
	 * @see Files#isRegularFile(Path, java.nio.file.LinkOption...)
	 * @see Path#getFileName()
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static final Predicate<Path> IS_TEXT = path ->
	{
		String lowerCaseFile = path.getFileName().toString().toLowerCase();
		return Files.isRegularFile(path) && (lowerCaseFile.endsWith(".txt") || lowerCaseFile.endsWith(".text"));
	};	
	
	/**
	 * If provided a directory, returns a stream of all text files within that
	 * directory. Follows any symbolic links encountered. If provided a file,
	 * returns a stream containing only that file regardless of its extension.
	 *
	 * @param start the initial path to start with
	 * @param keep function that determines whether to keep a file
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #IS_TEXT
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static Stream<Path> find(Path start, Predicate<Path> keep) throws IOException {
		return Files.walk(start, FileVisitOption.FOLLOW_LINKS).filter(keep); 
	};

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #find(Path, Predicate)
	 * @see #IS_TEXT
	 */
	public static Stream<Path> find(Path start) throws IOException {
		return Files.find(start, Integer.MAX_VALUE, (path, attribute) -> IS_TEXT.test(path), FileVisitOption.FOLLOW_LINKS);
	}

	/**
	 * Returns a list of text files using streams.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #find(Path)
	 * @see Collectors#toList()
	 */
	public static List<Path> list(Path start) throws IOException {
		return find(start).collect(Collectors.toList());
	}
}