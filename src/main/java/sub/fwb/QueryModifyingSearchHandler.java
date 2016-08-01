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

		NamedList<String> nl = new SimpleOrderedMap<>();
		
		String oldQuery = req.getParams().get(CommonParams.Q);
		System.out.println("old: " + oldQuery);
		nl.add("original q", oldQuery);

		ModifiableSolrParams par = new ModifiableSolrParams(req.getParams());
		par.set(CommonParams.Q, "lemma:imbis");
		req.setParams(par);
		String newQuery = req.getParams().get(CommonParams.Q);
		System.out.println("new: " + newQuery);
		nl.add("modified q", newQuery);
		rsp.add("queryModifier", nl);
		super.handleRequestBody(req, rsp);
	}

}
