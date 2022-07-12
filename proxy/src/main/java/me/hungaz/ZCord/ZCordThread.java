package me.hungaz.ZCord;

import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import net.md_5.bungee.BungeeCord;
import me.hungaz.ZCord.ZCord.CheckState;
import me.hungaz.ZCord.caching.PacketUtils.KickType;
import me.hungaz.ZCord.caching.PacketsPosition;
import me.hungaz.ZCord.captcha.CaptchaGeneration;
import me.hungaz.ZCord.captcha.CaptchaGenerationException;
import me.hungaz.ZCord.config.Settings;
import me.hungaz.ZCord.utils.ManyChecksUtils;



public class ZCordThread
{

    private static Thread thread;
    private static final HashSet<String> TO_REMOVE_SET = new HashSet<>();
    private static BungeeCord bungee = BungeeCord.getInstance();

    public static void start()
    {
        ( thread = new Thread( () ->
        {
            while ( sleep( 1000 ) )
            {
                try
                {
                    long currTime = System.currentTimeMillis();
                    for ( Map.Entry<String, Connector> entryset : bungee.getZCord().getConnectedUsersSet().entrySet() )
                    {
                        Connector connector = entryset.getValue();
                        if ( !connector.isConnected() )
                        {
                            TO_REMOVE_SET.add( entryset.getKey() );
                            continue;
                        }
                        CheckState state = connector.getState();
                        switch ( state )
                        {
                            case SUCCESSFULLY:
                            case FAILED:
                                TO_REMOVE_SET.add( entryset.getKey() );
                                continue;
                            default:
                                if ( ( currTime - connector.getJoinTime() ) >= Settings.IMP.TIME_OUT )
                                {
                                    connector.failed( KickType.TIMED_OUT, state == ZCord.CheckState.CAPTCHA_ON_POSITION_FAILED
                                            ? "Too long fall check" : "Captcha not entered" );
                                    TO_REMOVE_SET.add( entryset.getKey() );
                                    continue;
                                } else if ( state == ZCord.CheckState.CAPTCHA_ON_POSITION_FAILED || state == ZCord.CheckState.ONLY_POSITION )
                                {
                                    connector.sendMessage( PacketsPosition.CHECKING_MSG );
                                } else
                                {
                                    connector.sendMessage( PacketsPosition.CHECKING_CAPTCHA_MSG );
                                }
                                connector.sendPing();
                        }
                    }

                } catch ( Exception e )
                {
                    bungee.getLogger().log( Level.WARNING, "[ZCord] Error detected! Please report this to developer.", e );
                } finally
                {
                    if ( !TO_REMOVE_SET.isEmpty() )
                    {
                        for ( String remove : TO_REMOVE_SET )
                        {
                            bungee.getZCord().removeConnection( remove, null );
                        }
                        TO_REMOVE_SET.clear();
                    }
                }
            }

        }, "ZCord thread" ) ).start();
    }

    public static void stop()
    {
        if ( thread != null )
        {
            thread.interrupt();
        }
    }

    private static boolean sleep(long time)
    {
        try
        {
            Thread.sleep( time );
        } catch ( InterruptedException ex )
        {
            return false;
        }
        return true;
    }

    public static void startCleanUpThread()
    {
        Thread t = new Thread( () ->
        {
            byte counterClean = 0;
            int counterCaptcha = 0;
            while ( !Thread.interrupted() && sleep( 5 * 1000 ) )
            {
                if ( ++counterClean == 12 )
                {
                    counterClean = 0;
                    ManyChecksUtils.cleanUP();
                    if ( bungee.getZCord() != null )
                    {
                        ZCord ZCord = bungee.getZCord();
                        if ( ZCord.getServerPingUtils() != null )
                        {
                            ZCord.getServerPingUtils().cleanUP();
                        }
                        if ( ZCord.getSql() != null )
                        {
                            ZCord.getSql().tryCleanUP();
                        }
                        if ( ZCord.getGeoIp() != null )
                        {
                            ZCord.getGeoIp().tryClenUP();
                        }
                    }
                }
                //FailedUtils.flushQueue();
                int captchaMin = Settings.IMP.CAPTCHA.CAPTCHA_REGENERATION_TIME;
                if ( captchaMin <= 0 )
                {
                    captchaMin = 1;
                }
                if ( ++counterCaptcha == ( 12 * captchaMin ) )
                {
                    counterCaptcha = 0;

                    try
                    {
                        CaptchaGeneration.generateImages();
                    } catch ( CaptchaGenerationException ignored )
                    {
                    }
                }
            }
        }, "CleanUp thread" );
        t.setDaemon( true );
        t.start();
    }
}
