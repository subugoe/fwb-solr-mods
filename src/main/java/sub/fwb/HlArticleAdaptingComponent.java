package sub.fwb;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.response.SolrQueryResponse;

public class HlArticleAdaptingComponent extends SearchComponent {

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		SolrQueryResponse response = rb.rsp;
		@SuppressWarnings("unchecked")
		SimpleOrderedMap<Object> highlightedFields = (SimpleOrderedMap<Object>) ((SimpleOrderedMap<Object>) response
				.getValues().get("highlighting")).getVal(0);
		String article = ((String[]) highlightedFields.get("artikel"))[0];

		for (int i = highlightedFields.size() - 1; i >= 0; i--) {
			String fieldName = highlightedFields.getName(i);
			if (!fieldName.equals("artikel")) {
				String[] values = (String[]) highlightedFields.get(fieldName);
				for (String value : values) {
					String valueStart = getStart(value);
					String valueEnd = getEnd(value);
					String valueMiddle = getMiddle(value);
					article = article.replaceFirst("(?s)" + valueStart + ".*?" + valueEnd, valueMiddle);
				}
				highlightedFields.remove(fieldName);
			}
		}
		highlightedFields.add("artikel", new String[] { article });
	}

	private String getStart(String quote) {
		return extract("(<!--start .*?-->)", quote);
	}

	private String getEnd(String quote) {
		return extract("(<!--end .*?-->)", quote);
	}

	private String getMiddle(String quote) {
		return extract("<!--start .*?-->(.*)<!--end .*?-->", quote);
	}

	private String extract(String regex, String s) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

	@Override
	public String getDescription() {
		return "Modifier for highlighted article";
	}

}
