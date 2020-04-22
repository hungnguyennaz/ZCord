package io.github.waterfallmc.waterfall.exception;

import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import net.md_5.bungee.api.ProxyServer;

/**
 * Thrown when the internal server throws a recoverable exception.
 */
public class ProxyInternalException extends ProxyException {

    public ProxyInternalException(String message) {
        super(message);
    }

    public ProxyInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyInternalException(Throwable cause) {
        super(cause);
    }

    protected ProxyInternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static void reportInternalException(Throwable cause) {
        try {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyExceptionEvent(new ProxyInternalException(cause)));;
        } catch (Throwable t) {
            t.printStackTrace(); // Don't want to rethrow!
        }
    }
}
