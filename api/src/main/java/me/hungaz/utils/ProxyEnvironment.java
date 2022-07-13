package me.hungaz.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProxyEnvironment {
    private static final boolean RUNNING_AS_ROOT_OR_ADMIN;

    static {
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        boolean isAdmin = false;
        String[] command = isWindows ? new String[]{"reg", "query", "reg query \"HKU\\S-1-5-19\"" } : new String[]{"id", "-u" };

        try {
            Process process = new ProcessBuilder(command).start();
            process.waitFor();
            if (isWindows) {
                isAdmin = process.exitValue() == 0;
            } else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String uid = reader.readLine();
                isAdmin = uid.equals("0");
            }
        } catch (InterruptedException | IOException ignored) {
        }

        RUNNING_AS_ROOT_OR_ADMIN = isAdmin;
    }

    public static boolean userIsRootOrAdmin() {
        return RUNNING_AS_ROOT_OR_ADMIN;
    }
}