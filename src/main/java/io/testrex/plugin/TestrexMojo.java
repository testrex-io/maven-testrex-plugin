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
        File[] files = getReportFiles();

        HttpClient httpClient = HttpClientBuilder.create().build();
        TestrexConnector connector = new TestrexConnectorImpl(url, httpClient);

        for (File file : files) {
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

    /**
     * Finds report files in reports directory.
     *
     * @return report files
     * @throws MojoFailureException if the reports directory is invalid
     */
    private File[] getReportFiles() throws MojoFailureException {
        if (reportsDirectory == null) {
            throw new MojoFailureException("Failed to open surefire report directory.");
        }

        if (!reportsDirectory.exists()) {
            getLog().warn("No surefire report files has been found.");
            return new File[0];
        }

        return reportsDirectory.listFiles((file, s) -> s.toLowerCase().endsWith(".xml"));
    }
}
