package wasdev.sample.servlet;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.*;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.AccountPermission.Permission;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static wasdev.sample.servlet.Constant.DEVOXX_CORPUS;

/**
 * Servlet implementation
 */
@WebServlet("/labelSearch")
public class LabelSearch extends HttpServlet {

	private static final long serialVersionUID = 2492962675818275629L;
	
	private static final Logger LOGGER = Logger.getLogger(LabelSearch.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOGGER.info("initialize concept insights service");

        final ConceptInsights conceptInsightsService = new ConceptInsightsConfig().getService();

        final String account = conceptInsightsService.getFirstAccountId();

        Corpus corpus = new Corpus(account, DEVOXX_CORPUS);
        //corpus.addAccountPermissions(new AccountPermission(account, Permission.READ_WRITE_ADMIN));

        // label search

        LOGGER.info("enumeration");
        Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            String value = request.getParameter(key);
            LOGGER.info(key + "=" + value);

        }

        LOGGER.info("label search");

        Map<String, Object> searchGraphConceptByLabelParams = new HashMap<>();
        searchGraphConceptByLabelParams.put("query", request.getParameter("keyword"));
        searchGraphConceptByLabelParams.put("prefix", true);
        searchGraphConceptByLabelParams.put("limit", 10);

        RequestedFields concept_fields = new RequestedFields();
        concept_fields.include("link");
        concept_fields.include("\"abstract\":1");
        RequestedFields document_fields = new RequestedFields();
        document_fields.include("\"user_fields\":1");

        searchGraphConceptByLabelParams.put("concept_fields", concept_fields);

        Matches matches = conceptInsightsService.searchGraphsConceptByLabel(Graph.WIKIPEDIA, searchGraphConceptByLabelParams);

        LOGGER.info("gather matches from label search");

        List<String> ids = new ArrayList<>();

        for (Concept concept : matches.getMatches()) {
            ids.add(concept.getId());
            break;
        }

        LOGGER.info("conceptual search");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ConceptInsights.IDS, ids);
        parameters.put(ConceptInsights.LIMIT, 20);

        RequestedFields requestedFields = new RequestedFields();
        requestedFields.include("\"user_fields\":1");
        parameters.put(ConceptInsights.DOCUMENT_FIELDS, requestedFields);
        

        QueryConcepts queryConcepts = conceptInsightsService.conceptualSearch(corpus, parameters);

        // output results

        LOGGER.info("output results");

        response.setContentType("text/html");
        response.getWriter().print(queryConcepts.getResults().toString());
        System.out.println(queryConcepts.toString());

    }
}