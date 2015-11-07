package spring.webcrawler.service.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.LinkStringFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spring.webcrawler.common.SpiderConstant;
import spring.webcrawler.service.index.IIndexCreator;
import spring.webcrawler.service.spider.SpiderService;

/**
 * Parser by using HtmlParser
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class ParserByHtmlParserService implements IParserService {

	private static Logger logger = LoggerFactory
			.getLogger(ParserByHtmlParserService.class);
	private IIndexCreator indexCreator;
	private String title;
	private List<URI> uris;
	private URI uri;
	private Parser parser;

	public IIndexCreator getIndexCreator() {
		return indexCreator;
	}

	public void setIndexCreator(IIndexCreator indexCreator) {
		this.indexCreator = indexCreator;
	}

	@Override
	public List<URI> parseLinks(URI uri, String content) {

		this.uri = uri;
		uris = new LinkedList<URI>();

		try {
			StringBean stringBean = new StringBean();
			parser = new Parser(uri.toString());
			parser.visitAllNodesWith(stringBean);
			String contentText = stringBean.getStrings();

			// create index
			indexCreator.createIndex(uri, contentText);

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
		} catch (ParserException e1) {
			e1.printStackTrace();
		}

		logger.debug("Found (" + uris.size() + ") links in [" + uri.toString()
				+ "]");

		return uris;

	}

	private boolean samePageCheckByTitle(String content) throws ParserException {

		parser = new Parser(uri.toString());
		NodeList nodes;
		nodes = parser.parse(new HasAttributeFilter("title"));
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.elementAt(i);
			if (node instanceof TitleTag) {
				title = (String) ((TitleTag) node).getTitle();
				break;
			}
		}

		if (null != SpiderService.titles
				&& !SpiderService.titles.contains(title)) {
			SpiderService.titles.add(title);
			return false;
		}
		return true;
	}

	private void getLinks(String content) throws ParserException {

		String link;
		URI addUri;
		parser = new Parser(uri.toString());
		NodeList list = parser.parse(new LinkStringFilter(""));
		for (int i = 0; i < list.size(); i++) {

			Node node = list.elementAt(i);
			if (node instanceof LinkTag) {
				link = ((LinkTag) node).extractLink();
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
					link = SpiderConstant.PREFIX_HTTP + SpiderService.HOST
							+ link;
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
				} catch (URISyntaxException e) {
					logger.error("URISyntaxException with href:" + link);
					// continue;
				}
			}
		}
	}
}
