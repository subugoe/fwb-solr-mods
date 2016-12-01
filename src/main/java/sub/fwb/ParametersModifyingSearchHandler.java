package sub.fwb;

import java.util.List;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class ParametersModifyingSearchHandler extends SearchHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {

		String oldQuery = req.getParams().get("q");
		String queryFieldsWithBoosts = req.getParams().get("qf");
		String hlFields = req.getParams().get("hl.fl");
		ParametersModifier modifier = new ParametersModifier(queryFieldsWithBoosts, hlFields);
		ModifiedParameters modified = modifier.changeParamsForQuery(oldQuery);
		String newQuery = modified.q;
		String newHlQuery = modified.hlQ;
		String newHlFields = modified.hlFl;
		String newQueryFields = modified.qf;
		List<String> facetQueries = modified.facetQueries;

		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		newParams.set("q", newQuery);
		newParams.set("qf", newQueryFields);
		if (!newHlQuery.isEmpty()) {
			newParams.set("hl.q", newHlQuery);
		}
		newParams.set("hl.fl", newHlFields);
		for (String facetQ : facetQueries) {
			newParams.add("facet.query", facetQ);
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
		public String qf = "";
		public String hlFl = "";
		public List<String> facetQueries;

		public ModifiedParameters(String q, String hlQ, String qf, String hlFl, List<String> facetQueries) {
			this.q = q;
			this.hlQ = hlQ;
			this.qf = qf;
			this.hlFl = hlFl;
			this.facetQueries = facetQueries;
		}
	}

}
