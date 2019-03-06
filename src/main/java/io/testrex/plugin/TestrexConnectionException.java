package io.testrex.plugin;

/**
 * Exception representing problem with connection to the Testrex server.
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class TestrexConnectionException extends Exception {

    /**
     * Create exception with given message.
     *
     * @param message message
     */
    public TestrexConnectionException(final String message) {
        super(message);
    }

    /**
     * Create exception with given message and cause.
     *
     * @param message message
     * @param cause cause of this exception
     */
    public TestrexConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
