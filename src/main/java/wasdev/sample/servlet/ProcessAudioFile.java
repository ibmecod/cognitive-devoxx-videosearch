package wasdev.sample.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.AccountPermission;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.AccountPermission.Permission;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Corpus;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/ProcessAudioFile")
@MultipartConfig
public class ProcessAudioFile extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(ProcessAudioFile.class.getName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// set session timeout
		
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(1*60*60);
		
		// output variables
		
		logger.info("link=" + request.getParameter("link"));
		
		// initialize speech to text service
		
		logger.info("initialize speech to text service");
		
		final SpeechToText speechToTextService = new SpeechToText();
	    speechToTextService.setUsernameAndPassword("d2334b1a-4c18-41df-8dab-3659c0dbfb3d", "dRGqpp6tvCXD");
	    speechToTextService.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");
	    RecognizeOptions options = new RecognizeOptions();
	    options.contentType("audio/ogg");
	    options.continuous(true);
	    options.interimResults(true);
	    
	    // initialize concept insights service
	    
		logger.info("initialize concept insights service");
		
	    final ConceptInsights conceptInsightsService = new ConceptInsights();
	    conceptInsightsService.setEndPoint("https://gateway.watsonplatform.net/concept-insights/api");
	    conceptInsightsService.setUsernameAndPassword("a425694b-f06a-4956-886d-e2e9e66d7c65", "FPhRNXvLnJ9m");
	    
	    final String name = "devoxx_corpus1";
	    final String account = conceptInsightsService.getFirstAccountId();
	    
	    Corpus corpus = new Corpus(account, name);
	    corpus.addAccountPermissions(new AccountPermission(account, Permission.READ_WRITE_ADMIN));
	    
	    // store file contents to temporary file

		logger.info("store file contents to temporary file");
		
	    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	    InputStream fileContentInputStream = filePart.getInputStream();
	    
	    File tempFile = File.createTempFile("temp-file-name", null);
	    
	    FileUtils.copyInputStreamToFile(fileContentInputStream, tempFile);
	    
	    // get speech results
	    
		logger.info("get speech results");
		
	    SpeechResults speechResults = null;
		try
		{
			speechResults = speechToTextService.recognize(tempFile, options);
			
			logger.info("got speech results");
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	    
	    // create document

		logger.info("create document");
		
	    Document newDocument;

		newDocument = new Document(corpus, UUID.randomUUID().toString());
		newDocument.setLabel("Loaded from web interface.");
		
		Map<String, String> userFields = new HashMap<>();
		userFields.put("link", request.getParameter("link"));
		newDocument.setUserFields(userFields);

		int partCount;
		for (partCount = 0; partCount < speechResults.getResults().size(); partCount++)
		{
			Transcript transcript = speechResults.getResults().get(partCount);

			newDocument.addParts(new com.ibm.watson.developer_cloud.concept_insights.v2.model.Part("part_" + partCount, transcript.getAlternatives().get(0).getTranscript(), HttpMediaType.TEXT_PLAIN));

		}	    
	    
		conceptInsightsService.createDocument(newDocument);
		newDocument = conceptInsightsService.getDocument(newDocument);
		newDocument.setTimeToLive(3600);
		conceptInsightsService.updateDocument(newDocument);
		
		response.setContentType("text/html");
		response.getWriter().print("Number of parts loaded: " + partCount + ".");
		
	}

}

