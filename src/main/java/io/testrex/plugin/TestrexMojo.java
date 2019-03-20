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
    @Parameter
    private String url;

    /**
     * ID of project in Testrex.
     */
    @Parameter
    private int projectId;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        ReportFileLoader fileLoader = new SurefireReportFileLoader();
        File[] files;

        try {
            files = fileLoader.loadReportFiles(reportsDirectory);
        } catch (IOException e) {
            throw new MojoFailureException("Cannot load surefire report files.", e);
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        TestrexConnector connector = new TestrexConnectorImpl(url, httpClient);

        getLog().info("Found: " + files.length + " report files.");

        sendReportFiles(files, connector);
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
