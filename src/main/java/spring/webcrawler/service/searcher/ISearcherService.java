package spring.webcrawler.service.searcher;

import java.util.List;

import spring.webcrawler.bean.UrlBean;

/**
 * The interface of searcher
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public interface ISearcherService {
	
	/**
	 * Parse links
	 * 
	 * @param String
	 *            search key
	 */
	public List<UrlBean> search(String searchKey);
}
