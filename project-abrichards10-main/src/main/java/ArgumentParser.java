import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses arguments and checks for values and flags
 * @author angelarichards261
 */
public class ArgumentParser extends InvertedIndex {

	/** Map where we store the parsed elements **/
	private final Map<String, String> map;
		
	/** 
	 * Constructor that creates a new HashMap
	 */
	public ArgumentParser() {
		this.map = new HashMap<>();
	}
		
	/**
	 * When called, parses the arguments given
	 * @param args the argument(s) to parse
	 */
	public ArgumentParser(String[] args) {
		this();
		parse(args);
	}
	
	/**
	 * Returns the number of flags in the map
	 * @return the size of the map
	 */
	public int numFlags() {
		return map.size();
	}
		
	/**
	 * Checks if the map contains a flag
	 * @param flag the flag we are looking for
	 * @return true if the map contains the flag
	 */ 
	public boolean hasFlag(String flag) {
		return map.containsKey(flag);
	}
	
	/**
	 * Checks if an argument is a flag
	 * @param arg the argument we check
	 * @return true if the argument is a flag
	 */
	public boolean isFlag(String arg) {
		return arg != null && arg.startsWith("-") && arg.length() > 1;
	}
			
	/**
	 * Checks if an argument is a value
	 * @param arg argument we are checking
	 * @return true if the argument is a value 
	 */
	public boolean isValue(String arg) {
		return !isFlag(arg);
	}
	
	/**
	 * Checks if the flag has a value
	 * @param flag the flag we are checking
	 * @return true if the map contains flag as a key
	 */
	public boolean hasValue(String flag) {
		return map.get(flag) == null;
	}
		
	/**
	 * Gets the value of the flag from the map
	 * @param flag the flag we are checking
	 * @return the String flag from the map
	 */
	public String getValue(String flag) {
		return map.get(flag);
	}
	
	/**
	 * Gets the string from that particular flag
	 * @param flag the flag to check
	 * @return the flag from the map
	 */
	public String getString(String flag) {
		return map.get(flag);
	}

	/**
	 * Gets the string from the given flag, or returns a default value
	 * @param flag the flag we are given
	 * @param defaultValue the default String 
	 * @return the flag or DefaultValue if the value is null
	 */
	public String getString(String flag, String defaultValue) {
		String value = getString(flag);
		return value == null ? defaultValue : value;
	}
		
	/**
	 * Gets the path from the map given a flag
	 * @param flag that we get from the map
	 * @return the path of the flag if it exists
	 */
	public Path getPath(String flag) {
		String path = map.get(flag);
		if(path != null) {
			return Path.of(path);
		}
		return null;
	}

	/**
	 * Gets the path from the given flag
	 * Or returns the defaultValue if it doesn't exist
	 * @param flag the flag we search for
	 * @param defaultValue the default value we return
	 * @return the path of the flag or the defaultValue
	 */
	public Path getPath(String flag, Path defaultValue) {
		Path value = getPath(flag);
		return value == null ? defaultValue : value;
	}
		 
	/**
	 * Parses the argument given
	 * @param arg the argument we are parsing
	 */
	public void parse(String[] arg) {
		for (int i = 0; i < arg.length; i++) {
			if (isFlag(arg[i])) {
				if (i + 1 != arg.length && isValue(arg[i + 1])) {
						map.put(arg[i], arg[i + 1]);
					}
				else {
					map.put(arg[i], null);
				}
			}
		}
	}
		
	/**
	 * Overrides the default toString method
	 * to print map
	 * @return a string of the map
	 */
	@Override
	public String toString() {
		return this.map.toString();
	}
}