package wasdev.sample.servlet;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;

/**
 * @author Stephan Janssen
 */
class ConceptInsightsConfig {

    // TODO Externalise the credentials using Spring Config
    private static final String INSIGHTS_END_POINT = "https://gateway.watsonplatform.net/concept-insights/api";
    private static final String INSIGHTS_USERNAME = "a425694b-f06a-4956-886d-e2e9e66d7c65";
    private static final String INSIGHTS_PASSWORD = "FPhRNXvLnJ9m";

    public ConceptInsights getService() {
        final com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights conceptInsightsService;
        conceptInsightsService = new com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights();
        conceptInsightsService.setEndPoint(INSIGHTS_END_POINT);
        conceptInsightsService.setUsernameAndPassword(INSIGHTS_USERNAME, INSIGHTS_PASSWORD);
        return conceptInsightsService;
    }
}
