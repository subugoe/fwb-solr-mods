package sub.fwb;

import java.io.IOException;

import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.response.SolrQueryResponse;

import sub.fwb.parse.ParseUtil;

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
				String[] originalSnippet = (String[]) currentDoc.get("artikel_text");
				String[] newSnippet = null;
				if (onlyLemmaHighlightings(currentDoc)) {
					String[] firstLemmaSnippet = (String[]) currentDoc.getVal(0);
					firstLemmaSnippet = removeHighlightingTags(firstLemmaSnippet, rb);
					firstLemmaSnippet = removeFirstWord(firstLemmaSnippet);
					newSnippet = firstLemmaSnippet;
				} else if (isEmpty(originalSnippet)) {
					newSnippet = chooseOtherSnippet(currentDoc);
				} else {
					newSnippet = originalSnippet;
				}
				currentDoc.add("artikel_text", formatSnippet(newSnippet));
			}
		}
	}

	private boolean onlyLemmaHighlightings(SimpleOrderedMap<Object> currentDoc) {
		if (currentDoc.size() == 0) {
			return false;
		}
		for (int i = 0; i < currentDoc.size(); i++) {
			String currentHlName = currentDoc.getName(i);
			if (!currentHlName.startsWith("lemma")) {
				return false;
			}
		}
		return true;
	}

	private String[] removeHighlightingTags(String[] snippetArray, ResponseBuilder rb) {
		if (snippetArray.length == 0) {
			return new String[] { "" };
		}
		String hlSnippet = snippetArray[0];
		String hlPre = rb.req.getParams().get("hl.simple.pre");
		String hlPost = rb.req.getParams().get("hl.simple.post");

		hlSnippet = hlSnippet.replace(hlPre, "");
		hlSnippet = hlSnippet.replace(hlPost, "");

		return new String[] { hlSnippet };
	}

	private String[] removeFirstWord(String[] snippetArray) {
		String hlSnippet = snippetArray[0];
		int firstSpace = hlSnippet.indexOf(" ");
		boolean thereAreCharsAfterSpace = hlSnippet.length() > firstSpace + 1;
		if (firstSpace > 0 && thereAreCharsAfterSpace) {
			hlSnippet = hlSnippet.substring(firstSpace + 1);
		}
		return new String[] { hlSnippet };
	}

	private boolean isEmpty(String[] array) {
		return array == null || array.length == 0;
	}

	private String[] chooseOtherSnippet(SimpleOrderedMap<Object> currentDoc) {
		for (int i = 0; i < currentDoc.size(); i++) {
			String currentHlName = currentDoc.getName(i);
			if (!currentHlName.startsWith("lemma")) {
				return (String[]) currentDoc.getVal(i);
			}
		}
		return new String[] { "" };
	}

	private String[] formatSnippet(String[] snippetArray) {
		String hlSnippet = "";
		if (!isEmpty(snippetArray)) {
			hlSnippet = snippetArray[0];
			hlSnippet = ParseUtil.trimSpecialChars(hlSnippet);
		}
		return new String[] { hlSnippet };
	}

	@Override
	public String getDescription() {
		return "Modifier for highlighted snippets";
	}

}
