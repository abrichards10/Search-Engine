import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter; 
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author angelarichards261
 * @author University of San Francisco
 * @version Spring 2021
 */

public class JSONWriter extends InvertedIndex {
		
	/**
	 * Writes the elements as a pretty JSON array
	 * 
	 * @param <V> the element type
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if IO exception occurs
	 */
	public static <V> void asArray(Collection<V> elements, Writer writer, int level) throws IOException {
		indent(writer, level);
		asSingleArray(elements, writer, level);
		writer.write('\n');
	}
	
	/**
	 * Writes a set of elements as a JSON array
	 * to the given path
	 * 
	 * @param <V> the element type
	 * @param elements the elements to write
	 * @param path the file path
	 * @throws IOException if IOException occurs
	 */
	public static <V> void asArray(Collection<V> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}
	
	/**
	 * Returns the elements as a JSON array
	 *
	 * @param elements the elements to write
	 * @param <V> the element type stored in elements
	 * @return a String containing the elements
	 */
	public static <V> String asArray(Collection<V> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			System.out.println("Could not write JSON array");
			return null;
		}
	}
	
	/**
	 * Writes arrays as a nested or single array
	 * 
	 * @param <V> the element type
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if IOException occurs
	 */
	private static <V> void asSingleArray(Collection<V> elements, Writer writer, int level) throws IOException {
		writer.write('[');
		Iterator<?> iterator = elements.iterator();

		if (iterator.hasNext()) {
			writer.write('\n');
			asArrayVariable(iterator.next(), writer, level);
		}

		while (iterator.hasNext()) {
			writer.write(',');
			writer.write('\n');
			asArrayVariable(iterator.next(), writer, level);
		}

		writer.write('\n');
		indent("]", writer, level);
	}
	
	/**
	 * Writes the elements value
	 * 
	 * @param <V> the element type
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the level of indentation
	 * @throws IOException if IOException occurs
	 */
	private static <V> void asArrayVariable(V element, Writer writer, int level) throws IOException {
		indent(writer, level + 1);
		asVariable(element, writer, level);
	}

	/**
	 * Returns the elements as a pretty JSON object
	 * 
	 * @param elements the elements to use
	 * @return a String containing the elements in pretty JSON format
	 */	
	public static String asObject(Map<?, ?> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			System.out.println("Could not write JSON object");
			return null;
		}
	}
	
	/**
	 * Writes the elements as a pretty JSON object to the file path
	 * 
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<?, ?> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}
	
	/**
	 * Writes the elements as a pretty JSON object
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<?, ?> elements, Writer writer, int level) throws IOException {
		indent(writer, level);
		asSingleObject(elements, writer, level);
		writer.write('\n');
	}

	/**
	 * Adds the elements in a NestedObject 
	 * 
	 * @param elements the elements in the Map
	 * @param path the path we create for a new BufferedWriter
	 * @throws IOException if IO Exception occurs
	 */
	public static void asNestedObject(Map<String, TreeSet<Integer>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}
		
	/**
	 * Writes the elements as a NestedObject
	 * 
	 * @param elements the elements to return
	 * @return the elements as a String in NestedObject format
	 */
	public static String asNestedObject(Map<String, TreeSet<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Writes the elements as a pretty JSON nested object
	 * 
	 * @param elements the elements we are writing
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	private static void asNestedObject(Map<String, TreeSet<Integer>> elements, Writer writer, int level) throws IOException {
			
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");
		if(iterator.hasNext()) {
			String line = iterator.next();
			writer.write("\n");
			quote(line.toString(), writer, level+1);	
			Collection<Integer> nestedList = elements.get(line);
			asSingleArray(nestedList, writer, level);
		}
		
		while(iterator.hasNext()) {
			String line = iterator.next();
			writer.write(",\n");
			quote(line.toString(), writer, level+1);
			Collection<Integer> nestedList = elements.get(line);
			asSingleArray(nestedList, writer, level);

		}
		writer.write("\n");
		indent("}", writer, level);
	}
	
	/**
	 * Writes the elements as a Double Nested Object
	 *  
	 * @param elements the elements to write
	 * @throws IOException if an IOException occurs
	 * @return the writer as a String
	 */
	public static String asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) throws IOException {
		Writer writer = new StringWriter();
		int level = 0;
		asDoubleNested(elements, writer, level);
		return writer.toString();
	}
	
	/**
	 * Writes the elements as a pretty JSON double nested object
	 * Uses the asNestedObject function to create inner nested object
	 * 
	 * @param elements the elements we are writing
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	private static void asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer, int level) throws IOException {
		var iterator = elements.keySet().iterator();
		writer.write("{");
		if(iterator.hasNext()) {
			String key = iterator.next();
			writer.write("\n\t");
			writer.write('"' + key+ '"' + ": ");
			asNestedObject(elements.get(key), writer, level+1);
		}
		while(iterator.hasNext()) {
			String key = iterator.next();
			writer.write(",\n\t");
			writer.write('"' + key+ '"' + ": ");
			asNestedObject(elements.get(key), writer, level+1);
		}
		writer.write("\n");
		indent("}", writer, level-1);
	}
	
	/**
	 * Adds the objects in doubleNested
	 * 
	 * @param elements the elements in the Map
	 * @param path the path we create in a new BufferedWriter
	 * @throws IOException if an IO exception occurs
	 */
	public static void asDoubleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNested(elements, writer, 0);
		}
	}

	/**
	 * Helper method for writing the elements as 
	 * either a normal or nested JSON object
	 * 
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the level of indentation
	 * @throws IOException if IOException occurs
	 */
	private static void asSingleObject(Set<? extends Map.Entry<?, ?>> element, Writer writer, int level) throws IOException {
		var iterator = element.iterator();
		writer.write('{');

		if (iterator.hasNext()) {
			writer.write('\n');
			asSingleObjectVariable(iterator.next(), writer, level);
		}

		while (iterator.hasNext()) {
			writer.write(',');
			writer.write('\n');
			asSingleObjectVariable(iterator.next(), writer, level);
		}

		writer.write('\n');
		indent("}", writer, level);
	}
	
	/**
	 * Helper method for writing elements as a normal or 
	 * nested JSON object
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the level indentation
	 * @throws IOException if IOException occurs
	 */
	private static void asSingleObject(Map<?, ?> elements, Writer writer, int level) throws IOException {
		asSingleObject(elements.entrySet(), writer, level);
	}
	
	/**
	 * Helper method for writing an element with 
	 * the given name and value
	 * 
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the level of indentation
	 * @throws IOException if IOExecption occurs
	 */
	private static void asSingleObjectVariable(Map.Entry<?, ?> element, Writer writer, int level) throws IOException {
		quote(element.getKey().toString(), writer, level + 1);
		writer.write(": ");
		asVariable(element.getValue(), writer, level);
	}
		
	/**
	 * Helper method to write the value of an element
	 * 
	 * @param <V> the element type
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the initial level of indentation
	 * @throws IOException if IOException occurs
	 */
	private static <V> void asVariable(V element, Writer writer, int level) throws IOException {
		if (element instanceof Map<?, ?>) {
			asSingleObject((Map<?, ?>) element, writer, level + 1);
		} else if (element instanceof Collection<?>) {
			asSingleArray((Collection<?>) element, writer, level + 1);
		} else if (element instanceof String) {
			quote(element.toString(), writer);
		} else if (element instanceof JSONObject) {
			((JSONObject) element).toJSONObject(writer, level);
		} else {
			writer.write(element.toString());
		}
	}
	
	/**
	 * Writes a tab when called given the number of times
	 * 
	 * @param times the number of times iterated
	 * @return an indent 
	 */
	public static String indent(int times) {
		return times > 0 ? String.format("%" + (times*2) + "s", " ") : "";
	}
	
	/**
	 * Writes a tab when called, given the number of times we need a tab
	 * 
	 * @param writer the writer to use
	 * @param times the number of times iterated
	 * @throws IOException if an IO Exception occurs
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}
	
	/**
	 * Indents in between the given elements
	 * 
	 * @param element the individual element
	 * @param writer the writer to use
	 * @param times the number of times we iterate
	 * @throws IOException if an IO Exception occurs
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents the elements, then writes them out
	 * 
	 * @param element the element we indent
	 * @param writer the writer we use
	 * @param times the number of times we tab
	 * @throws IOException if IO Exception occurs
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Wraps quotes around an element
	 * 
	 * @param element the element we wrap
	 * @return the element in quotes
	 */
	public static String quote(String element) {
		return "\"" + element + "\"";
	}
	
	/**
	 * Wraps a quote around the elements
	 * 
	 * @param element the element to wrap
	 * @param writer the writer we use 
	 * @throws IOException if an IO Exception occurs
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}
	
	/**
	 * Indents the element and wraps a quote around it 
	 * 
	 * @param element the element we wrap and quote
	 * @param writer the writer we use
	 * @param times the number of times we tab
	 * @throws IOException if an IO Exception occurs
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		quote(element, writer);
	}
	
}