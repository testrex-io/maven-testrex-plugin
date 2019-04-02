package io.testrex.plugin;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Class with tests for {@link TestrexConnectorImpl}.
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
class TestrexConnectorTest {

    /**
     * Testrex connector instance to be tested.
     */
    private TestrexConnector testrexConnector;

    /**
     * Test REST API URL
     */
    private static final String testApiUrl = "http://test.io";

    /**
     * Mocked HttpClient
     */
    private HttpClient mockedHttpClient = mock(HttpClient.class);

    @BeforeEach
    void setUp() {
        testrexConnector = new TestrexConnectorImpl(testApiUrl, mockedHttpClient);
    }

    @Test
    void testSendReportFileCorrectly() throws Exception {
        File file = new File("test");
        int projectId = 1;

        HttpResponse mockedResponse = getSuccessfulMockedResponse();
        ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);

        when(mockedHttpClient.execute(any(HttpPost.class))).thenReturn(mockedResponse);

        testrexConnector.sendReportFile(file, projectId);

        verify(mockedHttpClient).execute(argument.capture());

        HttpPost post = argument.getValue();

        Header header = post.getFirstHeader("Content-Type");
        assertThat(header.getValue()).isEqualTo("application/xml");

        assertThat(post.getEntity()).isInstanceOf(FileEntity.class);
        FileEntity fileEntity = (FileEntity)post.getEntity();
        assertThat(fileEntity).isEqualToComparingFieldByField(new FileEntity(file));

        assertThat(post.getURI().toString())
                .isEqualTo(testApiUrl + "/projects/" + projectId + "/testsuites");
    }

    @Test
    void testSendReportFileFailsOnServerError() throws Exception {
        File file = new File("test");
        int projectId = 1;

        Exception connectionRefusedException = new IOException("Connection refused");

        when(mockedHttpClient.execute(any(HttpPost.class))).thenThrow(connectionRefusedException);

        assertThatExceptionOfType(TestrexConnectionException.class)
                .isThrownBy(() -> testrexConnector.sendReportFile(file, projectId))
                .withCause(connectionRefusedException);
    }

    @Test
    void testSendReportFileFailsOnInvalidResponseStatus() throws Exception {
        File file = new File("test");
        int projectId = 1;

        HttpResponse mockedResponse = getUnsuccessfulMockedResponse();

        when(mockedHttpClient.execute(any(HttpPost.class))).thenReturn(mockedResponse);

        assertThatExceptionOfType(TestrexConnectionException.class)
                .isThrownBy(() -> testrexConnector.sendReportFile(file, projectId));
    }

    /**
     * Returns mock of unsuccessful HttpResponse
     *
     * @return unsuccessful HttpResponse
     */
    private HttpResponse getUnsuccessfulMockedResponse() {
        HttpResponse mockedResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);

        when(mockedResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);

        return mockedResponse;
    }

    /**
     * Returns mock of success HttpResponse
     *
     * @return successful HttpResponse
     */
    private HttpResponse getSuccessfulMockedResponse() {
        HttpResponse mockedResponse = mock(HttpResponse.class, RETURNS_DEEP_STUBS);

        when(mockedResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_CREATED);

        return mockedResponse;
    }
}
