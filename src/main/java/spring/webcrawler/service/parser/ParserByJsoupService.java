package spring.webcrawler.service.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spring.webcrawler.common.SpiderConstant;
import spring.webcrawler.service.index.IIndexCreator;
import spring.webcrawler.service.spider.SpiderService;

/**
 * Parser by using Jsoup
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class ParserByJsoupService implements IParserService {

	private static Logger logger = LoggerFactory
			.getLogger(ParserByJerryService.class);
	private IIndexCreator indexCreator;
	private String title;
	private List<URI> uris;

	public IIndexCreator getIndexCreator() {
		return indexCreator;
	}

	public void setIndexCreator(IIndexCreator indexCreator) {
		this.indexCreator = indexCreator;
	}

	@Override
	public List<URI> parseLinks(URI uri, String content) {

		uris = new LinkedList<URI>();

		// get page content for index creating
		Document htmlDocument = Jsoup.parse(content);
		// create index
		indexCreator.createIndex(uri, htmlDocument.body().text());

		Elements linksOnPage = htmlDocument.select("a[href]");
		title = htmlDocument.title();
		if (SpiderService.titles.contains(title)) {
			logger.debug("Already visited because of same title"
					+ uri.toString());
		} else {
			SpiderService.titles.add(title);
			// if current depth is 1, stop getting links
			if (1 == SpiderService.currentDepth) {
				logger.debug("Stop parsing href because current depth is 1.");
			} else {
				getLinks(linksOnPage);
			}
		}
		logger.debug("Found (" + uris.size() + ") links in [" + uri.toString()
				+ "]");
		return uris;
	}

	private void getLinks(Elements linksOnPage) {
		String link;
		URI addUri;
		for (Element e : linksOnPage) {
			link = e.attr("href");

			if (null == link || "".equals(link.trim())) {
				logger.debug("Blank href!");
				continue;
			}

			if (!link.startsWith(SpiderConstant.PREFIX_HTTP)
					&& !link.startsWith(SpiderConstant.PREFIX_HTTPS)
					&& !link.startsWith(SpiderConstant.PREFIX_SLASH)) {
				logger.debug("Not a http href:" + link);
				continue;
			} else if (link.startsWith(SpiderConstant.PREFIX_SLASH)) {
				link = SpiderConstant.PREFIX_HTTP + SpiderService.HOST + link;
			}

			try {
				addUri = new URI(link);
				if (uris.contains(addUri)) {
					logger.debug("Already existing href:" + link);
					continue;
				}

				if (!addUri.getHost().contains(SpiderService.HOST_KEY)) {
					logger.debug("Other website href:" + link);
					continue;
				}

				if (!SpiderService.toVisit.contains(addUri)) {
					SpiderService.toVisit.add(addUri);
					uris.add(addUri);
				}
			} catch (URISyntaxException e1) {
				logger.error("URISyntaxException with href:" + link);
				continue;
			}
		}
	}
}
