package io.github.waterfallmc.waterfall.conf;

import com.google.common.base.Joiner;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.io.File;

public class WaterfallConfiguration extends Configuration {

    /**
     * The supported versions displayed to the client
     * <p>Default is a comma separated list of supported versions. For example 1.8.x, 1.9.x, 1.10.x</p>
     */
    private String gameVersion;

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

    @Override
    public void load() {
        super.load();
        YamlConfig config = new YamlConfig(new File("waterfall.yml"));
        config.load(false); // Load, but no permissions
        gameVersion = config.getString("game_version", "").isEmpty() ? Joiner.on(", ").join(ProtocolConstants.SUPPORTED_VERSIONS) : config.getString("game_version", "");
        // Throttling options
        tabThrottle = config.getInt("throttling.tab_complete", tabThrottle);
        disableModernTabLimiter = config.getBoolean("disable_modern_tab_limiter", disableModernTabLimiter);
    }

    @Override
    public String getGameVersion() {
        return gameVersion;
    }

    @Override
    public int getTabThrottle() {
        return tabThrottle;
    }

    @Override
    public boolean isDisableModernTabLimiter() {
        return disableModernTabLimiter;
    }
}
