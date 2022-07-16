package io.github.waterfallmc.waterfall.event;

import com.google.common.base.Preconditions;
import io.github.waterfallmc.waterfall.exception.ProxyException;
import net.md_5.bungee.api.plugin.Event;

/**
 * Called whenever an exception is thrown in a recoverable section of the server.
 */
public class ProxyExceptionEvent extends Event {

    private ProxyException exception;

    public ProxyExceptionEvent(ProxyException exception) {
        this.exception = Preconditions.checkNotNull(exception, "exception");
    }

    /**
     * Gets the wrapped exception that was thrown.
     *
     * @return Exception thrown
     */
    public ProxyException getException() {
        return exception;
    }

}
