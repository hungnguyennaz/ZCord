package net.md_5.bungee;

public class Bootstrap
{

    public static void main(String[] args) throws Exception {
        if (Float.parseFloat(System.getProperty("java.class.version") ) < 52.0) {  // ZCord
            System.err.println("Your Java version (" + System.getProperty("java.version") + ") is too old. Please update your Java runtime."); // ZCord
            System.out.println("Java 8 or later is required to run ZCord."); // ZCord
            return;
        }

        BungeeCordLauncher.main(args);
    }
}
