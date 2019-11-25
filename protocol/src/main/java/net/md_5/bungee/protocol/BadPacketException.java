package net.md_5.bungee.protocol;

public class BadPacketException extends RuntimeException
{
    private static final boolean PROCESS_TRACES = Boolean.getBoolean("waterfall.bad-packet-traces");

    public BadPacketException(String message)
    {
        super( message );
    }

    public BadPacketException(String message, Throwable cause)
    {
        super( message, cause );
    }

    // Waterfall start
    @Override
    public Throwable initCause(Throwable cause)
    {
        if (PROCESS_TRACES) {
            return super.initCause(cause);
        }
        return this;
    }

    @Override
    public Throwable fillInStackTrace()
    {
        if (PROCESS_TRACES) {
            return super.fillInStackTrace();
        }
        return this;
    }
    // Waterfall end
}
