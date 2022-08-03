package me.hungaz.ZCord.packets;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.hungaz.ZCord.utils.Dimension;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JoinGame extends DefinedPacket {
    private final int entityId;
    private boolean hardcore = false;
    private short gameMode = 0;
    private short previousGameMode = 0;
    private Set<String> worldNames = new HashSet<>(Arrays.asList("minecraft:overworld"));
    private String worldName = "minecraft:overworld";
    private int dimensionId = 0;
    private long seed = 1;
    private short difficulty = 0;
    private short maxPlayers = 1;
    private String levelType = "flat";
    private int viewDistance = 0;
    private boolean reducedDebugInfo = false;
    private boolean normalRespawn = true;
    private boolean debug = false;
    private boolean flat = true;

    private Tag dimensions1_16;
    private Tag dimensions1_16_2;
    private Tag dimensions1_18_2;
    private Tag dimensions1_19;
    private Tag dimensions1_19_1;

    private Tag dimension;
    private Tag dimension1_18_2;

    public JoinGame() {
        this(0, Dimension.OVERWORLD);
    }

    public JoinGame(int entityId, Dimension dimension) {
        this.entityId = entityId;
        this.dimensionId = dimension.getDimensionId();
        this.worldName = dimension.getKey();
        this.worldNames = new HashSet<>(Arrays.asList(dimension.getKey()));

        this.dimensions1_16 = dimension.getFullCodec(ProtocolConstants.MINECRAFT_1_16_1);
        this.dimensions1_16_2 = dimension.getFullCodec(ProtocolConstants.MINECRAFT_1_16_2);
        this.dimensions1_18_2 = dimension.getFullCodec(ProtocolConstants.MINECRAFT_1_18_2);
        this.dimensions1_19 = dimension.getFullCodec(ProtocolConstants.MINECRAFT_1_19);
        this.dimensions1_19_1 = dimension.getFullCodec(ProtocolConstants.MINECRAFT_1_19_1);

        this.dimension = dimension.getAttributes(ProtocolConstants.MINECRAFT_1_16_2);
        this.dimension1_18_2 = dimension.getAttributes(ProtocolConstants.MINECRAFT_1_18_2);
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeInt(entityId);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2) {
            buf.writeBoolean(hardcore);
        }
        buf.writeByte(gameMode);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_16) {
            buf.writeByte(previousGameMode);

            writeVarInt(worldNames.size(), buf);
            for (String world : worldNames) {
                writeString(world, buf);
            }

            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_19_1) {
                writeTag(dimensions1_19_1, buf);
            } else if (protocolVersion >= ProtocolConstants.MINECRAFT_1_19) {
                writeTag(dimensions1_19, buf);
            } else if (protocolVersion >= ProtocolConstants.MINECRAFT_1_18_2) {
                writeTag(dimensions1_18_2, buf);
            } else if (protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2) {
                writeTag(dimensions1_16_2, buf);
            } else {
                writeTag(dimensions1_16, buf);
            }
        }

        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_16) {
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_19 || protocolVersion <= ProtocolConstants.MINECRAFT_1_16_1) {
                writeString(worldName, buf);
            } else if (protocolVersion == ProtocolConstants.MINECRAFT_1_18_2) {
                writeTag(dimension1_18_2, buf);
            } else {
                writeTag(dimension, buf);
            }
            writeString(worldName, buf);
        } else if (protocolVersion > ProtocolConstants.MINECRAFT_1_9) {
            buf.writeInt(dimensionId); //dim
        } else {
            buf.writeByte(dimensionId); //dim
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_15) {
            buf.writeLong(seed);
        }
        if (protocolVersion < ProtocolConstants.MINECRAFT_1_14) {
            buf.writeByte(difficulty);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2) {
            writeVarInt(maxPlayers, buf);
        } else {
            buf.writeByte(maxPlayers);
        }
        if (protocolVersion < ProtocolConstants.MINECRAFT_1_16) {
            writeString(levelType, buf);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_14) {
            writeVarInt(viewDistance, buf);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_18) {
            writeVarInt(viewDistance, buf);
        }
        if (protocolVersion >= 29) {
            buf.writeBoolean(reducedDebugInfo);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_15) {
            buf.writeBoolean(normalRespawn);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_16) {
            buf.writeBoolean(debug);
            buf.writeBoolean(flat);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_19) {
            buf.writeBoolean(false); //lastDeathPos
        }

    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        throw new UnsupportedOperationException();
    }
}