package io.rusk.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.rusk.network.NetworkManager;

public class PacketEncoder extends MessageToByteEncoder<Packet> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {

        out.writeInt(NetworkManager.getPacketId(packet));
        packet.writeData(out);

    }
}
