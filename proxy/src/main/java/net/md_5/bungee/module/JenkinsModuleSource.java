package net.md_5.bungee.module;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.Data;
import net.md_5.bungee.Util;

@Data
public class JenkinsModuleSource implements ModuleSource
{

    @Override
    public void retrieve(ModuleSpec module, ModuleVersion version)
    {
        System.out.println( "Attempting to Jenkins download module " + module.getName() + " v" + version.getBuild() );
        try
        {
            final String url = String.format(
                "https://papermc.io/api/v2/projects/%1$s/versions/%2$s/builds/%3$s/downloads/%4$s-%2$s-%3$s.jar",
                "waterfall",
                net.md_5.bungee.api.ProxyServer.getInstance().getVersion().split(":")[2].split("-")[0],
                version.getBuild(),
                module.getName()
            );
            URL website = new URL( url );
            URLConnection con = website.openConnection();
            // 15 second timeout at various stages
            con.setConnectTimeout( 15000 );
            con.setReadTimeout( 15000 );
            con.setRequestProperty( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36" );

            Files.copy( con.getInputStream(), module.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING );
            System.out.println( "Download complete" );
        } catch ( IOException ex )
        {
            System.out.println( "Failed to download: " + Util.exception( ex ) );
        }
    }
}
