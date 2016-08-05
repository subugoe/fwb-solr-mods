package sub.fwb;

import org.apache.solr.common.params.HighlightParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class HlQueryModifyingSearchHandler extends SearchHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {

		String oldQuery = req.getParams().get(HighlightParams.Q);
		QueryModifier modifier = new QueryModifier();
		String newQuery = modifier.expandQuery(oldQuery);

		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		newParams.set(HighlightParams.Q, newQuery);
		req.setParams(newParams);

		NamedList<String> queryInfoList = new SimpleOrderedMap<>();
		queryInfoList.add("original hl.q", oldQuery);
		queryInfoList.add("modified hl.q", newQuery);
		rsp.add("queryModifier", queryInfoList);

		super.handleRequestBody(req, rsp);
	}


}
