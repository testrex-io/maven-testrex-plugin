package io.testrex.plugin;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class SurefireReportFileLoader implements ReportFileLoader {

    /**
     * Name of the xsd schema file.
     */
    private static final String SUREFIRE_TEST_REPORT_XSD_FILE_NAME = "surefire-test-report.xsd";

    @Override
    public final File[] loadReportFiles(final File reportsDirectory) throws IOException {
        if (reportsDirectory == null) {
            throw new NullPointerException("Specified directory is null.");
        }

        if (!reportsDirectory.exists()) {
            throw new IOException("Failed to open surefire report directory.");
        }

        return reportsDirectory.listFiles(this::isValidSurefireReportFile);
    }

    /**
     * Verifies that the given file is correct surefire test report file.
     *
     * @param file tested file
     * @return true, if the given file is valid surefire test report file, false otherwise
     */
    private boolean isValidSurefireReportFile(final File file) {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream xsdInputStream = classLoader.getResourceAsStream(SUREFIRE_TEST_REPORT_XSD_FILE_NAME);

        if (xsdInputStream == null) {
            throw new RuntimeException("The surefire-test-report.xsd file was not found.");
        }

        Source xsdSource = new StreamSource(xsdInputStream);
        Source xmlSource = new StreamSource(file);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            Schema schema = schemaFactory.newSchema(xsdSource);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);
            return true;
        } catch (SAXException e) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException("An IOException occurred during verification of file: " + file, e);
        }
    }
}
