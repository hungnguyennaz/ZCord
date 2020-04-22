package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.plugin.Plugin;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper exception for all cases to which a plugin can be immediately blamed for
 */
public class ProxyPluginException extends ProxyException {
    public ProxyPluginException(String message, Throwable cause, Plugin responsiblePlugin) {
        super(message, cause);
        this.responsiblePlugin = checkNotNull(responsiblePlugin, "responsiblePlugin");
    }

    public ProxyPluginException(Throwable cause, Plugin responsiblePlugin) {
        super(cause);
        this.responsiblePlugin = checkNotNull(responsiblePlugin, "responsiblePlugin");
    }

    protected ProxyPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Plugin responsiblePlugin) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.responsiblePlugin = checkNotNull(responsiblePlugin, "responsiblePlugin");
    }

    private final Plugin responsiblePlugin;

    /**
     * Gets the plugin which is directly responsible for the exception being thrown
     *
     * @return plugin which is responsible for the exception throw
     */
    public Plugin getResponsiblePlugin() {
        return responsiblePlugin;
    }
}
