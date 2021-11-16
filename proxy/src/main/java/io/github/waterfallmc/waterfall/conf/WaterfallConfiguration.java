package io.github.waterfallmc.waterfall.conf;

import com.google.common.base.Joiner;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.File;

public class WaterfallConfiguration extends Configuration {

    /**
     * Whether we log InitialHandler connections
     * <p>
     * Default is true
     */
    private boolean logInitialHandlerConnections = true;

    /**
     * The supported versions displayed to the client
     * <p>Default is a comma separated list of supported versions. For example 1.8.x, 1.9.x, 1.10.x</p>
     */
    private String gameVersion;

    /**
     * Whether we use Netty's async DNS resolver for the HttpClient.
     * <p>Default is true (use Netty's async DNS resolver)</p>
     */
    private boolean useNettyDnsResolver = true;

    /*
     * Throttling options
     * Helps prevent players from overloading the servers behind us
     */

    /**
     * How often players are allowed to send tab throttle.
     * Value in milliseconds.
     * <p/>
     * Default is one packet per second.
     */
    private int tabThrottle = 1000;
    private boolean disableModernTabLimiter = true;

    private boolean disableEntityMetadataRewrite = false;
    private boolean disableTabListRewrite = true;

    /*
     * Plugin Message limiting options
     * Allows for more control over server-client communication
     */

    /**
     * How many channels there can be between server and player,
     * typically used by mods or some plugins.
     */
    private int pluginChannelLimit = 128;

    /**
     * How long the maximum channel name can be,
     * only reason to change it would be broken mods.
     */
    private int pluginChannelNameLimit = 128;

    @Override
    public void load() {
        super.load();
        YamlConfig config = new YamlConfig(new File("waterfall.yml"));
        config.load(false); // Load, but no permissions
        logInitialHandlerConnections = config.getBoolean( "log_initial_handler_connections", logInitialHandlerConnections );
        gameVersion = config.getString("game_version", "").isEmpty() ? Joiner.on(", ").join(ProtocolConstants.SUPPORTED_VERSIONS) : config.getString("game_version", "");
        useNettyDnsResolver = config.getBoolean("use_netty_dns_resolver", useNettyDnsResolver);
        // Throttling options
        tabThrottle = config.getInt("throttling.tab_complete", tabThrottle);
        disableModernTabLimiter = config.getBoolean("disable_modern_tab_limiter", disableModernTabLimiter);
        disableEntityMetadataRewrite = config.getBoolean("disable_entity_metadata_rewrite", disableEntityMetadataRewrite);
        disableTabListRewrite = config.getBoolean("disable_tab_list_rewrite", disableTabListRewrite);
        pluginChannelLimit = config.getInt("registered_plugin_channels_limit", pluginChannelLimit);
        pluginChannelNameLimit = config.getInt("plugin_channel_name_limit", pluginChannelNameLimit);
    }

    @Override
    public boolean isLogInitialHandlerConnections() {
        return logInitialHandlerConnections;
    }

    @Override
    public String getGameVersion() {
        return gameVersion;
    }

    @Override
    public boolean isUseNettyDnsResolver() {
        return useNettyDnsResolver;
    }

    @Override
    public int getTabThrottle() {
        return tabThrottle;
    }

    @Override
    public boolean isDisableModernTabLimiter() {
        return disableModernTabLimiter;
    }

    @Override
    public boolean isDisableEntityMetadataRewrite() {
        return disableEntityMetadataRewrite;
    }

    @Override
    public boolean isDisableTabListRewrite() {
        return disableTabListRewrite;
    }

    @Override
    public int getPluginChannelLimit() {
        return pluginChannelLimit;
    }

    @Override
    public int getPluginChannelNameLimit() {
        return pluginChannelNameLimit;
    }
}
