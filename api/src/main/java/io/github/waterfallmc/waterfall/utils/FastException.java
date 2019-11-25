package io.github.waterfallmc.waterfall.utils;

// This is basically a copy of QuietException
public class FastException extends RuntimeException {

    public FastException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return this;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
