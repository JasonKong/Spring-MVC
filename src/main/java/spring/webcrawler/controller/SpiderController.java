package spring.webcrawler.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import spring.webcrawler.bean.CrawlerBean;
import spring.webcrawler.bean.UrlBean;
import spring.webcrawler.service.searcher.ISearcherService;
import spring.webcrawler.service.searcher.SearcherByLuceneService;
import spring.webcrawler.service.spider.ISpiderService;
import spring.webcrawler.service.spider.SpiderService;

/**
 * The controller of spider project. Main function is to crawl and to search.
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
@Controller
@EnableAutoConfiguration
public class SpiderController {

	private static Logger logger = LoggerFactory
			.getLogger(SpiderController.class);
	private static BeanFactory factory;
	private static ISearcherService searcher;
	private static ISpiderService spider;

	List<UrlBean> urlBeans = new ArrayList<UrlBean>();

	public SpiderController() {
		// Read xml to get all classes
		factory = new ClassPathXmlApplicationContext("spring.xml");
		// spider class
		spider = factory.getBean("spider", SpiderService.class);
		// searcher class
		searcher = factory.getBean("searcher", SearcherByLuceneService.class);
	}

	/*
	 * When search button is clicked, search method will be called.
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/search")
	public String search(CrawlerBean crawlerBean, Map<String, Object> model) {

		if ("".equals(crawlerBean.getSearchKey().trim())) {
			crawlerBean.setMessage("Please input key you want to search.");
			return "searcher";
		}
		// Do search
		urlBeans = searcher.search(crawlerBean.getSearchKey());

		// Put search results into model map which could be displayed on the
		// page.
		model.put("urlBeans", urlBeans);

		logger.debug("searcher is successful!");
		return "searcher";
	}

	/*
	 * When crawl button is clicked, crawl method will be called.
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/crawl")
	public String crawl(CrawlerBean crawlerBean) {
		try {

			// Do crawl
			spider.startCrawl(new URI(crawlerBean.getUri()),
					crawlerBean.getDepth());
			logger.debug("Crawler is successful!");

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return "searcher";
	}

	/*
	 * Launch spider page
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/spider")
	public String webcrawler(Map<String, Object> model) {
		return "spider";
	}

	/*
	 * Launch search page
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/searcher")
	public String searcher(Map<String, Object> model) {
		return "searcher";
	}

	public static void main(String[] args) {
		SpringApplication.run(SpiderController.class, args);
	}
}
