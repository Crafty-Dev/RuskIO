package io.rusk.network.packet;

import io.netty.buffer.ByteBuf;

public interface Packet {


    void readData(ByteBuf in);

    void writeData(ByteBuf out);

}
