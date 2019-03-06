package io.testrex.plugin;

import java.io.File;

/**
 * Interface representing connector to Testrex server.
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public interface TestrexConnector {

    /**
     * Send report file to the server for project with given id.
     *
     * @param file report file
     * @param projectId id of project
     * @throws TestrexConnectionException if occurs any connection problem with Testrex server.
     */
    void sendReportFile(File file, int projectId) throws TestrexConnectionException;
}
