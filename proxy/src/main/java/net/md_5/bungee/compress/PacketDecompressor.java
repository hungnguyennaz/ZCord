package net.md_5.bungee.compress;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

import me.hungaz.ZCord.utils.FastException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.protocol.DefinedPacket;
import me.hungaz.ZCord.utils.FastBadPacketException;

public class PacketDecompressor extends MessageToMessageDecoder<ByteBuf>
{
    //ZCord start - limit compressed data to 2 MiB
    private static final int MAXIMUM_UNCOMPRESSED_SIZE = Integer.getInteger( "maximumPacketSize", 2 ) * 1024 * 1024; // 2MiB default vanilla maximum
    private static final int MAXIMUM_UNCOMPRESSED_SIZE_WHILE_CHECKING = ( ( 100 * 4 ) + Short.MAX_VALUE ) + 5 + 5; //((100 chars channel tag) + max data size) + string varint + packet id varint

    static
    {
        BungeeCord.getInstance().getLogger().info( "[ZCord] Maximum packet size: " + MAXIMUM_UNCOMPRESSED_SIZE );
        BungeeCord.getInstance().getLogger().info( "[ZCord] Maximum packet size while checking: " + MAXIMUM_UNCOMPRESSED_SIZE_WHILE_CHECKING );
    }
    private int threshold = -1;
    public boolean checking = false;

    public void setThreshold(int threshold)
    {
        this.threshold = threshold;
    }
    //ZCord end

    private BungeeZlib zlib;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        zlib = CompressFactory.zlib.newInstance();
        zlib.init( false, 0 );
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
    {
        zlib.free();
    }
    public void handleVoid(ChannelHandlerContext ctx) throws Exception {
        if(ctx == null) {
            assert false;
            if (ctx.isRemoved()) {
                zlib.init(false,1);
            }
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        int size = DefinedPacket.readVarInt( in );
        if ( size == 0 )
        {
            out.add( in.retain() );
        } else
        {
            //ZCord start
            if ( threshold != -1 && size < threshold )
            {
                throw new FastException( "Uncompressed size " + size + " is less than threshold " + threshold );
            }

            if ( size > MAXIMUM_UNCOMPRESSED_SIZE )
            {
                throw new FastException( "Uncompressed size " + size + " exceeds threshold of " + MAXIMUM_UNCOMPRESSED_SIZE + ". If you're server owner launch BungeeCord with '-DmaximumPacketSize=X' before '-jar', where X is 2 = 2MiB(default), 3 = 3MiB, 8 = 8MiB, 10 = ..... For forge use 16" );
            }

            if ( checking && size > MAXIMUM_UNCOMPRESSED_SIZE_WHILE_CHECKING )
            {
                throw new FastException( "Uncompressed size " + size + " exceeds threshold of " + MAXIMUM_UNCOMPRESSED_SIZE_WHILE_CHECKING + " (While checking)" );
            }
            ByteBuf decompressed;
            if ( checking )
            {
                decompressed = ctx.alloc().directBuffer( size, size );
            } else
            {
                decompressed = ctx.alloc().directBuffer();
            }
            //ZCord end

            try
            {
                zlib.process( in, decompressed, checking ); //ZCord
                Preconditions.checkArgument( decompressed.readableBytes() == size, "Decompressed size %s is not equal to actual decompressed bytes", size, decompressed.readableBytes());

                out.add( decompressed );
                decompressed = null;
            } finally
            {
                if ( decompressed != null )
                {
                    decompressed.release();
                }
            }
        }
    }
}
