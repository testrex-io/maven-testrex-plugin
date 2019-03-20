package io.testrex.plugin;

import java.io.File;
import java.io.IOException;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public interface ReportFileLoader {

    /**
     * Loads report files from specified report directory
     * that can be send to the Testrex server.
     *
     * @param reportsDirectory directory with report files
     * @return field of report files
     * @throws IOException if the specified directory can not be opened
     */
    File[] loadReportFiles(File reportsDirectory) throws IOException;
}
