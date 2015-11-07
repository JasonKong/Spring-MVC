package spring.webcrawler.service.parser;

import java.net.URI;
import java.util.List;

/**
 * The interface of parser
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public interface IParserService {

	/**
	 * Parse links
	 * 
	 * @param URI
	 *            target uri
	 * @param String
	 *            page info
	 */
	public List<URI> parseLinks(URI uri, String content);
}
