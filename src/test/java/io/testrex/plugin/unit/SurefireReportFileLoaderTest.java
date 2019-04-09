package io.testrex.plugin.unit;

import io.testrex.plugin.SurefireReportFileLoader;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SurefireReportFileLoader}.
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
class SurefireReportFileLoaderTest {

    /**
     * SurefireReportFileLoader instance for testing.
     */
    private SurefireReportFileLoader surefireReportFileLoader = new SurefireReportFileLoader();

    /**
     * Spy of report file directory.
     */
    private File mockedReportFileDirectory;


    @BeforeEach
    void setUp() {
        mockedReportFileDirectory = spy(new File(Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResource("./"))
                .getFile()));
    }

    @Test
    void testLoadReportFilesFailsWithNotExistingDirectory() {

        when(mockedReportFileDirectory.exists()).thenReturn(false);

        assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> surefireReportFileLoader.loadReportFiles(mockedReportFileDirectory));
    }

    @Test
    void testLoadReportFilesDoesntLoadIncorrectFile() throws Exception {
        String nameOfIncorrectReportFile = "SomeFile.txt";

        when(mockedReportFileDirectory.exists()).thenReturn(true);
        when(mockedReportFileDirectory.list()).thenReturn(new String[]{nameOfIncorrectReportFile});

        Condition<File> incorrectReportFileCondition = new Condition<>(file ->
                nameOfIncorrectReportFile.equals(file.getName()), "Incorrect report file.");

        File[] foundReportFiles = surefireReportFileLoader.loadReportFiles(mockedReportFileDirectory);

        assertThat(foundReportFiles).doNotHave(incorrectReportFileCondition);
    }

    @Test
    void testLoadReportFilesDoesntLoadInvalidSurefireReportFile() throws Exception {
        String nameOfIncorrectReportFile = "invalid-surefire-report-file.xml";

        File invalidReportFile = new File(Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResource(nameOfIncorrectReportFile))
                .getFile());

        when(mockedReportFileDirectory.exists()).thenReturn(true);
        when(mockedReportFileDirectory.list()).thenReturn(new String[]{invalidReportFile.getName()});

        Condition<File> invalidReportFileCondition = new Condition<>(file ->
                nameOfIncorrectReportFile.equals(file.getName()), "Invalid report file.");

        File[] foundReportFiles = surefireReportFileLoader.loadReportFiles(mockedReportFileDirectory);

        assertThat(foundReportFiles).doNotHave(invalidReportFileCondition);
    }

    @Test
    void testLoadReportFilesLoadsValidSurefireReportFile() throws Exception {
        String nameOfIncorrectReportFile = "valid-surefire-report-file.xml";

        File invalidReportFile = new File(Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResource(nameOfIncorrectReportFile))
                .getFile());

        when(mockedReportFileDirectory.exists()).thenReturn(true);
        when(mockedReportFileDirectory.list()).thenReturn(new String[]{invalidReportFile.getName()});

        Condition<File> invalidReportFileCondition = new Condition<>(file ->
                nameOfIncorrectReportFile.equals(file.getName()), "Valid report file.");

        File[] foundReportFiles = surefireReportFileLoader.loadReportFiles(mockedReportFileDirectory);

        assertThat(foundReportFiles).have(invalidReportFileCondition);
    }
}
