package me.hungaz.ZCord.caching;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.Protocol;
import me.hungaz.ZCord.Connector;
import me.hungaz.ZCord.packets.SetExp;


public class CachedExpPackets
{

    private ByteBuf[/*tick*/][/*mc version*/] byteBuf = new ByteBuf[ Connector.TOTAL_TICKS ][ PacketUtils.PROTOCOLS_COUNT ];

    public CachedExpPackets()
    {
        create();
    }

    private void create()
    {
        int ticks = Connector.TOTAL_TICKS;
        int interval = 2;
        float expinterval = 1f / ( (float) ticks / (float) interval );
        SetExp setExp = new SetExp( 0, 0, 0 );
        for ( int i = 0; i < ticks; i = i + interval )
        {
            setExp.setExpBar( setExp.getExpBar() + expinterval );
            setExp.setLevel( setExp.getLevel() + 1 );
            PacketUtils.fillArray( byteBuf[i], setExp, Protocol.ZCord );
        }
    }

    public ByteBuf get(int tick, int version)
    {
        ByteBuf buf = byteBuf[tick][PacketUtils.rewriteVersion( version )];
        return buf == null ? null : buf.retainedDuplicate();
    }

    public void release()
    {
        for ( int i = 0; i < byteBuf.length; i++ )
        {
            if ( byteBuf[i] != null )
            {
                for ( ByteBuf buf : byteBuf[i] )
                {
                    PacketUtils.releaseByteBuf( buf );
                }
                byteBuf[i] = null;
            }

        }
        byteBuf = null;
    }
}