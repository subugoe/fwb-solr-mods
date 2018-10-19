package sub.fwb;

import java.util.Set;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class ParametersModifyingSearchHandler extends SearchHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {

		String oldQuery = req.getParams().get("q"); // lemma:imbis
		String queryFieldsWithBoosts = req.getParams().get("qf"); // lemma^10000 neblem^1000 ...
		String hlFields = req.getParams().get("hl.fl"); // lemma_text,neblem_text,...
		ParametersModifier modifier = new ParametersModifier(queryFieldsWithBoosts, hlFields);
		ModifiedParameters modified = modifier.changeParamsForQuery(oldQuery);
		
		String newQuery = modified.q; // lemma:(imbis imbis* *imbis*)^10000
		String newHlQuery = modified.hlQ; // lemma_text:*imbis*
		String newHlFields = modified.hlFl;
		String newQueryFields = modified.qf;
		String defType = modified.defType;
		Set<String> facetQueries = modified.facetQueries;

		String[] filterQueries = req.getParams().getParams("fq");
		if (filterQueries != null && filterQueries.length > 0 && !filterQueries[filterQueries.length - 1].startsWith("wortart")) {
			newHlQuery = rewriteHlQuery(filterQueries);
		}

		ModifiableSolrParams newParams = new ModifiableSolrParams(req.getParams());
		if (!"".equals(defType)) {
			newParams.set("defType", defType);
		}
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

	private String rewriteHlQuery(String[] filterQueries) {
		String lastFq = filterQueries[filterQueries.length - 1];
		String rewritten = "";
		if (lastFq.contains("_exakt:")) {
			rewritten = lastFq.replace("_exakt:", "_text_exakt:");
		} else {
			rewritten = lastFq.replace(":", "_text:");
		}
		return rewritten;
	}

	public static class ModifiedParameters {
		public String q = "";
		public String hlQ = "";
		public String qf = "";
		public String hlFl = "";
		public String defType = "";
		public Set<String> facetQueries;

		public ModifiedParameters(String q, String hlQ, String qf, String hlFl, String defType, Set<String> facetQueries) {
			this.q = q;
			this.hlQ = hlQ;
			this.qf = qf;
			this.hlFl = hlFl;
			this.defType = defType;
			this.facetQueries = facetQueries;
		}
	}

}
