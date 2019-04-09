package io.testrex.plugin;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public interface AuthorizationClient {


    /**
     * Authorize user against AuthorizationServer and return Access Token.
     *
     * @param username username
     * @param password password
     * @return Access Token for given user
     * @throws AuthorizationFailedException when the authorization fails due to invalid username, password or connection
     */
    String authorizeUser(String username, String password) throws AuthorizationFailedException;
}
