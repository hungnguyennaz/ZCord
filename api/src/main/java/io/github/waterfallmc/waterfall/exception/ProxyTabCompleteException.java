package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Called when a tab-complete request throws an exception
 */
public class ProxyTabCompleteException extends ProxyCommandException {

    public ProxyTabCompleteException(String message, Throwable cause, Command command, CommandSender commandSender, String[] arguments) {
        super(message, cause, command, commandSender, arguments);
    }

    public ProxyTabCompleteException(Throwable cause, Command command, CommandSender commandSender, String[] arguments) {
        super(cause, command, commandSender, arguments);
    }

    protected ProxyTabCompleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Command command, CommandSender commandSender, String[] arguments) {
        super(message, cause, enableSuppression, writableStackTrace, command, commandSender, arguments);
    }
}
