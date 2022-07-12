package me.hungaz.ZCord.utils;

public class FastException extends RuntimeException
{

    public FastException(String message)
    {
        super( message );
    }

    @Override
    public Throwable initCause(Throwable cause)
    {
        return this;
    }

    @Override
    public Throwable fillInStackTrace()
    {
        return this;
    }
}
