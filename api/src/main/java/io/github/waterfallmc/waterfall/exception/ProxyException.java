package io.github.waterfallmc.waterfall.exception;

/**
 * Wrapper exception for all exceptions that are thrown by the server.
 */
public class ProxyException extends Exception {

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }

    protected ProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
