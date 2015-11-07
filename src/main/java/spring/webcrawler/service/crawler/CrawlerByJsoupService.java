package spring.webcrawler.service.crawler;

import java.io.IOException;
import java.net.URI;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crawler by using Jsoup
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class CrawlerByJsoupService implements ICrawlerService {

	private static Logger logger = LoggerFactory
			.getLogger(CrawlerByJsoupService.class);

	@Override
	public boolean get(URI uri, StringBuilder content) {

		Connection connection = Jsoup.connect(uri.toString());
		try {
			Document htmlDocument = connection.get();
			content.append(htmlDocument.toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}

		if (connection.response().statusCode() == 200) {
			logger.debug("**Visiting** Received web page at " + uri);
		}

		if (!connection.response().contentType().contains("text/html")) {
			logger.error("**Failure** Retrieved something other than HTML");
			return false;
		}
		return true;
	}

}
