package io.github.waterfallmc.waterfall.event;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.plugin.Cancellable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import lombok.ToString;

/**
 * Represents a brand new connection made to the proxy, allowing for plugins to
 * efficiently close a connection, useful for connection throttlers, etc
 */
@ToString
public class ConnectionInitEvent extends AsyncEvent<ConnectionInitEvent> implements Cancellable {

    private final SocketAddress remoteAddress;
    private final ListenerInfo listener;
    private boolean isCancelled = false;

    public ConnectionInitEvent(SocketAddress remoteAddress, ListenerInfo listener, Callback<ConnectionInitEvent> done) {
        super(done);
        this.remoteAddress = remoteAddress;
        this.listener = listener;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    /**
     * @return the INetSocketAddress of the connection being opened
     * @deprecated BungeeCord can accept connections via Unix domain sockets
     */
    @Deprecated
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) remoteAddress;
    }

    /**
     * @return the SocketAddress of the connection being opened
     */
    public SocketAddress getRemoteSocketAddress() {
        return remoteAddress;
    }
}
