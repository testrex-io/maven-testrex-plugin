package io.testrex.plugin;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class AuthorizationFailedException extends TestrexConnectionException {

    /**
     * Creates authorization failed exception with given message.
     *
     * @param message message
     */
    public AuthorizationFailedException(final String message) {
        super(message);
    }


    /**
     * Creates authorization failed exception with given message and cause.
     *
     * @param message message
     * @param cause cause of the exception
     */
    public AuthorizationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
