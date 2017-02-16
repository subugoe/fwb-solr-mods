package sub.fwb;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.response.SolrQueryResponse;

import sub.fwb.parse.ParseUtil;

public class HlArticleAdaptingComponent extends SearchComponent {

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		SolrQueryResponse response = rb.rsp;
		String highlightMarker = rb.req.getParams().get("hl.simple.pre");
		@SuppressWarnings("unchecked")
		SimpleOrderedMap<Object> highlightedFields = (SimpleOrderedMap<Object>) ((SimpleOrderedMap<Object>) response
				.getValues().get("highlighting")).getVal(0);
		String articleField = "";
		String ending = ParseUtil.EXACT;
		if (highlightedFields.get("artikel") != null) {
			articleField = "artikel";
		} else if (highlightedFields.get("artikel" + ending) != null) {
			articleField = "artikel" + ending;
		} else {
			throw new RuntimeException("No highlighted field found. Must be either 'artikel' or 'artikel" + ending + "'.");
		}
		String article = ((String[]) highlightedFields.get(articleField))[0];

		for (int i = highlightedFields.size() - 1; i >= 0; i--) {
			String fieldName = highlightedFields.getName(i);
			if (!fieldName.startsWith("artikel")) {
				String[] values = (String[]) highlightedFields.get(fieldName);
				for (String value : values) {
					if (value.contains(highlightMarker)) {
						String valueStart = getStart(value);
						String valueEnd = getEnd(value);
						String valueMiddle = getMiddle(value);
						article = article.replaceFirst("(?s)" + valueStart + ".*?" + valueEnd, Matcher.quoteReplacement(valueMiddle));
					}
				}
				highlightedFields.remove(fieldName);
			}
		}
		highlightedFields.remove("artikel");
		highlightedFields.remove("artikel" + ending);
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
