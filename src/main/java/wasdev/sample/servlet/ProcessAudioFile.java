package wasdev.sample.servlet;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.AccountPermission;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Corpus;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Part;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static wasdev.sample.servlet.Constant.DEVOXX_CORPUS;

/**
 * @author Stephan Janssen
 */

class ProcessAudioFile {

    private static final Logger LOGGER = Logger.getLogger(ProcessAudioFile.class.getName());

    /**
     * Process the audio file asynchronously.
     *
     * @param audioFile     the audio file
     * @param documentName  the entry documentName
     * @param youTubeLink   the YouTube link
     */
    @Async
    public void execute(final File audioFile,
                        final String documentName,
                        final String youTubeLink) {

        // Start speech to text process
        SpeechResults speechResults = processSpeechToText(audioFile);

        // initialize concept insights service
        processConceptInsights(documentName, youTubeLink, speechResults);

        if (audioFile.delete()) {
            LOGGER.log(Level.INFO, "File {0} removed", audioFile.getName());
        }
    }

    /**
     * Start speech to text process.
     *
     * @param audioFile the audio file
     */
    private SpeechResults processSpeechToText(final File audioFile) {
        LOGGER.info("process speech to text service");

        final RecognizeOptions options = new RecognizeOptions();
        options.contentType("audio/ogg");
        options.continuous(true);
        options.interimResults(false);

        SpeechResults speechResults = null;
        // get speech results
        LOGGER.info("get speech results");
        try {
            speechResults = new SpeechToTextConfig().getService().recognize(audioFile, options);
            LOGGER.log(Level.INFO, "got speech results (index={0})", speechResults.getResultIndex());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getCause().toString());
        }

        return speechResults;
    }

    /**
     * Concept insights service.
     *
     * @param name the audio documentName
     * @param link the YouTube link
     */
    private void processConceptInsights(final String name,
                                        final String link,
                                        final SpeechResults speechResults) {
        LOGGER.info("initialize concept insights service");
        final ConceptInsights conceptInsights = new ConceptInsightsConfig().getService();

        LOGGER.info("create corpus");
        final String account = conceptInsights.getFirstAccountId();

        final Corpus corpus = new Corpus(account, DEVOXX_CORPUS);
        corpus.addAccountPermissions(new AccountPermission(account, AccountPermission.Permission.READ_WRITE_ADMIN));

        LOGGER.info("create document");
        Document newDocument = new Document(corpus, UUID.randomUUID().toString());
        newDocument.setName(name);
        newDocument.setLabel(name);

        final Map<String, String> userFields = new HashMap<>();
        userFields.put("youTubeLink", link);
        newDocument.setUserFields(userFields);

        LOGGER.info("Add speech results into document");
        StringBuilder stringBuilder = new StringBuilder();
        for (Transcript transcript : speechResults.getResults()) {
            stringBuilder.append(transcript.getAlternatives().get(0).getTranscript());
        }
        
        
		String recognizedText = stringBuilder.toString();
		LOGGER.info("Transcript:");
		LOGGER.info(recognizedText);

        newDocument.addParts(new Part("part_", recognizedText, HttpMediaType.TEXT_PLAIN));

        conceptInsights.createDocument(newDocument);

        // Why is the follow step needed?  GET / UPDATE

        //LOGGER.info("get document");
        //Document foundDocument = conceptInsights.getDocument(newDocument);

        //LOGGER.info("update document");
        //conceptInsights.updateDocument(foundDocument );
    }
}
