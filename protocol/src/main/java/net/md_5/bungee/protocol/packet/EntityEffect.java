package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EntityEffect extends DefinedPacket {

    private int entityId;
    private int effectId;
    private int amplifier;
    private int duration;
    private boolean hideParticles;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = readVarInt(buf);
        this.effectId = buf.readUnsignedByte();
        this.amplifier = buf.readUnsignedByte();
        this.duration = readVarInt(buf);
        this.hideParticles = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf) {
        writeVarInt(this.entityId, buf);
        buf.writeByte(this.effectId);
        buf.writeByte(this.amplifier);
        writeVarInt(this.duration, buf);
        buf.writeBoolean(this.hideParticles);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
