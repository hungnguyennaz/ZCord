package net.md_5.bungee.protocol;

import io.netty.handler.codec.DecoderException;

public class FastDecoderException extends DecoderException {

    public FastDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public FastDecoderException(String message) {
        super(message);
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
