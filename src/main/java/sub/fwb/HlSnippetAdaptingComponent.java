package sub.fwb;

import java.io.IOException;

import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.response.SolrQueryResponse;

public class HlSnippetAdaptingComponent extends SearchComponent {

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		SolrQueryResponse response = rb.rsp;
		SimpleOrderedMap<Object> highlightedDocs = (SimpleOrderedMap<Object>) response.getValues().get("highlighting");
		if (highlightedDocs != null) {
			for (int i = 0; i < highlightedDocs.size(); i++) {
				SimpleOrderedMap<Object> currentDoc = (SimpleOrderedMap<Object>) highlightedDocs.getVal(i);

				String[] articleHl = (String[]) currentDoc.get("artikel_text");
				if (isEmpty(articleHl)) {
					String[] quoteHl = (String[]) currentDoc.get("zitat_text");
					currentDoc.add("artikel_text", quoteHl);
					currentDoc.remove("zitat_text");
				}
			}
		}
	}

	private boolean isEmpty(String[] array) {
		return array == null || array.length == 0;
	}

	@Override
	public String getDescription() {
		return "Modifier for highlighted snippets";
	}

}
