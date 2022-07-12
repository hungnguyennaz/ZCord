package net.md_5.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.protocol.packet.Handshake;

/**
 * Event called to represent a player first making their presence and username
 * known.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
// FlameCord - Implement cancellable
public class PlayerHandshakeEvent extends Event implements Cancellable {
    @Getter
    @Setter
    private boolean cancelled = false;

    /**
     * Connection attempting to login.
     */
    private final PendingConnection connection;
    /**
     * The handshake.
     */
    private final Handshake handshake;

    public PlayerHandshakeEvent(PendingConnection connection, Handshake handshake)
    {
        this.connection = connection;
        this.handshake = handshake;
    }
}
