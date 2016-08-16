package sub.fwb;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class QueryModifyingSearchHandler extends SearchHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {

		String oldQuery = req.getParams().get(CommonParams.Q);
		String queryFieldsWithBoosts = req.getParams().get("qf");
		QueryModifier modifier = new QueryModifier(queryFieldsWithBoosts);
		String[] changedQueries = modifier.expandQuery(oldQuery);
		String newQuery = changedQueries[0];
		String hlQuery = changedQueries[1];

		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		newParams.set(CommonParams.Q, newQuery);
		if (!hlQuery.isEmpty()) {
			newParams.set("hl.q", hlQuery);
		}
		req.setParams(newParams);

		NamedList<String> queryInfoList = new SimpleOrderedMap<>();
		queryInfoList.add("original q", oldQuery);
		queryInfoList.add("modified q", newQuery);
		if (!hlQuery.isEmpty()) {
			queryInfoList.add("modified hl.q", hlQuery);
		}
		rsp.add("queryModifier", queryInfoList);

		super.handleRequestBody(req, rsp);
	}

}
