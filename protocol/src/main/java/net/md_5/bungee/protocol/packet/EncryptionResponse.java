package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EncryptionResponse extends DefinedPacket
{

    private byte[] sharedSecret;
    private byte[] verifyToken;
    private EncryptionData encryptionData;

    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        //1.19 due new things sends 390 bytes normaly
        sharedSecret = readArray( buf, 128 );
        if ( protocolVersion < ProtocolConstants.MINECRAFT_1_19 || buf.readBoolean() )
        {
            verifyToken = readArray( buf, 128 );
        } else
        {
            encryptionData = new EncryptionData( buf.readLong(), readArray( buf ) );
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        writeArray( sharedSecret, buf );
        if ( verifyToken != null )
        {
            if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19 )
            {
                buf.writeBoolean( true );
            }
            writeArray( verifyToken, buf );
        } else
        {
            buf.writeLong( encryptionData.getSalt() );
            writeArray( encryptionData.getSignature(), buf );
        }
    }

    //https://github.com/PaperMC/Velocity/commit/5ceac16a821ea35572ff11412ace8929fd06e278#diff-1642a6289610a04a4a1d0ddeaf54223c1d5e0536c2155cd6e379e33dd2f376dcR63
    @Override
    public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        int base = 256 + 2 + 2;
        if (protocolVersion > ProtocolConstants.MINECRAFT_1_19) {
            // Verify token is twice as long on 1.19+
            // Additional 1 byte for left <> right and 8 bytes for salt
            base += 350 + 8 + 1;
        }
        return base;
    }

    @Override
    public int expectedMinLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        int base = expectedMaxLength(buf, direction, protocolVersion);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_19) {
            // These are "optional"
            base -= 350 + 8;
        }
        return base;
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }

    @Data
    public static class EncryptionData
    {

        private final long salt;
        private final byte[] signature;
    }
}
