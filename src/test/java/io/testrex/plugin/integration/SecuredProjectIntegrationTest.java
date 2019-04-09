package io.testrex.plugin.integration;

import org.apache.http.HttpStatus;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class SecuredProjectIntegrationTest {

    private Verifier verifier;

    private static ClientAndServer testrexMockServer;
    private static ClientAndServer keycloakMockServer;

    @BeforeAll
    static void startServer() {
        testrexMockServer = ClientAndServer.startClientAndServer(8180);
        keycloakMockServer = ClientAndServer.startClientAndServer(8080);
    }

    @AfterAll
    static void stopServer() {
        testrexMockServer.stop();
        keycloakMockServer.stop();
    }

    @BeforeEach
    void setUp() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/secured-test-project");

        verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("io.testrex", "testrex-maven-plugin-test-project", "1.0-SNAPSHOT", "jar");
    }

    @AfterEach
    void tearDown() {
        verifier.resetStreams();
        testrexMockServer.reset();
        keycloakMockServer.reset();
    }

    @Test
    void testPluginSendsDataToServer() throws Exception {
        String accessToken = "testAccessToken";

        testrexMockServer.when(HttpRequest
                .request()
                    .withMethod("POST")
                    .withPath("/projects/1/testsuites"),
                Times.unlimited())
                .respond(HttpResponse
                        .response()
                        .withStatusCode(HttpStatus.SC_CREATED)
                );

        keycloakMockServer.when(HttpRequest
                .request()
                    .withMethod("POST")
                    .withPath("/auth/realms/dev/protocol/openid-connect/token"),
                Times.unlimited())
                .respond(HttpResponse
                        .response()
                        .withStatusCode(HttpStatus.SC_OK)
                        .withBody("{\"access_token\":\"" + accessToken + "\"}"));

        verifier.addCliOption("-N");
        verifier.executeGoal("test");

        verifier.verify(false);
        testrexMockServer.verify(HttpRequest
                .request()
                    .withHeader("Authorization", "Bearer " + accessToken),
                VerificationTimes.atLeast(1));
    }

    @Test
    void testBuildFailsWhenKeycloakServerIsDown() throws Exception {
        verifier.addCliOption("-N");

        assertThatExceptionOfType(VerificationException.class).isThrownBy(() -> verifier.executeGoal("test"));

        verifier.verifyTextInLog("[ERROR] Failed to authorize user.");
    }
}
