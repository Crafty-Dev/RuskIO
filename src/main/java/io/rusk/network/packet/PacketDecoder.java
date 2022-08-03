package io.rusk.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.rusk.network.NetworkManager;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int packetId = in.readInt();
        Packet packet = NetworkManager.getPacket(packetId);

        if (packet == null)
            return;

        packet.readData(in);

        if (in.readableBytes() > 0)
            throw new IllegalStateException("Packet had an unexpected size");

        out.add(packet);

    }
}
