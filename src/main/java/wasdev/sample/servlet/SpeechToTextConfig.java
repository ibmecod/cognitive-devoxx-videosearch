package wasdev.sample.servlet;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;

/**
 * @author Stephan Janssen
 */
class SpeechToTextConfig {

    // TODO Externalise the credentials using Spring Config
    private static final String SPEECH_TO_TEXT_END_POINT = "https://stream.watsonplatform.net/speech-to-text/api";
    private static final String SPEECH_TO_TEXT_USERNAME = "d2334b1a-4c18-41df-8dab-3659c0dbfb3d";
    private static final String SPEECH_TO_TEXT_PASSWORD = "dRGqpp6tvCXD";

    public SpeechToText getService() {
        final SpeechToText speechToTextService = new SpeechToText();
        speechToTextService.setUsernameAndPassword(SPEECH_TO_TEXT_USERNAME, SPEECH_TO_TEXT_PASSWORD);
        speechToTextService.setEndPoint(SPEECH_TO_TEXT_END_POINT);
        return speechToTextService;
    }
}
