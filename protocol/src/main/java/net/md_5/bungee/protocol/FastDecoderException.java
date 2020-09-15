package net.md_5.bungee.protocol;

import io.netty.handler.codec.DecoderException;

public class FastDecoderException extends DecoderException {

    private static final boolean PROCESS_TRACES = Boolean.getBoolean("waterfall.decoder-traces"); // Waterfall
    public FastDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public FastDecoderException(String message) {
        super(message);
    }

    @Override
    public Throwable initCause(Throwable cause)
    {
        // Waterfall start
        if (PROCESS_TRACES) {
            return super.initCause(cause);
        }
        // Waterfall end
        return this;
    }

    @Override
    public Throwable fillInStackTrace()
    {
        // Waterfall start
        if (PROCESS_TRACES) {
            return super.fillInStackTrace();
        }
        // Waterfall end
        return this;
    }
}
