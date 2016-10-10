package sub.fwb;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.HighlightParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class ParametersModifyingSearchHandler extends SearchHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {

		String oldQuery = req.getParams().get(CommonParams.Q);
		String queryFieldsWithBoosts = req.getParams().get("qf");
		String hlFields = req.getParams().get(HighlightParams.FIELDS);
		ParametersModifier modifier = new ParametersModifier(queryFieldsWithBoosts);
		ModifiedParameters modified = modifier.changeParamsForQuery(oldQuery);
		String newQuery = modified.q;
		String newHlQuery = modified.hlQ;

		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		newParams.set(CommonParams.Q, newQuery);
		if (!newHlQuery.isEmpty()) {
			newParams.set("hl.q", newHlQuery);
		}
		req.setParams(newParams);

		NamedList<String> queryInfoList = new SimpleOrderedMap<>();
		queryInfoList.add("original q", oldQuery);
		queryInfoList.add("modified q", newQuery);
		if (!newHlQuery.isEmpty()) {
			queryInfoList.add("modified hl.q", newHlQuery);
		}
		rsp.add("parametersModifier", queryInfoList);

		super.handleRequestBody(req, rsp);
	}

	public static class ModifiedParameters {
		public String q = "";
		public String hlQ = "";
		public String fq = "";
		public String hlFl = "";
		public ModifiedParameters(String q, String hlQ, String fq, String hlFl) {
			this.q = q;
			this.hlQ = hlQ;
			this.fq = fq;
			this.hlFl = hlFl;
		}
	}

}
