package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandBungee
extends Command {
    private final String message;

    public CommandBungee() {
        super("bungee");
        this.message = ChatColor.translateAlternateColorCodes('&', "&8> &7This server is running &6ZCord &7" + "1.8" + " (1.8.x - 1.19.x)\n&8> &7A free &6BungeeCord fork&7 made by HungNguyenAZ\n&8> &7Resource: https://zcord.2hg.pw");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage((BaseComponent)new TextComponent(this.message));
    }
}