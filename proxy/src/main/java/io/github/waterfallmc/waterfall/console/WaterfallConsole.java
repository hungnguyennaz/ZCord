package io.github.waterfallmc.waterfall.console;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public final class WaterfallConsole extends SimpleTerminalConsole {

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        ProxyServer proxy = ProxyServer.getInstance();
        return super.buildReader(builder
                .appName(proxy.getName())
                .completer(new ConsoleCommandCompleter(proxy))
        );
    }

    @Override
    protected boolean isRunning() {
        return BungeeCord.getInstance().isRunning;
    }

    @Override
    protected void runCommand(String command) {
        ProxyServer proxy = ProxyServer.getInstance();
        if (!proxy.getPluginManager().dispatchCommand(proxy.getConsole(), command)) {
            proxy.getConsole().sendMessage(new ComponentBuilder("Command not found").color(ChatColor.RED).create());
        }
    }

    @Override
    protected void shutdown() {
        ProxyServer.getInstance().stop();
    }

}
