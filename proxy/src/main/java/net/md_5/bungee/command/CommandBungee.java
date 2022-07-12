package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandBungee extends Command
{

    public CommandBungee()
    {
        super( "bungee" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        sender.sendMessage( "§8=================================================" );
        sender.sendMessage( "§bThis server is using §cZCord" );
        sender.sendMessage( "§bA proxy software made by a very handsome guy named §eHungNguyenAZ" );
        sender.sendMessage( "§8=================================================" );
    }
}
