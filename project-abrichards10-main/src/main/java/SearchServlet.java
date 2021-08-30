import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// https://commons.apache.org/proper/commons-lang/download_lang.cgi

/**
 * The servlet class responsible for setting up a simple message board.
 */
public class SearchServlet extends HttpServlet {

	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202040;

	/** The title to use for this webpage. */
	private static final String TITLE = "Search Engine";

	/** The logger to use for this servlet. */
	private static Logger log = LogManager.getLogger();

	/** The thread-safe data structure to use for storing messages. */
	private final ConcurrentLinkedQueue<String> queue;

	/** Template for HTML. **/
	private final String htmlTemplate;
	
	/** Base path with HTML templates. */
	private static final Path BASE = Path.of("src", "main", "resources");

	/** Default Stemmer **/ 
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	
	/** Web crawler **/ 
	private WebCrawler crawler;
		
	/** Inverted Index **/ 
	private final InvertedIndex invertedIndex;
	
	/**
	 * Initializes this message board. Each message board has its own collection
	 * of messages.
	 * 
	 * @param query the query we initialize 
	 * @param invertedIndex the index to use
	 * @param crawler the crawler to use
	 * @throws IOException if unable to read template
	 */
	public SearchServlet(QueryParseFileInterface query, ThreadSafeInvertedIndex invertedIndex, WebCrawler crawler) throws IOException {
		super();
		queue = new ConcurrentLinkedQueue<>();
		this.crawler = crawler;
		this.invertedIndex = invertedIndex;
		
		Path thispath = BASE.resolve("buildIndex.html");

		System.out.println(thispath);
		System.out.println(thispath.toAbsolutePath());
		System.out.println(thispath.getParent());
		
		htmlTemplate = Files.readString(thispath, StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());
				
		// setup form
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		// compile all of the messages together
		// keep in mind multiple threads may access this at once!
		values.put("messages", String.join("\n\n", queue));

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String username = request.getParameter("name");
		String message = request.getParameter("message");

		username = username == null ? "anonymous" : username;
		message = message == null ? "" : message;

		// avoid xss attacks using apache commons text
		// comment out if you don't have this library installed
		username = StringEscapeUtils.escapeHtml4(username);
		message = StringEscapeUtils.escapeHtml4(message);

		response.getWriter();

		String formatted = String.format(
				"<p>%s<br><font size=\"-2\">[ posted by %s at %s ]</font></p>", 
				message, username, getDate());
		
		// Stem the elements and add them to the list
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		Set<String> list = new TreeSet<String>();
		for (String part : TextParser.parse(message)) {
			list.add((stemmer.stem(part.toLowerCase())).toString());
		}

		// I need to use partial search instead 
		List<InvertedIndex.Results> results = this.invertedIndex.search(list, false);

		if (results.isEmpty()) {
			queue.clear();
			//TOTALLY incorrect, I just always leave the formatting until last minute
			formatted = String.format("<p>%s<br><font size=\"-2\">[searched at %s ]</font></p>", message, getDate());
			queue.add(formatted);
		} else {
			queue.clear();
			for (@SuppressWarnings("unused") ThreadSafeInvertedIndex.Results result : results) {
				formatted = String.format("<p>%s</p>", result.getWhere().toString());
				queue.add(formatted);
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}
	
	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}