package wasdev.sample.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.AccountPermission;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.AccountPermission.Permission;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Corpus;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.QueryConcepts;

/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/labelSearch")
public class LabelSearch extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(LabelSearch.class.getName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	    // initialize concept insights service
	    
		logger.info("initialize concept insights service");
		
	    final ConceptInsights conceptInsightsService = new ConceptInsights();
	    conceptInsightsService.setEndPoint("https://gateway.watsonplatform.net/concept-insights/api");
	    conceptInsightsService.setUsernameAndPassword("a425694b-f06a-4956-886d-e2e9e66d7c65", "FPhRNXvLnJ9m");
	    
	    final String name = "devoxx_corpus1";
	    final String account = conceptInsightsService.getFirstAccountId();
	    
	    Corpus corpus = new Corpus(account, name);
	    corpus.addAccountPermissions(new AccountPermission(account, Permission.READ_WRITE_ADMIN));
	    
	    // label search
	    
		logger.info("enumeration");
	    Enumeration<String> e = request.getParameterNames();
	    while (e.hasMoreElements())
	    {
			logger.info(e.nextElement());
	    	
	    }

//	    logger.info("label search");
//		
//	    Map <String, Object> searchGraphConceptByLabelParams = new HashMap<String, Object>();
//	    searchGraphConceptByLabelParams.put("query", request.getParameter("keyword"));
//	    searchGraphConceptByLabelParams.put("prefix", true);
//	    searchGraphConceptByLabelParams.put("limit", 10);
//
//	    RequestedFields concept_fields = new RequestedFields();
//	    concept_fields.include("link");
//	    concept_fields.include("\"abstract\":1");
//
//	    searchGraphConceptByLabelParams.put("concept_fields", concept_fields);
//
//	    Matches matches = conceptInsightsService.searchGraphsConceptByLabel(Graph.WIKIPEDIA, searchGraphConceptByLabelParams);
	    
	    logger.info("conceptual search");
	    
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    List<String> ids = new ArrayList<String>();
	    ids.add("/graphs/wikipedia/en-20120601/concepts/Artificial_intelligence");
//	    ids.add("/corpora/eve6tionsto1/devoxx_corpus1");
	    parameters.put(ConceptInsights.IDS, ids);
	    parameters.put(ConceptInsights.LIMIT, 10);
//	    parameters.put("document_fields", "{\"user_fields\":1} ");
		
//	    parameters.put("link", Integer.valueOf(1));

	    QueryConcepts queryConcepts = conceptInsightsService.conceptualSearch(corpus, parameters);

	    // output results
	    
		logger.info("output results");
		
		response.setContentType("text/html");
		response.getWriter().print(queryConcepts.toString());
		
	}

}

