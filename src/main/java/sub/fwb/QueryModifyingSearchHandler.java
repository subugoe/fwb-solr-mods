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
		QueryModifier modifier = new QueryModifier();
		String newQuery = modifier.expandQuery(oldQuery);

		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		newParams.set(CommonParams.Q, newQuery);
		req.setParams(newParams);

		NamedList<String> queryInfoList = new SimpleOrderedMap<>();
		queryInfoList.add("original q", oldQuery);
		queryInfoList.add("modified q", newQuery);
		rsp.add("queryModifier", queryInfoList);

		super.handleRequestBody(req, rsp);
	}

}
