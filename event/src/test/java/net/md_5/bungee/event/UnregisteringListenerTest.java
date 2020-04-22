package net.md_5.bungee.event;

import org.junit.Assert;
import org.junit.Test;

public class UnregisteringListenerTest
{

    private final EventBus bus = new EventBus();

    @Test
    public void testPriority()
    {
        bus.register( this );
        bus.unregister( this );
        bus.post( new TestEvent(), null ); // Waterfall - We dont need an exception handler here
    }

    @EventHandler
    public void onEvent(TestEvent evt)
    {
        Assert.fail( "Event listener wasn't unregistered" );
    }

    public static class TestEvent
    {
    }
}
