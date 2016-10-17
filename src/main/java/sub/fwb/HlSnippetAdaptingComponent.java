package sub.fwb;

import java.io.IOException;
import java.util.List;

import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.parser.ParseException;
import org.apache.solr.response.SolrQueryResponse;

import sub.fwb.parse.TokenFactory;
import sub.fwb.parse.tokens.QueryToken;

public class HlSnippetAdaptingComponent extends SearchComponent {

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		SolrQueryResponse response = rb.rsp;
		@SuppressWarnings("unchecked")
		SimpleOrderedMap<Object> highlightedDocs = (SimpleOrderedMap<Object>) response.getValues().get("highlighting");
		if (highlightedDocs != null) {
			for (int i = 0; i < highlightedDocs.size(); i++) {
				@SuppressWarnings("unchecked")
				SimpleOrderedMap<Object> currentDoc = (SimpleOrderedMap<Object>) highlightedDocs.getVal(i);

				String[] hlArticleSnippet = (String[]) currentDoc.get("artikel_text");
				if (isEmpty(hlArticleSnippet) && currentDoc.size() > 0) {
					String[] nonArticleSnippet = (String[]) currentDoc.getVal(0);
					currentDoc.add("artikel_text", nonArticleSnippet);
				}

				if (onlyLemmaIsQueried(rb)) {
					removeHighlightingTags(currentDoc, rb);
				}
			}
		}

	}

	private boolean isEmpty(String[] array) {
		return array == null || array.length == 0;
	}

	private boolean onlyLemmaIsQueried(ResponseBuilder rb) {
		@SuppressWarnings("unchecked")
		SimpleOrderedMap<Object> modifier = (SimpleOrderedMap<Object>) rb.rsp.getValues().get("parametersModifier");
		String originalQ = (String) modifier.get("original q");
		String queryFieldsWithBoosts = rb.req.getParams().get("qf");
		if (originalQ.contains("EXAKT")) {
			originalQ = originalQ.replace("EXAKT", "");
			originalQ = originalQ.trim();
		}
		TokenFactory factory = new TokenFactory();
		List<QueryToken> tokens = null;
		try {
			tokens = factory.createTokens(originalQ, queryFieldsWithBoosts, false);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		if (originalQ.startsWith("lemma:") && tokens.size() == 1) {
			return true;
		} else {
			return false;
		}
	}

	private void removeHighlightingTags(SimpleOrderedMap<Object> currentDoc, ResponseBuilder rb) {
		String[] hlSnippetArray = (String[]) currentDoc.get("artikel_text");
		if (hlSnippetArray == null || hlSnippetArray.length == 0) {
			return;
		}
		String hlSnippet = hlSnippetArray[0];
		String hlPre = rb.req.getParams().get("hl.simple.pre");
		String hlPost = rb.req.getParams().get("hl.simple.post");

		hlSnippet = hlSnippet.replace(hlPre, "");
		hlSnippet = hlSnippet.replace(hlPost, "");

		currentDoc.add("artikel_text", new String[] { hlSnippet });
	}

	@Override
	public String getDescription() {
		return "Modifier for highlighted snippets";
	}

}
