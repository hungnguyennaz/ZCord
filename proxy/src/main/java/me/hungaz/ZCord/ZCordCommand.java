package me.hungaz.ZCord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import me.hungaz.ZCord.captcha.CaptchaGeneration;
import me.hungaz.ZCord.captcha.CaptchaGenerationException;
import me.hungaz.ZCord.config.Settings;

public class ZCordCommand extends Command
{

    public ZCordCommand()
    {
        super( "zcord");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if ( sender instanceof ProxiedPlayer )
        {
            sendStat( sender );
            return;
        }
        if ( args.length == 0 )
        {
            sender.sendMessage( "§8------------------------------------" );
            sender.sendMessage( "§b> Zcord reload §6- §aReload the config" );
            sender.sendMessage( "§b> ZCord stat §6- §aShow stats (can only be executed by a player)" );
            sender.sendMessage( "§b> ZCord export §6- §aUpload a list of players who have passed verification" );
            sender.sendMessage( "§b> ZCord protection on/off §6- §aOn/off underattacking mode" );
            sender.sendMessage( "§b> ZCord generate §6- §aGenerate a new captcha" );
            sender.sendMessage( "§8------------------------------------" );
        } else if ( args[0].equalsIgnoreCase( "reload" ) )
        {
            BungeeCord.getInstance().getZCord().disable();
            BungeeCord.getInstance().setZCord( new ZCord( false ) );
            sender.sendMessage( "§aExecuted!" );
        } else if ( args[0].equalsIgnoreCase( "stat" ) || args[0].equalsIgnoreCase( "stats" ) || args[0].equalsIgnoreCase( "info" ) )
        {
            sendStat( sender );
        } else if ( args[0].equalsIgnoreCase( "export" ) )
        {
            export( sender, args );
            sender.sendMessage( "§aExecuted!" );
        } else if ( args[0].equalsIgnoreCase( "generate" ) )
        {
            try
            {
                CaptchaGeneration.generateImages();
                sender.sendMessage( "§a[ZCord] Starting..." );
            } catch ( CaptchaGenerationException e )
            {
                sender.sendMessage( "§c[ZCord] Error when trying to generate captcha: " + e.getMessage() );
            }
        } else if ( args[0].equalsIgnoreCase( "protection" ) )
        {
            if ( args.length >= 2 )
            {
                boolean enable = args[1].equalsIgnoreCase( "on" );
                BungeeCord.getInstance().getZCord().setForceProtectionEnabled( enable );
                sender.sendMessage( "§aSewn " + ( enable ? "Included" : "§cDisabled" ) );
            }
        }
    }

    private void sendStat(CommandSender sender)
    {
        ZCord zCord = BungeeCord.getInstance().getZCord();
        final ProxiedPlayer player = (ProxiedPlayer) sender;
        //Don't skid -> by xIsm4

        (new Timer()).schedule(new TimerTask() {
            public void run() {
                player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cZCord -> &eCPS/s "+zCord.botCounter)));
            }
        }, 120L, 120L);
    }

    private void export(CommandSender sender, String[] args)
    {
        ZCord ZCord = BungeeCord.getInstance().getZCord();

        if ( args.length == 1 )
        {
            sender.sendMessage( "§b> ZCord export [TIME_IN_SECONDS] §6- §aUpload the list of Filter-Passed players"
                + " verification within the specified time. specify ALL to get all time." );
            sender.sendMessage( "§b> ZCord export [TIME_IN_SECONDS] JOIN §6- §aupload the list of,"
                + " who logged into the server during the specified time (Takes into account those who also passed the check)." );
            return;
        }
        if ( args[1].equalsIgnoreCase( "all" ) )
        {
            List<String> out = new ArrayList<>( ZCord.getUsersCount() );
            ZCord.getUserCache().values().forEach( value ->
                out.add( value.getName() + "|" + value.getIp() + "|" + value.getLastCheck() + "|" + value.getLastJoin() )
            );
            exportToFile( out, args.length >= 3 && args[2].equalsIgnoreCase( "join" ) );
            return;
        }
        try
        {
            int seconds = Integer.parseInt( args[1] );
            boolean join = args.length >= 3 && args[2].equalsIgnoreCase( "join" );
            Calendar calendar = Calendar.getInstance();
            calendar.add( Calendar.SECOND, -seconds );
            long until = calendar.getTimeInMillis();

            List<String> out = new ArrayList<>( ZCord.getUsersCount() );
            ZCord.getUserCache().values().forEach( value ->
                {
                    if ( join )
                    {
                        if ( value.getLastJoin() >= until )
                        {
                            out.add( value.getName() + "|" + value.getIp() + "|" + value.getLastCheck() + "|" + value.getLastJoin() );
                        }
                    } else if ( value.getLastCheck() >= until )
                    {
                        out.add( value.getName() + "|" + value.getIp() + "|" + value.getLastCheck() + "|" + value.getLastJoin() );
                    }
                }
            );
            exportToFile( out, join );
        } catch ( Exception e )
        {
            sender.sendMessage( "§cEnter the number" );
        }
    }

    private void exportToFile(List<String> out, boolean join)
    {
        Path outFile = new File( "ZCord", "whitelist.out." + ( join ? "join" : "" ) + ".txt" ).toPath();
        try
        {
            Files.write( outFile, out, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING );
        } catch ( IOException e )
        {
            BungeeCord.getInstance().getLogger().log( Level.WARNING, "[ZCord] Could not export ips to file", e );
        }
    }

}
