package io.github.waterfallmc.waterfall.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Called when the proxy intercepts the command packet
 * allowing for plugins to prevent commands being added
 * to the clients which might not be wanted.
 *
 *
 * <i>Plugin developers, please implement {@link Command#hasPermission(CommandSender)}
 * properly in favor of this event.</i>
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProxyDefineCommandsEvent extends TargetedEvent {


    /**
     * The map of commands to be sent to the player
     */
    private final Map<String, Command> commands;

    public ProxyDefineCommandsEvent(Connection sender, Connection receiver, Map<String, Command> commands) {
        super(sender, receiver);
        this.commands = commands;
    }

}
