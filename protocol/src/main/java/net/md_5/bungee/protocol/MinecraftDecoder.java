package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.Setter;
import me.hungaz.ZCord.utils.FastBadPacketException;

import java.util.List;

@AllArgsConstructor
public class MinecraftDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Setter
    private Protocol protocol;
    private final boolean server;
    @Setter
    private int protocolVersion;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // See Varint21FrameDecoder for the general reasoning. We add this here as ByteToMessageDecoder#handlerRemoved()
        // will fire any cumulated data through the pipeline, so we want to try and stop it here.
        //For some reason if the bytebuf is not readable it will return
        if (!ctx.channel().isActive() || !in.isReadable()) {
            return;
        }

        //ZCord start
        if (!server && in.readableBytes() == 0) {
            return;
        }

        int originalReaderIndex = in.readerIndex();
        int originalReadableBytes = in.readableBytes();
        int packetId = DefinedPacket.readVarInt(in);
        if (packetId < 0 || packetId > Protocol.MAX_PACKET_ID) {
            throw new FastBadPacketException("[" + ctx.channel().remoteAddress() + "] <-> MinecraftDecoder received invalid packet id " + packetId);
        }
        //ZCord end
        Protocol.DirectionData prot = (server) ? protocol.TO_SERVER : protocol.TO_CLIENT;
        int protocolVersion = this.protocolVersion;
        DefinedPacket packet = prot.createPacket(packetId, protocolVersion);
        if (packet != null) {
            packet.read(in, prot.getDirection(), protocolVersion);
            if (in.isReadable()) {
                in.skipBytes(in.readableBytes()); //ZCord
                throw new FastBadPacketException("Packet " + protocol + ":" + prot.getDirection() + "/" + packetId + " (" + packet.getClass().getSimpleName() + ") larger than expected, extra bytes: " + in.readableBytes());
            }
        } else {
            in.skipBytes(in.readableBytes());
        }
        //System.out.println( "ID: " + packetId + ( packet == null ? " (null)" : " ("+packet+")" ) );
        ByteBuf copy = in.copy(originalReaderIndex, originalReadableBytes); //ZCord
        out.add(new PacketWrapper(packet, copy));
    }
}
