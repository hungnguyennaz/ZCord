package io.github.waterfallmc.waterfall.conf;

import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;

import java.io.File;

public class WaterfallConfiguration extends Configuration {

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
        // Throttling options
        tabThrottle = config.getInt("throttling.tab_complete", tabThrottle);
        disableModernTabLimiter = config.getBoolean("disable_modern_tab_limiter", disableModernTabLimiter);
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
