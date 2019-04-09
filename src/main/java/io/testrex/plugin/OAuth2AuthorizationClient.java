package io.testrex.plugin;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class OAuth2AuthorizationClient implements AuthorizationClient {

    /**
     * Url where can be obtained access token.
     */
    private final String url;

    /**
     * Client id of the maven plugin in Keycloak.
     */
    private final String clientId;

    /**
     * Http client.
     */
    private final HttpClient httpClient;

    /**
     * Authorization client for KeyCloak.
     *
     * @param url url where the Access Token can be obtained
     * @param clientId id of the client
     * @param httpClient http client
     */
    public OAuth2AuthorizationClient(final String url, final String clientId, final HttpClient httpClient) {
        this.url = url;
        this.clientId = clientId;
        this.httpClient = httpClient;
    }

    @Override
    public final String authorizeUser(
            final String username,
            final String password)
            throws AuthorizationFailedException {

        HttpPost post = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
        nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));

        post.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));

        try {
            HttpResponse response = httpClient.execute(post);

            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject object = new JSONObject(jsonString);

            return object.get("access_token").toString();
        } catch (IOException e) {
            throw new AuthorizationFailedException("Failed to authorize user.", e);
        } catch (JSONException e) {
            throw new AuthorizationFailedException(
                    "Data returned by the Authorization Server do not contain field 'access_token'.");
        }
    }
}
