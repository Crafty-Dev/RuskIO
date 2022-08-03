package io.rusk.network.packet;

import io.netty.buffer.ByteBuf;

public interface Packet {


    /**
     * Used for Decoding
     * @param in The incoming bytes
     */
    void readData(ByteBuf in);

    /**
     * Used for Encoding
     * @param out The outgoing bytes
     */
    void writeData(ByteBuf out);

}
