package io.testrex.plugin;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
     * REST API URL.
     */
    private String apiUrl;

    /**
     * HttpClient for communication.
     */
    private HttpClient httpClient;

    /**
     * Access Token of user.
     */
    private String accessToken;

    /**
     * Create connector to secured url with given HttpClient.
     * The given AccessToken is used in HttpRequest Authorization header.
     *
     * @param apiUrl api URL
     * @param httpClient HttpClient
     * @param accessToken Access Token
     */
    public TestrexConnectorImpl(final String apiUrl, final HttpClient httpClient, final String accessToken) {
        this.apiUrl = apiUrl;
        this.httpClient = httpClient;
        this.accessToken = accessToken;
    }

    /**
     * Create connector to unsecured url with given HttpClient.
     *
     * @param apiUrl api URL
     * @param httpClient HttpClient
     */
    public TestrexConnectorImpl(final String apiUrl, final HttpClient httpClient) {
        this.apiUrl = apiUrl;
        this.httpClient = httpClient;
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

        if (accessToken != null) {
            post.setHeader("Authorization", "Bearer " + accessToken);
        }

        post.setEntity(new FileEntity(file));

        try {
            HttpResponse response = httpClient.execute(post);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                throw new TestrexConnectionException("Failed to send file: '" + file.getName() + "'."
                        + " Status code: " + response.getStatusLine().getStatusCode()
                        + ", Message: " + response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            throw new TestrexConnectionException("Failed to send file: '" + file.getName() + "'", e);
        }
    }
}
