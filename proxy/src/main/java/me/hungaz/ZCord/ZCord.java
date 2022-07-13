package me.hungaz.ZCord;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import me.hungaz.ZCord.utils.*;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.compress.PacketDecompressor;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.protocol.Protocol;
import me.hungaz.ZCord.caching.PacketUtils;
import me.hungaz.ZCord.caching.PacketUtils.KickType;
import me.hungaz.ZCord.captcha.CaptchaGeneration;
import me.hungaz.ZCord.captcha.CaptchaGenerationException;
import me.hungaz.ZCord.config.Settings;


public class ZCord
{

    public static final long ONE_MIN = 60000;

    @Getter
    private final Map<String, Connector> connectedUsersSet = new ConcurrentHashMap<>();
    //UserName, Ip
    @Getter
    private final Map<String, ZCordUser> userCache = new ConcurrentHashMap<>();

    private final ExecutorService executor;

    @Getter
    private final Sql sql;
    @Getter
    private final GeoIp geoIp;
    @Getter
    private final ServerPingUtils serverPingUtils;
    @Getter
    private SkidStatics statics;

    private final CheckState normalState;
    private final CheckState attackState;

    private int botCounter = 0;
    private long lastAttack = 0;
    @Setter
    @Getter
    private long lastCheck = System.currentTimeMillis();
    @Setter
    @Getter
    private boolean forceProtectionEnabled = false;

    public ZCord(boolean startup)
    {
        Settings.IMP.reload( new File( "ZCord", "config.yml" ) );
        Scoreboard.DISABLE_DUBLICATE = Settings.IMP.FIX_SCOREBOARD_TEAMS;
        try
        {
            CaptchaGeneration.generateImages();
        } catch ( CaptchaGenerationException ignored )
        {
        }
        normalState = getCheckState( Settings.IMP.PROTECTION.NORMAL );
        attackState = getCheckState( Settings.IMP.PROTECTION.ON_ATTACK );
        PacketUtils.init();
        sql = new Sql( this );
        geoIp = new GeoIp( startup );
        serverPingUtils = new ServerPingUtils( this );
        SkidStatics statics = new SkidStatics();
        statics.startUpdating();

        if ( geoIp.isAvailable() )
        {
            executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() * 2, new ThreadFactoryBuilder().setNameFormat( "SC-%d" ).build() );
        } else
        {
            executor = null;
        }

        ZCordThread.start();
    }

    public void disable()
    {
        ZCordThread.stop();
        for ( Connector connector : connectedUsersSet.values() )
        {
            if ( connector.getUserConnection() != null )
            {
                connector.getUserConnection().disconnect( "§c[ZCord] Reloading the filter" );
            }
            connector.setState( CheckState.FAILED );
        }
        connectedUsersSet.clear();
        geoIp.close();
        sql.close();
        ManyChecksUtils.clear();
        serverPingUtils.clear();
        if ( executor != null )
        {
            executor.shutdownNow();
        }
    }

    /**
     * Сохраняет игрока в памяти и в датебазе
     *
     * @param userName Имя игрока
     * @param address InetAddress игрока
     * @param afterCheck игрок после проверки или нет
     */
    public void saveUser(String userName, InetAddress address, boolean afterCheck)
    {
        userName = userName.toLowerCase();
        long timestamp = System.currentTimeMillis();
        ZCordUser ZCordUser = userCache.get( userName );
        if ( ZCordUser == null )
        {
            ZCordUser = new ZCordUser( userName, address.getHostAddress(), timestamp, timestamp );
        } else
        {
            ZCordUser.setIp( address.getHostAddress() );
            ZCordUser.setLastJoin( timestamp );
            if ( afterCheck )
            {
                ZCordUser.setLastCheck( timestamp );
            }
        }

        userCache.put( userName, ZCordUser );
        if ( sql != null )
        {
            sql.saveUser( ZCordUser );
        }
    }

    public void addUserToCache(ZCordUser ZCordUser)
    {
        userCache.put( ZCordUser.getName(), ZCordUser );
    }

    /**
     * Удаляет игрока из памяти
     *
     * @param userName Имя игрока, которого следует удалить из памяти
     */
    public void removeUser(String userName)
    {
        userName = userName.toLowerCase();
        userCache.remove( userName );
    }

    public void connectToZCord(UserConnection userConnection)
    {
        userConnection.getCh().setEncoderProtocol( Protocol.GAME );
        userConnection.getCh().setDecoderProtocol( Protocol.ZCord );
        Connector connector = new Connector( userConnection, this );

        if ( !addConnection( connector ) )
        {
            userConnection.disconnect( BungeeCord.getInstance().getTranslation( "already_connected_proxy" ) ); // TODO: Cache this disconnect packet
        } else
        {
            PacketDecompressor packetDecompressor = userConnection.getCh().getHandle().pipeline().get( PacketDecompressor.class );
            if ( packetDecompressor != null )
            {
                packetDecompressor.checking = true;
            }
            userConnection.getCh().getHandle().pipeline().get( HandlerBoss.class ).setHandler( connector );
            connector.spawn();
        }
    }

    /**
     * Добавляет игрока в мапу
     *
     * @param connector connector
     * @return если игрок был добавлен в мапу
     */
    public boolean addConnection(Connector connector)
    {
        return connectedUsersSet.putIfAbsent( connector.getName(), connector ) == null;
    }

    /**
     * Убирает игрока из мапы.
     *
     * @param name Имя игрока (lowercased)
     * @param connector Объект коннектора
     * @throws RuntimeException Имя игрока и коннектор null
     */
    public void removeConnection(String name, Connector connector)
    {
        name = name == null ? connector == null ? null : connector.getName() : name;
        if ( name != null )
        {
            connectedUsersSet.remove( name );
        } else
        {
            throw new RuntimeException( "Name and connector is null" );
        }
    }

    /**
     * Увеличивает счетчик ботов
     */
    public void incrementBotCounter()
    {
        botCounter++;
    }

    /**
     * Количество подключений на проверке
     *
     * @return количество подключений на проверке
     */
    public int getOnlineOnFilter()
    {
        return connectedUsersSet.size();
    }

    /**
     * Количество пользователей, которые прошли проверку
     *
     * @return количество пользователей, которые прошли проверку
     */
    public int getUsersCount()
    {
        return userCache.size();
    }

    /**
     * Проверяет нужно ли игроку проходить проверку
     *
     * @param userName Имя игрока
     * @param address InetAddress игрока
     * @return Нужно ли юзеру проходить проверку
     */
    public boolean needCheck(String userName, InetAddress address)
    {
        ZCordUser ZCordUser = userCache.get( userName.toLowerCase() );
        if ( Settings.IMP.PROTECTION.CHECK_LOCALHOST >= 1 && address.isLoopbackAddress() )
        {
            return Settings.IMP.PROTECTION.CHECK_LOCALHOST != 1;
        }
        return ZCordUser == null || ( Settings.IMP.FORCE_CHECK_ON_ATTACK && isUnderAttack() )
            || !ZCordUser.getIp().equalsIgnoreCase( address.getHostAddress() );
    }

    public boolean needCheck(InitialHandler connection)
    {
        if ( Settings.IMP.PROTECTION.ALWAYS_CHECK )
        {
            return true;
        }

        if ( Settings.IMP.PROTECTION.SKIP_GEYSER && isGeyser( connection ) )
        {
            return isUnderAttack();
        }

        return needCheck( connection.getName(), connection.getAddress().getAddress() );
    }

    public boolean isGeyser(InitialHandler connection)
    {
        return connection.getExtraDataInHandshake().contains( "Floodgate" );
    }

    /**
     * Проверяет, находиться ли игрок на проверке
     *
     * @param name Имя игрока которого нужно искать на проверке
     * @return Находиться ли игрок на проверке
     */
    public boolean isOnChecking(String name)
    {
        return connectedUsersSet.containsKey( name.toLowerCase() );
    }

    /**
     * Проверяет есть ли в текущий момент бот атака
     *
     * @return true Если в текущий момент идёт атака
     */
    public boolean isUnderAttack()
    {
        if ( isForceProtectionEnabled() )
        {
            return true;
        }
        long currTime = System.currentTimeMillis();
        if ( currTime - lastAttack < Settings.IMP.PROTECTION_TIME )
        {
            return true;
        }
        long diff = currTime - lastCheck;
        if ( ( diff <= ONE_MIN ) && botCounter >= Settings.IMP.PROTECTION_THRESHOLD )
        {
            lastAttack = System.currentTimeMillis();
            lastCheck -= 61000;
            return true;
        } else if ( diff >= ONE_MIN )
        {
            botCounter = 0;
            lastCheck = System.currentTimeMillis();
        }
        return false;
    }

    public boolean checkBigPing(double ping)
    {
        int mode = isUnderAttack() ? 1 : 0;
        return ping != -1 && Settings.IMP.PING_CHECK.MODE != 2 && ( Settings.IMP.PING_CHECK.MODE == 0 || Settings.IMP.PING_CHECK.MODE == mode ) && ping >= Settings.IMP.PING_CHECK.MAX_PING;
    }

    public boolean isGeoIpEnabled()
    {
        int mode = isUnderAttack() ? 1 : 0;
        return geoIp.isAvailable() && ( Settings.IMP.GEO_IP.MODE == 0 || Settings.IMP.GEO_IP.MODE == mode );
    }

    public boolean checkGeoIp(InetAddress address)
    {

        return !geoIp.isAllowed( address );
    }

    public void checkAsyncIfNeeded(InitialHandler handler)
    {
        InetAddress address = handler.getAddress().getAddress();
        ChannelWrapper ch = handler.getCh();
        int version = handler.getVersion();
        BungeeCord bungee = BungeeCord.getInstance();
        if ( !Settings.IMP.PROTECTION.ALWAYS_CHECK && ManyChecksUtils.isManyChecks( address ) )
        {
            PacketUtils.kickPlayer( KickType.MANYCHECKS, Protocol.LOGIN, ch, version );
            if(Settings.IMP.LOG_ANTIBOT_CHECKS) {
                bungee.getLogger().log(Level.INFO, "(ZCord) [{0}] disconnected: Too many checks in 10 min", address);
            }
            return;
        }

        ServerPingUtils ping = getServerPingUtils();
        if ( ping.needCheck() && ping.needKickOrRemove( address ) )
        {
            PacketUtils.kickPlayer( KickType.PING, Protocol.LOGIN, ch, version );
            if(Settings.IMP.LOG_ANTIBOT_CHECKS) {
                bungee.getLogger().log(Level.INFO, "(ZCord) [{0}] disconnected: The player did not ping the server", address.getHostAddress());
            }
            return;
        }

        if ( isGeoIpEnabled() )
        {
            executor.execute( () ->
            {
                if ( checkGeoIp( address ) )
                {
                    PacketUtils.kickPlayer( KickType.COUNTRY, Protocol.LOGIN, ch, version );
                    if(Settings.IMP.LOG_ANTIBOT_CHECKS) {
                        bungee.getLogger().log(Level.INFO, "(ZCord) [{0}] disconnected: Country is not allowed",
                                address.getHostAddress());
                    }
                    return;
                }
                handler.delayedHandleOfLoginRequset();
            } );
        } else
        {
            handler.delayedHandleOfLoginRequset();
        }
    }

    public CheckState getCurrentCheckState()
    {
        return isUnderAttack() ? attackState : normalState;
    }

    private CheckState getCheckState(int mode)
    {
        switch ( mode )
        {
            case 0:
                return CheckState.ONLY_CAPTCHA;
            case 1:
                return CheckState.CAPTCHA_POSITION;
            case 2:
                return CheckState.CAPTCHA_ON_POSITION_FAILED;
            default:
                return CheckState.CAPTCHA_ON_POSITION_FAILED;
        }
    }


    public static enum CheckState
    {
        ONLY_POSITION,
        ONLY_CAPTCHA,
        CAPTCHA_POSITION,
        CAPTCHA_ON_POSITION_FAILED,
        SUCCESSFULLY,
        FAILED
    }

    public SkidStatics getSkidStatics() {
        return statics;
    }
}
