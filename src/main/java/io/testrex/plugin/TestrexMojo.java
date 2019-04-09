package io.testrex.plugin;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Mojo(name = "send", defaultPhase = LifecyclePhase.TEST)
public class TestrexMojo extends AbstractMojo {

    /**
     * Directory with report files.
     */
    @Parameter(defaultValue = "${project.build.directory}/surefire-reports/")
    private File reportsDirectory;

    /**
     * Url to Testrex REST API.
     */
    @Parameter(defaultValue = "http://localhost:8080")
    private String url;

    /**
     * Url to authorization server token.
     */
    @Parameter(defaultValue = "http://localhost:8080/auth/realms/dev/protocol/openid-connect/token")
    private String oAuthTokenUrl;

    /**
     * Client id of the plugin in authorization server.
     */
    @Parameter(defaultValue = "testrex-maven-plugin")
    private String authClientId;

    /**
     * ID of project in Testrex.
     */
    @Parameter(required = true)
    private int projectId;

    /**
     * Is the testrex server secured.
     */
    @Parameter(defaultValue = "true")
    private boolean authentication;

    /**
     * Username used for authentication.
     */
    @Parameter()
    private String username;

    /**
     * Password used for authenrication.
     */
    @Parameter()
    private String password;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        ReportFileLoader fileLoader = new SurefireReportFileLoader();
        File[] files;

        try {
            files = fileLoader.loadReportFiles(reportsDirectory);
        } catch (IOException e) {
            throw new MojoFailureException("Cannot load surefire report files.", e);
        }

        getLog().info("Found: " + files.length + " report file(s).");

        HttpClient httpClient = HttpClientBuilder.create().build();

        TestrexConnector connector;

        if (authentication) {
            connector = createSecuredConnector(httpClient);
        } else {
            connector = new TestrexConnectorImpl(url, httpClient);
        }

        sendReportFiles(files, connector);
    }

    /**
     * Creates TestrexConnector that is secured by Oauth2.
     *
     * @param httpClient HttpClient
     * @return secured TestrexConnector
     * @throws MojoExecutionException when the authorization fails due to invalid username, password or
     *                                connection problems.
     */
    private TestrexConnector createSecuredConnector(final HttpClient httpClient) throws MojoExecutionException {
        if (username == null || username.isEmpty()) {
            throw new MojoExecutionException(
                    "No username provided. Provide username or set 'authentication' to 'false'.");
        }
        if (password == null || password.isEmpty()) {
            throw new MojoExecutionException(
                    "No password provided. Provide password or set 'authentication' to 'false'.");
        }

        AuthorizationClient authorizationClient =
                new OAuth2AuthorizationClient(oAuthTokenUrl, authClientId, httpClient);

        try {
            String accessToken = authorizationClient.authorizeUser(username, password);

            return new TestrexConnectorImpl(url, httpClient, accessToken);
        } catch (AuthorizationFailedException e) {

            getLog().error("Failed to authorize user.");

            throw new MojoExecutionException("Failed to authorize user.", e);
        }
    }

    /**
     * Send given report files via given connector to the Testrex server.
     *
     * @param reportFiles report files to be send
     * @param connector connector to Testrex server
     * @throws MojoExecutionException when the send operation fails
     */
    private void sendReportFiles(final File[] reportFiles, final TestrexConnector connector)
            throws MojoExecutionException {

        for (File file : reportFiles) {
            String fileName = file.getName();

            getLog().info("Sending file: '" + fileName + "'.");

            try {
                connector.sendReportFile(file, projectId);

                getLog().info("File: '" + fileName + "' send successfully.");
            } catch (TestrexConnectionException e) {
                getLog().error("Failed to send file: '" + fileName + "'.");

                throw new MojoExecutionException("Failed to send file.", e);
            }
        }
    }
}
