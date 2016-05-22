package wasdev.sample.servlet;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/processAudioFile")
@MultipartConfig
public class UploadAudioFileServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UploadAudioFileServlet.class.getName());

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {

        // Get required parameters
        final String youTubeLink = request.getParameter("link");
        final String documentName = request.getParameter("documentName");
        LOGGER.info("link= " + youTubeLink);
        LOGGER.info("documentName= " + documentName);

        // Get audio file
        File tempFile = createTempFile(request.getPart("file"));

        // Process audio file asynchronously
        new ProcessAudioFile().execute(tempFile, documentName, youTubeLink);

        // Reply to user
        response.setContentType("text/html");
        response.getWriter().print("Processing");
    }


    private File createTempFile(final Part filePart) throws IOException, ServletException {
        // Retrieves <input type="file" name="file">
        InputStream fileContentInputStream = filePart.getInputStream();
        File tempFile = File.createTempFile("temp-file-name", null);
        FileUtils.copyInputStreamToFile(fileContentInputStream, tempFile);
        return tempFile;
    }
}
