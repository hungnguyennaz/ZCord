package me.hungaz.ZCord;

public class ZCordUser
{
    private final String name;
    private String ip;
    private long lastCheck;
    private long lastJoin;

    public ZCordUser(String name, String ip, long lastCheck, long lastJoin)
    {
        this.name = name;
        this.ip = ip;
        this.lastCheck = lastCheck;
        this.lastJoin = lastJoin;
    }

    public String getName()
    {
        return name;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public long getLastCheck()
    {
        return lastCheck;
    }

    public void setLastCheck(long lastCheck)
    {
        this.lastCheck = lastCheck;
    }

    public long getLastJoin()
    {
        return lastJoin;
    }

    public void setLastJoin(long lastJoin)
    {
        this.lastJoin = lastJoin;
    }
}