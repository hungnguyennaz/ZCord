package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * Thrown whenever there is an exception with any enabling or disabling of plugins.
 */
public class ProxyPluginEnableDisableException extends ProxyPluginException {
    public ProxyPluginEnableDisableException(String message, Throwable cause, Plugin responsiblePlugin) {
        super(message, cause, responsiblePlugin);
    }

    public ProxyPluginEnableDisableException(Throwable cause, Plugin responsiblePlugin) {
        super(cause, responsiblePlugin);
    }

    protected ProxyPluginEnableDisableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin) {
        super(message, cause, enableSuppression, writableStackTrace, responsiblePlugin);
    }
}