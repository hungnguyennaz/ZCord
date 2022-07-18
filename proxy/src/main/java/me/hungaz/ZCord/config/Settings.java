package me.hungaz.ZCord.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings extends Config
{

    @Ignore
    public static final Settings IMP = new Settings();
    @Create
    public MESSAGES MESSAGES;
    @Create
    public DIMENSIONS DIMENSIONS;
    @Create
    public GEO_IP GEO_IP;
    @Create
    public PING_CHECK PING_CHECK;
    @Create
    public SERVER_PING_CHECK SERVER_PING_CHECK;
    @Create
    public PROTECTION PROTECTION;
    @Create
    public CAPTCHA CAPTCHA;
    @Create
    public SQL SQL;
    @Comment(
        {
        "How many players/bots must enter in 1 minute for protection to turn on",
        "Recommended settings are: ",
        "150 online - 25, 250 online - 30, 350 online - 35, 550 online - 40,45, or adjust yourself.",
        "I recommended to increases these value"
        })
    public int PROTECTION_THRESHOLD = 30;
    @Comment("How long is automatic protection active? In milliseconds. 1 sec = 1000")
    public int PROTECTION_TIME = 120000;
    @Comment("Whether to check for a bot when entering the server during a bot attack, regardless of whether it passed the check or not")
    public boolean FORCE_CHECK_ON_ATTACK = true;
    @Comment("Whether to show online (include bots) from the filter")
    public boolean SHOW_ONLINE = false;
    @Comment("How much time the player has to pass the defense. In milliseconds. 1 sec = 1000. Remember to increase the value when using with Geyser.")
    public int TIME_OUT = 7272;
    @Comment("Whether to enable fix from 'Team 'xxx' already exist in this scoreboard'")
    public boolean FIX_SCOREBOARD_TEAMS = true;
    @Comment("Do you want to log trash stacktraces")
    public boolean LOG_NETTY_STACKTRACE = false;
    @Comment("Do you want to log antibot work? Usefull for large networks")
    public boolean LOG_ANTIBOT_CHECKS = true;

    public void reload(File file)
    {
        load( file );
        save( file );
    }

    @Comment(
            {
                    "Don't use '\\n', use %nl%",
                    "Before using HEX colors, use the '&' character. Example: &#9c9dff",
                    "WARNING! If the client is below 1.16 or not supporting HEX, text with HEX colors will not showing any colors, the color will always be white."
            }
    )
    public static class MESSAGES
    {

        public String PREFIX = "&a&lZ&b&lCord";
        public String CHECKING = "%prefix%&7>> &aPlease wait...";
        public String CHECKING_CAPTCHA = "%prefix%&7>> &aEnter captcha codes into chat";
        public String CHECKING_CAPTCHA_WRONG = "%prefix%&7>> &cYou entered the wrong captcha, please try again. &a%s &c%s";
        public String SUCCESSFULLY = "%prefix%&7>> &aDone!";
        public String KICK_MANY_CHECKS = "%prefix%%nl%%nl%&c Suspicious activity detected on your IP%nl%%nl%&6Please try again after 10 minutes.";
        public String KICK_NOT_PLAYER = "%prefix%%nl%%nl%&c Verification failed, you can be a bot%nl%&7&oIf not, please try again.";
        public String KICK_COUNTRY = "%prefix%%nl%%nl%&cYour country isn't allowed on the server. Please report here discord.gg/yourserver";
        public String KICK_BIG_PING = "%prefix%%nl%%nl%&cYour ping is too high to play!";
    }

    @Comment("Enable/Disable GeoIP checking")
    public static class GEO_IP
    {

        @Comment(
            {
            "When does verification work?",
            "0 - Always",
            "1 - During a bot attack",
            "2 - Never"
            })
        public int MODE = 2;
        @Comment(
            {
            "How will GeoIP work?",
            "0 - White list(Only those countries that are on the list can enter)",
            "1 - Black list(Only those countries that are not in the list can enter)"
            })
        public int TYPE = 0;
        @Comment(
            {
            "Where to download GEOIP database",
            "Change the link if it doesn't work",
            "The file must end with .mmdb or be packed into .tar.gz"
            })
        public String NEW_GEOIP_DOWNLOAD_URL = "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-Country&license_key=%license_key%&suffix=tar.gz";
        @Comment(
            {
            "If the key stops working, then in order to get a new one, you need to register at https://www.maxmind.com/",
            "and generate a new key at https://www.maxmind.com/en/accounts/current/license-key"
            })
        public String MAXMIND_LICENSE_KEY = "P5g0fVdAQIq8yQau";
        @Comment("Allowed countries")
        public List<String> ALLOWED_COUNTRIES = Arrays.asList( "RU", "UA", "BY", "KZ", "EE", "MD", "KG", "AZ", "LT", "LV", "GE", "PL" );
    }

    @Comment("Enable/Disable high ping check")
    public static class PING_CHECK
    {

        @Comment(
            {
            "When does verification work?",
            "0 - Always",
            "1 - During a bot attack",
            "2 - Never"
            })
        public int MODE = 1;
        @Comment("Maximum allowable ping")
        public int MAX_PING = 350;
    }

    @Comment("Enable/Disable check for direct connection")
    public static class SERVER_PING_CHECK
    {

        @Comment(
            {
            "When does verification work?",
            "0 - Always",
            "1 - During a bot attack",
            "2 - Never",
            "Recommended to enable because it's blocked much type of botters"
            })
        public int MODE = 1;
        @Comment("How long can you enter the server after receiving the server motd")
        public int CACHE_TIME = 12;
        public List<String> KICK_MESSAGE = new ArrayList()
        {
            {
                add( "%nl%" );
                add( "%nl%" );
                add( "&cPlease add this server into the server list" );
                add( "%nl%" );
                add( "%nl%" );
                add( "&lOur server ip &8>> &b&lIP: yourserver.memay.beo" );
                add( "%nl%" );
                add( "%nl%" );
                add( "&lRefresh the list and wait for 3-5 seconds" );
                add( "%nl%" );
                add( "%nl%" );
                add( "&a&l Then rejoin. " );
                add( "%nl%" );
                add( "%nl%" );
                add( "%nl%" );

            }
        };
    }

    @Comment(
        {
        "How the protection will work",
        "0 - Captcha verification only",
        "1 - Drop check + captcha",
        "2 - Drop check, if failed, then captcha"
        })
    public static class PROTECTION
    {

        @Comment("Drop check, if failed, then captcha")
        public int NORMAL = 1;
        @Comment("Mode of operation during an attack")
        public int ON_ATTACK = 1;
        @Comment(
            {
            "Should I enable constant checking of players upon entry?",
            "When enabling this feature, don't forget to increase the protection-threshold limits"
            })
        public boolean ALWAYS_CHECK = false;

        @Comment(
            {
            "Should I check players with ip 127.0.0.1?", "May fix some bugs when Floodgate players join.",
            "0 - Check", "1 - Disable check", "2 - Always check"
            })
        public int CHECK_LOCALHOST = 0;

        @Comment("Should i skip Geyser? The player just need to verify 1 time, after that they won't have to verify anymore. Direct-connect won't check Geyser anymore.")
        public boolean SKIP_GEYSER = true;
        @Comment("How long should the player be blacklisted (in minutes)")
        public int BLACKLIST_TIME = 10;
    }

    @Comment("Database setup")
    public static class SQL
    {

        @Comment("Database type: SQLite or MySQL")
        public String STORAGE_TYPE = "sqlite";
        @Comment("After how many days to remove players from the database who have been verified and have not logged in again. 0 or less to disable")
        public int PURGE_TIME = 14;
        @Comment("Settings for mysql")
        public String HOSTNAME = "127.0.0.1";
        public int PORT = 3306;
        public String USER = "user";
        public String PASSWORD = "password";
        public String DATABASE = "database";
    }

    @Comment("Dimensions")
    public static class DIMENSIONS
    {
        @Comment(
            {
            "Which world to use",
            "0 - Overworld",
            "1 - Nether",
            "2 - The End"
            })
        public int TYPE = 0;
    }

    @Comment("Captcha settings")
    public static class CAPTCHA
    {
        @Comment("How many captcha instances to generate. A large number can take up a lot of RAM but less cpu usages.")
        public int COUNT = 1200;
        @Comment("How often the captcha should regenerate itself in minutes.")
        public int CAPTCHA_REGENERATION_TIME = 360;
        @Comment("Captcha Font size")
        public int CAPTCHA_FONT_SIZE = 50;
    }
}
