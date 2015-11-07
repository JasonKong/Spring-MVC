package spring.webcrawler.service.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.jerry.JerryFunction;
import jodd.lagarto.LagartoParser;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.LagartoDOMBuilderTagVisitor;
import jodd.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spring.webcrawler.common.SpiderConstant;
import spring.webcrawler.service.index.IIndexCreator;
import spring.webcrawler.service.spider.SpiderService;

/**
 * Parser by using Jerry
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class ParserByJerryService implements IParserService {

	private static Logger logger = LoggerFactory
			.getLogger(ParserByJerryService.class);
	private IIndexCreator indexCreator;
	private List<URI> uris;
	private URI uri;

	public IIndexCreator getIndexCreator() {
		return indexCreator;
	}

	public void setIndexCreator(IIndexCreator indexCreator) {
		this.indexCreator = indexCreator;
	}

	public List<URI> parseLinks(URI uri, String content) {
		this.uri = uri;
		uris = new LinkedList<URI>();
		
		// get page info of current uri
		getBodyText(content);

		// check same page by title
		if (samePageCheckByTitle(content)) {
			logger.debug("Already visited because of same title" + uri.toString());
		} else {
			// if current depth is 1, stop getting links
			if (1 == SpiderService.currentDepth) {
				logger.debug("Stop parsing href because current depth is 1.");
			} else {
				getLinks(content);
			}
		}
		
		logger.debug("Found (" + uris.size() + ") links in [" + uri.toString()
				+ "]");

		return uris;
	}

	private void getBodyText(String content) {
		JerryParser jerryParser = Jerry.jerry();

		LagartoDOMBuilder lagartoDOMBuilder = (LagartoDOMBuilder) jerryParser
				.getDOMBuilder();

		LagartoDOMBuilderTagVisitor tagVisitor = new LagartoDOMBuilderTagVisitor(
				lagartoDOMBuilder);

		lagartoDOMBuilder.enableHtmlMode();

		StringBuilder result = new StringBuilder();
		LagartoParser parser = new LagartoParser(content, false);

		parser.parse(new TagAdapter(tagVisitor) {

			public void text(CharSequence text) {

				String str = text.toString();
				str = StringUtil.removeChars(str, "\r\n\t\b");
				if (str.trim().length() != 0) {
					result.append(str);
				}
			}

		});

		// create index
		indexCreator.createIndex(uri, result.toString());
	}

	private boolean samePageCheckByTitle(String content) {
		int start = content.indexOf("<title>") + 7;
		int end = content.indexOf("</title>");

		if (start < 0 || end < 0 || start > end) {
			return false;
		}

		String title = content.substring(start, end);
		if (null != SpiderService.titles
				&& !SpiderService.titles.contains(title)) {
			SpiderService.titles.add(title);
			return false;
		}
		return true;
	}

	private void getLinks(String content) {

		Jerry doc = Jerry.jerry(content);
		doc.$("a").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {

				URI uri;
				String link = $this.attr("href");

				if (null == link || "".equals(link.trim())) {
					logger.debug("Blank href!");
					return true;
				}

				if (!link.startsWith(SpiderConstant.PREFIX_HTTP)
						&& !link.startsWith(SpiderConstant.PREFIX_HTTPS)
						&& !link.startsWith(SpiderConstant.PREFIX_SLASH)) {
					logger.debug("Not a http href:" + link);
					return true;
				} else if (link.startsWith(SpiderConstant.PREFIX_SLASH)) {
					link = SpiderConstant.PREFIX_HTTP + SpiderService.HOST
							+ link;
				}

				try {
					uri = new URI(link);
					if (uris.contains(uri)) {
						logger.debug("Already existing href:" + link);
						return true;
					}

					if (!uri.getHost().contains(SpiderService.HOST_KEY)) {
						logger.debug("Other website href:" + link);
						return true;
					}
					
					if (!SpiderService.toVisit.contains(uri)) {
						SpiderService.toVisit.add(uri);
						uris.add(uri);
					}
				} catch (URISyntaxException e) {
					logger.error("URISyntaxException with href:" + link);
					return true;
				}

				return true;
			}
		});
	}
}
