package spring.webcrawler.service.spider;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spring.webcrawler.service.crawler.ICrawlerService;
import spring.webcrawler.service.parser.IParserService;

/**
 * Spider
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class SpiderService implements ISpiderService {

	private static Logger logger = LoggerFactory
			.getLogger(SpiderService.class);

	private ICrawlerService crawler;
	private IParserService parser;

	private Set<URI> alreadyVisited = new HashSet<URI>();
	public static List<URI> toVisit = new LinkedList<URI>();
	public static Set<String> titles = new HashSet<String>();
	public static int currentDepth;
	public static String HOST;
	public static String HOST_KEY;

	public ICrawlerService getCrawler() {
		return crawler;
	}

	public void setCrawler(ICrawlerService crawler) {
		this.crawler = crawler;
	}

	public IParserService getParser() {
		return parser;
	}

	public void setParser(IParserService parser) {
		this.parser = parser;
	}

	@Override
	public void startCrawl(URI uri, int depth) {
		long starTime = System.currentTimeMillis();
		deleteLuceneData();
		HOST = uri.getHost();
		HOST_KEY = uri.getHost().substring(4);
		
		// crawl
		crawl(uri, depth);
		
		long endTime = System.currentTimeMillis();

		logger.info("The num of already visited pages: "
				+ alreadyVisited.size());
		logger.info("The num of toVisit pages: " + toVisit.size());
		logger.info("The time of processing: "
				+ (float) (endTime - starTime) / 1000 + 's');
	}

	/*
	 * A recursive function for crawlling
	 */
	private void crawl(URI uri, int depth) {
		currentDepth = depth;
		logger.debug("current depth:" + depth);
		
		StringBuilder messageResponse = new StringBuilder();
		if (depth < 1) {
			return;
		}

		if (alreadyVisited.contains(uri)) {
			return;
		} else {
			alreadyVisited.add(uri);
		}

		// call crawler
		if (crawler.get(uri, messageResponse)) {

			// call parser
			List<URI> uris = parser.parseLinks(uri, messageResponse.toString());

			for (int i = 0; i < uris.size(); i++) {
				crawl(uris.get(i), depth - 1);
			}
		}
	}

	/*
	 * delete lucene data
	 */
	private void deleteLuceneData() {
		String path = System.getProperty("user.dir") + "\\lucene_dat";
		File directory = new File(path);
		if (directory.exists() && directory.isDirectory()) {
			File files[] = directory.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
		// directory.delete();
	}

}
