package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import me.hungaz.ZCord.utils.FastCorruptedFrameException;

import java.util.List;

public class Varint21FrameDecoder extends ByteToMessageDecoder {

    private static boolean DIRECT_WARNING;
    @Deprecated
    private boolean is119;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!ctx.channel().isActive()) {
            in.skipBytes(in.readableBytes());
            return;
        }

        in.markReaderIndex();

        for (int i = 0; i < 3; i++) // Waterfall
        {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            // Waterfall start
            byte read = in.readByte();
            if (read >= 0) {
                in.resetReaderIndex();
                int length = DefinedPacket.readVarInt(in);
                // Waterfall end
                if (false && length == 0) // Waterfall - ignore
                {
                    throw new FastCorruptedFrameException("Empty Packet!");
                }

                if (in.readableBytes() < length) {
                    in.resetReaderIndex();
                    // Waterfall start
                } else {
                    out.add(in.readRetainedSlice(length));
                    // Waterfall end
                }
                return;
            }
        }

        super.setSingleDecode(true);
        throw new FastCorruptedFrameException("length wider than 21-bit");
    }
}
