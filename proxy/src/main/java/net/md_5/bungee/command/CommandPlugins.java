package net.md_5.bungee.command;

import java.util.Collection;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class CommandPlugins
extends Command {
    public CommandPlugins() {
        super("gplugins", "bungeecord.command.plugins");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(this.getPluginList());
    }

    private BaseComponent[] getPluginList() {
        Collection<Plugin> plugins = BungeeCord.getInstance().getPluginManager().getPlugins();
        ComponentBuilder builder = new ComponentBuilder();
        builder.append("Plugins (" + plugins.size() + "):§a ");
        boolean firstIteration = true;
        for (Plugin plugin : plugins) {
            if (firstIteration) {
                firstIteration = false;
            } else {
                builder.append(", ").color(ChatColor.WHITE);
            }
            builder.append(plugin.getDescription().getName()).color(ChatColor.GREEN);
        }
        return builder.create();
    }
}
