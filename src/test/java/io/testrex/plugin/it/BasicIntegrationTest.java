package io.testrex.plugin.it;

import org.apache.http.HttpStatus;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
class BasicIntegrationTest {


    private Verifier verifier;

    private static ClientAndServer mockServer;

    @BeforeAll
    static void startServer() {
        mockServer = ClientAndServer.startClientAndServer(8080);
    }

    @AfterAll
    static void stopServer() {
        mockServer.stop();
    }

    @BeforeEach
    void setUp() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/project-to-test");

        verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("io.testrex", "testrex-maven-plugin-test-project", "1.0-SNAPSHOT", "jar");
    }

    @AfterEach
    void tearDown() {
        verifier.resetStreams();
    }

    @Test
    void testPluginSendsDataToServer() throws Exception {
        mockServer.when(HttpRequest
                .request()
                .withMethod("POST")
                .withPath("/projects/1/testsuites"),
                Times.unlimited())
                .respond(
                        HttpResponse.response()
                        .withStatusCode(HttpStatus.SC_CREATED)
        );

        verifier.addCliOption("-N");
        verifier.executeGoal("test");

        verifier.verify(false);
        mockServer.verify(HttpRequest.request(), VerificationTimes.atLeast(1));
    }

    @Test
    void testBuildFailsWhenServerIsDown() throws Exception {
        verifier.addCliOption("-N");

        assertThatExceptionOfType(VerificationException.class).isThrownBy(() -> verifier.executeGoal("test"));

        verifier.verifyTextInLog("[ERROR] Failed to send file: 'TEST-test.testproject.SampleTest.xml'.");
    }
}
