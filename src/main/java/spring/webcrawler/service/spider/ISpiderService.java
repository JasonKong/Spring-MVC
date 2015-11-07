package spring.webcrawler.service.spider;

import java.net.URI;

/**
 * The interface of spider
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public interface ISpiderService {
	
	/**
	 * Crawl a uri and create index for searching
	 * 
	 * @param URI
	 *            target uri
	 * @param int
	 *            crawling depth
	 */
	public void startCrawl(URI uri, int depth);
}
