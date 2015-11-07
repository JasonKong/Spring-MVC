package spring.webcrawler.bean;

/**
 * UrlBean: Using for saving the search result
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class UrlBean {
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String url;
	private String title;
	private String content;

	public UrlBean(String url, String title, String content) {
		this.url = url;
		this.title = title;
		this.content = content;
	}

	public UrlBean(String url, String content) {
		this.url = url;
		this.content = content;
	}

	public UrlBean() {
	}
}
