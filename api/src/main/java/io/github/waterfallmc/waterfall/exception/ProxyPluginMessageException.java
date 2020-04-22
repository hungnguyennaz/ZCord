package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Thrown when an incoming plugin message channel throws an exception
 */
public class ProxyPluginMessageException extends ProxyPluginException {

    private final ProxiedPlayer player;
    private final String channel;
    private final byte[] data;

    public ProxyPluginMessageException(String message, Throwable cause, Plugin responsiblePlugin, ProxiedPlayer player, String channel, byte[] data) {
        super(message, cause, responsiblePlugin);
        this.player = checkNotNull(player, "player");
        this.channel = checkNotNull(channel, "channel");
        this.data = checkNotNull(data, "data");
    }

    public ProxyPluginMessageException(Throwable cause, Plugin responsiblePlugin, ProxiedPlayer player, String channel, byte[] data) {
        super(cause, responsiblePlugin);
        this.player = checkNotNull(player, "player");
        this.channel = checkNotNull(channel, "channel");
        this.data = checkNotNull(data, "data");
    }

    protected ProxyPluginMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin, ProxiedPlayer player, String channel, byte[] data) {
        super(message, cause, enableSuppression, writableStackTrace, responsiblePlugin);
        this.player = checkNotNull(player, "player");
        this.channel = checkNotNull(channel, "channel");
        this.data = checkNotNull(data, "data");
    }

    /**
     * Gets the channel to which the error occurred from recieving data from
     *
     * @return exception channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the data to which the error occurred from
     *
     * @return exception data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Gets the player which the plugin message causing the exception originated from
     *
     * @return exception player
     */
    public ProxiedPlayer getPlayer() {
        return player;
    }
}
