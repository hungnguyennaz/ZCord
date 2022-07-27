package net.md_5.bungee;

public class Bootstrap
{

    public static void main(String[] args) throws Exception {
        if (Float.parseFloat(System.getProperty("java.class.version") ) < 52.0) {  
            System.out.println("You are using an outdated java version, we recommend the usage of lastest"); 
            return;
        }

        BungeeCordLauncher.main(args);
    }
}
