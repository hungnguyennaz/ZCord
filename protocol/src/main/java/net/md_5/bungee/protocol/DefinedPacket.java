package net.md_5.bungee.protocol;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.hungaz.ZCord.utils.FastBadPacketException;
import me.hungaz.ZCord.utils.FastException;
import me.hungaz.ZCord.utils.FastOverflowPacketException;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

@RequiredArgsConstructor
public abstract class DefinedPacket
{

    private static final FastException VARINT_TOO_BIG = new FastException( "varint too big" ); //ZCord
    private static final FastException ILLEGAL_BUF = new FastException( "Buffer is no longer readable" ); //ZCord

    public static void writeString(String s, ByteBuf buf)
    {
        if ( s.length() > Short.MAX_VALUE )
        {
            throw new OverflowPacketException( "Cannot send string longer than Short.MAX_VALUE (got " + s.length() + " characters)" );
        }

        byte[] b = s.getBytes( Charsets.UTF_8 );
        writeVarInt( b.length, buf );
        buf.writeBytes( b );
    }

    public static String readString(ByteBuf buf)
    {
        return readString( buf, Short.MAX_VALUE );
    }

    public static String readString(ByteBuf buf, int maxLen)
    {
        int len = readVarInt( buf );
        if ( len > maxLen * 4 )
        {
            throw new FastOverflowPacketException( "Cannot receive string longer than " + maxLen * 4 + " (got " + len + " bytes)" );
        }

        byte[] b = new byte[ len ];
        buf.readBytes( b );

        String s = new String( b, Charsets.UTF_8 );
        if ( s.length() > maxLen )
        {
            throw new FastOverflowPacketException( "Cannot receive string longer than " + maxLen + " (got " + s.length() + " characters)" );
        }

        return s;
    }

    // Waterfall start
    public static void writeString(String s, final int maxLength, ByteBuf buf)
    {
        if ( s.length() > maxLength )
        {
            throw new OverflowPacketException( String.format( "Cannot send string longer than %s (got %s characters)", maxLength, s.length() ) );
        }

        byte[] b = s.getBytes( Charsets.UTF_8 );
        writeVarInt( b.length, buf );
        buf.writeBytes( b );
    }
    // Waterfall end

    public static void writeArray(byte[] b, ByteBuf buf)
    {
        if ( b.length > Short.MAX_VALUE )
        {
            throw new OverflowPacketException( "Cannot send byte array longer than Short.MAX_VALUE (got " + b.length + " bytes)" );
        }
        writeVarInt( b.length, buf );
        buf.writeBytes( b );
    }

    public static byte[] toArray(ByteBuf buf)
    {
        byte[] ret = new byte[ buf.readableBytes() ];
        buf.readBytes( ret );

        return ret;
    }

    public static byte[] readArray(ByteBuf buf)
    {
        return readArray( buf, buf.readableBytes() );
    }

    public static byte[] readArray(ByteBuf buf, int limit)
    {
        int len = readVarInt( buf );
        if ( len > limit )
        {
            throw new FastOverflowPacketException( "Cannot receive byte array longer than " + limit + " (got " + len + " bytes)" );
        }
        byte[] ret = new byte[ len ];
        buf.readBytes( ret );
        return ret;
    }

    public static int[] readVarIntArray(ByteBuf buf)
    {
        int len = readVarInt( buf );
        int[] ret = new int[ len ];

        for ( int i = 0; i < len; i++ )
        {
            ret[i] = readVarInt( buf );
        }

        return ret;
    }

    public static void writeStringArray(List<String> s, ByteBuf buf)
    {
        writeVarInt( s.size(), buf );
        for ( String str : s )
        {
            writeString( str, buf );
        }
    }

    public static List<String> readStringArray(ByteBuf buf)
    {
        int len = readVarInt( buf );
        List<String> ret = new ArrayList<>( len );
        for ( int i = 0; i < len; i++ )
        {
            ret.add( readString( buf ) );
        }
        return ret;
    }

    public static int readVarInt(ByteBuf input)
    {
        return readVarInt( input, 5 );
    }

    public static int readVarInt(ByteBuf input, int maxBytes)
    {
        int out = 0;
        int bytes = 0;
        byte in;
        // int readable = input.readableBytes(); //ZCord
        while ( true )
        {
            // ZCord start
            // if ( readable-- == 0 )
            // {
            //      throw ILLEGAL_BUF;
            //   }
            //ZCord end
            in = input.readByte();

            out |= ( in & 0x7F ) << ( bytes++ * 7 );

            if ( bytes > maxBytes )
            {
                input.clear();
                throw VARINT_TOO_BIG; //ZCord
            }

            if ( ( in & 0x80 ) != 0x80 )
            {
                break;
            }
        }

        return out;
    }

    public static void writeVarInt(int value, ByteBuf output)
    {
        int part;
        while ( true )
        {
            part = value & 0x7F;

            value >>>= 7;
            if ( value != 0 )
            {
                part |= 0x80;
            }

            output.writeByte( part );

            if ( value == 0 )
            {
                break;
            }
        }
    }

    public static int readVarShort(ByteBuf buf)
    {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ( ( low & 0x8000 ) != 0 )
        {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return ( ( high & 0xFF ) << 15 ) | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite)
    {
        int low = toWrite & 0x7FFF;
        int high = ( toWrite & 0x7F8000 ) >> 15;
        if ( high != 0 )
        {
            low = low | 0x8000;
        }
        buf.writeShort( low );
        if ( high != 0 )
        {
            buf.writeByte( high );
        }
    }

    public static void writeUUID(UUID value, ByteBuf output)
    {
        output.writeLong( value.getMostSignificantBits() );
        output.writeLong( value.getLeastSignificantBits() );
    }

    public static UUID readUUID(ByteBuf input)
    {
        return new UUID( input.readLong(), input.readLong() );
    }

    public static void writeProperties(Property[] properties, ByteBuf buf)
    {
        if ( properties == null )
        {
            writeVarInt( 0, buf );
            return;
        }

        writeVarInt( properties.length, buf );
        for ( Property prop : properties )
        {
            writeString( prop.getName(), buf );
            writeString( prop.getValue(), buf );
            if ( prop.getSignature() != null )
            {
                buf.writeBoolean( true );
                writeString( prop.getSignature(), buf );
            } else
            {
                buf.writeBoolean( false );
            }
        }
    }

    public static Property[] readProperties(ByteBuf buf)
    {
        Property[] properties = new Property[ DefinedPacket.readVarInt( buf ) ];
        for ( int j = 0; j < properties.length; j++ )
        {
            String name = readString( buf );
            String value = readString( buf );
            if ( buf.readBoolean() )
            {
                properties[j] = new Property( name, value, DefinedPacket.readString( buf ) );
            } else
            {
                properties[j] = new Property( name, value );
            }
        }

        return properties;
    }

    public static void writePublicKey(PlayerPublicKey publicKey, ByteBuf buf)
    {
        if ( publicKey != null )
        {
            buf.writeBoolean( true );
            buf.writeLong( publicKey.getExpiry() );
            writeArray( publicKey.getKey(), buf );
            writeArray( publicKey.getSignature(), buf );
        } else
        {
            buf.writeBoolean( false );
        }
    }

    public static PlayerPublicKey readPublicKey(ByteBuf buf)
    {
        if ( buf.readBoolean() )
        {
            return new PlayerPublicKey( buf.readLong(), readArray( buf ), readArray( buf ) );
        }

        return null;
    }

    public static Tag readTag(ByteBuf input)
    {
        Tag tag = NamedTag.read( new DataInputStream( new ByteBufInputStream( input ) ) );
        Preconditions.checkArgument( !tag.isError(), "Error reading tag: %s", tag.error() );
        return tag;
    }

    public static void writeTag(Tag tag, ByteBuf output)
    {
        try
        {
            tag.write( new DataOutputStream( new ByteBufOutputStream( output ) ) );
        } catch ( IOException ex )
        {
            throw new RuntimeException( "Exception writing tag", ex );
        }
    }

    //ZCord start - see https://github.com/PaperMC/Waterfall/blob/master/BungeeCord-Patches/0057-Additional-DoS-mitigations.patch
    public static void doLengthSanityChecks(ByteBuf buf, DefinedPacket packet,
                                      ProtocolConstants.Direction direction, int protocolVersion, int expectedMinLen, int expectedMaxLen)
    {
        //Temporary disable for 1.19
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19 )
        {
            return;
        }
        int readable = buf.readableBytes();
        if ( readable > expectedMaxLen )
        {
            throw new FastBadPacketException( "Packet " + packet.getClass()
                                    + " Protocol " + protocolVersion + " was too big (expected "
                                    + expectedMaxLen + " bytes, got " + readable + " bytes)" );
        }
        if ( readable < expectedMinLen )
        {
            throw new FastBadPacketException( "Packet " + packet.getClass()
                                    + " Protocol " + protocolVersion + " was too small (expected "
                                    + expectedMinLen + " bytes, got " + readable + " bytes)" );
        }
    }
    //ZCord end

    public void read(ByteBuf buf)
    {
        throw new UnsupportedOperationException( "Packet must implement read method" );
    }

    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        read( buf );
    }

    public void write(ByteBuf buf)
    {
        throw new UnsupportedOperationException( "Packet must implement write method" );
    }

    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        write( buf );
    }

    //https://github.com/PaperMC/Velocity/commit/5ceac16a821ea35572ff11412ace8929fd06e278#diff-1642a6289610a04a4a1d0ddeaf54223c1d5e0536c2155cd6e379e33dd2f376dcR63
    public abstract int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    public abstract int expectedMinLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    public abstract void handle(AbstractPacketHandler handler) throws Exception;

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
