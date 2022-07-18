package me.hungaz.ZCord.captcha;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.BungeeCord;
import me.hungaz.ZCord.caching.CachedCaptcha;
import me.hungaz.ZCord.caching.PacketUtils;
import me.hungaz.ZCord.captcha.generator.map.MapPalette;
import me.hungaz.ZCord.config.Settings;


@UtilityClass
public class CaptchaGeneration
{
    private static volatile boolean generation = false;

    public static synchronized void generateImages() throws CaptchaGenerationException
    {
        if ( generation )
        {
            throw new CaptchaGenerationException( "Captcha is already generated!" );
        }

        generation = true;
        Thread thread = new Thread( CaptchaGeneration::generateCaptchas );
        thread.setName( "CaptchaGenerationProvider-thread" );
        thread.setPriority( Thread.MIN_PRIORITY );
        thread.start();
    }
    private static void generateCaptchas()
    {
        try
        {
            List<Font> fonts = Arrays.asList(
                    new Font( Font.SANS_SERIF, Font.PLAIN, Settings.IMP.CAPTCHA.CAPTCHA_FONT_SIZE ),
                    new Font( Font.SERIF, Font.PLAIN, Settings.IMP.CAPTCHA.CAPTCHA_FONT_SIZE),
                    new Font( Font.MONOSPACED, Font.BOLD, Settings.IMP.CAPTCHA.CAPTCHA_FONT_SIZE) );
            PacketUtils.captchas.clear();
            BungeeCord.getInstance().getLogger().log( Level.INFO, "[ZCord] " + ( BungeeCord.getInstance().isEnabled() ? "Background captcha generation has been started." : "Captcha generation will continue in the instance." ) );
            ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors(),
                    new ThreadFactoryBuilder()
                            .setPriority( Thread.MIN_PRIORITY )
                            .setNameFormat( "CaptchaGenerationTask-thread-%d" )
                            .build() );
            MapPalette.prepareColors();

            int captchaCount = Settings.IMP.CAPTCHA.COUNT;

            if ( captchaCount <= 0 )
            {
                captchaCount = 1;
            }

            List<CachedCaptcha.CaptchaHolder> holders = Collections.synchronizedList( new ArrayList<>() );

            for ( int i = 1; i <= captchaCount; i++ )
            {
                executor.execute( new CaptchaGenerationTask( executor, fonts, holders ) );
            }

            long start = System.currentTimeMillis();
            ThreadPoolExecutor ex = (ThreadPoolExecutor) executor;
            while ( ex.getActiveCount() != 0 )
            {
                if ( BungeeCord.getInstance().isEnabled() )
                {
                    BungeeCord.getInstance().getLogger().log( Level.INFO, "[ZCord] Generating captcha [" + ( captchaCount - ex.getQueue().size() ) + "/" + captchaCount + "]" );
                }

                PacketUtils.captchas.setCaptchas( new ArrayList<>( holders ) );
                try
                {
                    Thread.sleep( 1000L );
                } catch ( InterruptedException ex1 )
                {
                    BungeeCord.getInstance().getLogger().log( Level.WARNING, "[ZCord] Error when generating captcha, shutting down...", ex1 );
                    System.exit( 0 );
                    return;
                }
            }

            executor.shutdownNow();

            PacketUtils.captchas.setCaptchas( new ArrayList<>( holders ) );
            System.gc();
            BungeeCord.getInstance().getLogger().log( Level.INFO, "[ZCord] Captcha generated in {0} ms", System.currentTimeMillis() - start );
        } catch ( Exception e )
        {
            e.printStackTrace();
        } finally
        {
            generation = false;
        }
    }
}