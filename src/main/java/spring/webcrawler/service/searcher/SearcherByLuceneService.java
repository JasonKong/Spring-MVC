package spring.webcrawler.service.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spring.webcrawler.bean.UrlBean;
import spring.webcrawler.common.SpiderConstant;

/**
 * searcher by using Lucene
 * 
 * @author Ming Kong(ID:1449315)
 *
 */
public class SearcherByLuceneService implements ISearcherService {
	private static Logger logger = LoggerFactory
			.getLogger(SearcherByLuceneService.class);
	@Override
	public List<UrlBean> search(String content) {

		Directory directory;
		IndexSearcher searcher;
		IndexReader reader;
		QueryParser parser;
		Query query;
		TopDocs tds;
		ScoreDoc[] sds;
		Document doc;
		// Analyzer a;

		List<UrlBean> lst = new ArrayList<>();

		try {
			// a = new NMSegAnalyzer();

			directory = FSDirectory.open(new File(
					SpiderConstant.LUCENE_DAT_PATH));

			reader = IndexReader.open(directory);

			searcher = new IndexSearcher(reader);

			parser = new QueryParser(Version.LUCENE_36,
					SpiderConstant.LUCENE_FIELD_CONTENT, new StandardAnalyzer(
							Version.LUCENE_36));

			query = parser.parse(content);

			tds = searcher.search(query, 80);

			sds = tds.scoreDocs;

			logger.debug("Found " + sds.length + " hits.");
			for (ScoreDoc sd : sds) {
				doc = searcher.doc(sd.doc);
				String txt = doc.get(SpiderConstant.LUCENE_FIELD_CONTENT);
				txt = highlight(new StandardAnalyzer(Version.LUCENE_36), query,
						SpiderConstant.LUCENE_FIELD_CONTENT, txt);
				lst.add(new UrlBean(doc.get(SpiderConstant.LUCENE_FIELD_URI),
						txt));
				logger.debug("score   = " + sd.score);
				logger.debug("uri     = "
						+ doc.get(SpiderConstant.LUCENE_FIELD_URI));
//				logger.debug("content = " + txt);
			}

			if (null != searcher)
				searcher.close();
			if (null != reader)
				reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}

		return lst;
	}

	private String highlight(Analyzer a, Query query, String fieldName,
			String txt) throws IOException, InvalidTokenOffsetsException {
		String str = null;
		QueryScorer scorer = new QueryScorer(query);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		// SimpleHTMLFormatter fmt = new SimpleHTMLFormatter("<b>", "</b>");
		SimpleHTMLFormatter fmt = new SimpleHTMLFormatter(
				"<span style='color:red'>", "</span>");
		Highlighter lighter = new Highlighter(fmt, scorer);
		lighter.setTextFragmenter(fragmenter);

		str = lighter.getBestFragment(a, fieldName, txt);
		return str;

	}
}
