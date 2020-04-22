package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Exception thrown when a server event listener throws an exception
 */
//TODO Find a better way to retrieve the plugin for this. Currently the event register/bake process
// doesnt leave a reference to the plugin so there's no way to know what one is being used at any given time
public class ProxyEventException extends ProxyException {

    private final Listener listener;
    private final Event event;

    public ProxyEventException(String message, Throwable cause, Listener listener, Event event) {
        super(message, cause);
        this.listener = checkNotNull(listener, "listener");
        this.event = checkNotNull(event, "event");
    }

    public ProxyEventException(Throwable cause, Listener listener, Event event) {
        super(cause);
        this.listener = checkNotNull(listener, "listener");
        this.event = checkNotNull(event, "event");
    }

    protected ProxyEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Listener listener, Event event) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.listener = checkNotNull(listener, "listener");
        this.event = checkNotNull(event, "event");
    }

    /**
     * Gets the listener which threw the exception
     *
     * @return event listener
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Gets the event which caused the exception
     *
     * @return event
     */
    public Event getEvent() {
        return event;
    }
}
