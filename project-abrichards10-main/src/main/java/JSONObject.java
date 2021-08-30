import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Creates a JSON writable status for 
 * JSONWriter --> which implements this interface
 * @author angelarichards261
 *
 */
public interface JSONObject {
	
	/**
	 * Returns a JSONObject String that 
	 * is wrapped with a certain number of tabs
	 * @param times the number of times indented
	 * @return a JSONObject String
	 */
	default String toJSONString(int times) {
		try {
			StringWriter writer = new StringWriter();
			this.toJSONObject(writer, times);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Writes a JSONObject
	 * @param writer the writer to use
	 * @param times the number of times indented
	 * @throws IOException if IOException occurs
	 */
	void toJSONObject(Writer writer, int times) throws IOException;
}
