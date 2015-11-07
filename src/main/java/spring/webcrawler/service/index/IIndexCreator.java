package spring.webcrawler.service.index;

import java.net.URI;

/**
 * The interface of index creator
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public interface IIndexCreator {
	
	/**
	 * Create index for searching
	 * 
	 * @param URI
	 *            target uri
	 * @param String
	 *            page info
	 */
	public void createIndex(URI uri, String content);
}
