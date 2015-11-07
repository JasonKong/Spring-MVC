package spring.webcrawler.service.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crawler by using HttpClient
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class CrawlerByHttpClientService implements ICrawlerService {

	private static Logger logger = LoggerFactory
			.getLogger(CrawlerByHttpClientService.class);
	
	@Override
	public boolean get(URI uri, StringBuilder content) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(new HttpGet(uri));

			if (200 != httpResponse.getStatusLine().getStatusCode()) {
				logger.error("Invalid URI:" + uri.toString());
				return false;
			}

			InputStream is = httpResponse.getEntity().getContent();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));

			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}

		} catch (ClientProtocolException e) {
			e.getMessage();
			return false;
		} catch (IOException e) {
			e.getMessage();
			return false;
		}

		return true;
	}

}
