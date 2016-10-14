package sub.fwb;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import sub.fwb.ParametersModifyingSearchHandler.ModifiedParameters;

public class HlQueryModifyingSearchHandler extends SearchHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {

		String oldHlQuery = req.getParams().get("hl.q");
		String queryFieldsWithBoosts = req.getParams().get("qf");
		String hlFields = req.getParams().get("hl.fl");
		ParametersModifier modifier = new ParametersModifier(queryFieldsWithBoosts, hlFields);
		ModifiedParameters modified = modifier.changeParamsForQuery(oldHlQuery);

		String newHlQuery = modified.q;
		String newHlFields = modified.hlFl;
		String newQueryFields = modified.qf;

		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		newParams.set("hl.q", newHlQuery);
		newParams.set("qf", newQueryFields);
		newParams.set("hl.fl", newHlFields);
		req.setParams(newParams);

		NamedList<String> queryInfoList = new SimpleOrderedMap<>();
		queryInfoList.add("original hl.q", oldHlQuery);
		queryInfoList.add("modified hl.q", newHlQuery);
		rsp.add("queryModifier", queryInfoList);

		super.handleRequestBody(req, rsp);
	}


}
