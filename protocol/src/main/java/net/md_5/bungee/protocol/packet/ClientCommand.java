package net.md_5.bungee.protocol.packet;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ChatChain;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientCommand extends DefinedPacket {

    private String command;
    private long timestamp;
    private long salt;
    private Map<String, byte[]> signatures;
    private boolean signedPreview;
    private ChatChain chain;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        command = readString(buf);
        timestamp = buf.readLong();
        salt = buf.readLong();

        int cnt = readVarInt(buf);
        Preconditions.checkArgument(cnt <= 8, "Too many signatures");
        signatures = new HashMap<>(cnt);
        for (int i = 0; i < cnt; i++) {
            signatures.put(readString(buf, 16), readArray(buf));
        }

        signedPreview = buf.readBoolean();
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_19_1) {
            chain = new ChatChain();
            chain.read(buf, direction, protocolVersion);
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        writeString(command, buf);
        buf.writeLong(timestamp);
        buf.writeLong(salt);

        writeVarInt(signatures.size(), buf);
        for (Map.Entry<String, byte[]> entry : signatures.entrySet()) {
            writeString(entry.getKey(), buf);
            writeArray(entry.getValue(), buf);
        }

        buf.writeBoolean(signedPreview);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_19_1) {
            chain.write(buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }
}