package spring.webcrawler.service.crawler;

import java.net.URI;

/**
 * The interface of crawler
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public interface ICrawlerService {

	/**
	 * Get URI information
	 * 
	 * @param URI
	 *            target uri
	 * @param StringBuilder
	 *            page info
	 */
	public boolean get(URI uri, StringBuilder content);
}
