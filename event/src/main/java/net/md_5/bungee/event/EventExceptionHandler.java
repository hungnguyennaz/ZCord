package net.md_5.bungee.event;

public interface EventExceptionHandler<T> {

    public void handleEventException(String msg, T event, EventHandlerMethod method, Throwable ex);

}
