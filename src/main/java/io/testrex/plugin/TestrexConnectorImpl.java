package io.testrex.plugin;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;

import java.io.File;
import java.io.IOException;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Implementation of {@link TestrexConnector}.
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class TestrexConnectorImpl implements TestrexConnector {

    /**
     * Http created status.
     */
    private static final int HTTP_STATUS_CREATED = 201;

    /**
     * REST API URL.
     */
    private String apiUrl;

    /**
     * HttpClient for communication.
     */
    private HttpClient httpClient;

    /**
     * Create connector to given url with given HttpClient.
     *
     * @param url api URL
     * @param client HttpClient
     */
    public TestrexConnectorImpl(final String url, final HttpClient client) {
        this.apiUrl = url;
        this.httpClient = client;
    }

    @Override
    public final void sendReportFile(final File file, final int projectId) throws TestrexConnectionException {
        if (file == null) {
            throw new NullPointerException("File is null.");
        }

        String urlToAddReportFile = apiUrl + "/projects/" + projectId + "/testsuites";

        HttpPost post = new HttpPost(urlToAddReportFile);

        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Content-Type", "application/xml");

        post.setEntity(new FileEntity(file));

        try {
            HttpResponse response = httpClient.execute(post);

            if (response.getStatusLine().getStatusCode() != HTTP_STATUS_CREATED) {
                throw new TestrexConnectionException("Failed to send file: '" + file.getName() + "'."
                        + " Status code: " + response.getStatusLine().getStatusCode()
                        + ", Message: " + response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            throw new TestrexConnectionException("Failed to send file: '" + file.getName() + "'", e);
        }
    }
}
