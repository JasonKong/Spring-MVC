package spring.webcrawler.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * CrawlerBean: Using for saving page information
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class CrawlerBean {
	@NotNull
	@NotBlank
	@Size(max = 100)
	private String uri;

	@NotNull
	@NotBlank
	@Size(max = 2)
	private int depth;

	private String searchKey;

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public CrawlerBean(String uri, int depth) {
		this.uri = uri;
		this.depth = depth;
	}

	public CrawlerBean() {
	}

}
