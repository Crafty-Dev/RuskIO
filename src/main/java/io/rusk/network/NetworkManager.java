package io.rusk.network;

import io.netty.channel.epoll.Epoll;
import io.rusk.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {


    public static final boolean EPOLL = Epoll.isAvailable();

    private static final List<Class<? extends Packet>> PACKET_REGISTRY = new ArrayList<>();

    /**
     * Registers a packet.
     * Packets should be registered in the same order on client and server
     * @param packet The packet class
     */
    public static void registerPacket(Class<? extends Packet> packet) {
        PACKET_REGISTRY.add(packet);
    }

    /**
     * Returns a packets id
     * @param packet The packet whose id should be returned
     * @return The id based on the index the packet has in the registry
     */
    public static int getPacketId(Packet packet) {
        return PACKET_REGISTRY.indexOf(packet.getClass());
    }

    /**
     * Returns a new Packet with the given id
     * @param packetId The id of the packet
     * @return A new packet. The type is based on the id
     * @throws RuntimeException If the packet has no empty constructor
     */
    public static Packet getPacket(int packetId) throws RuntimeException {

        Packet packet = null;

        try {
            packet = PACKET_REGISTRY.get(packetId).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to instantiate Packet; Maybe it does not have an empty constructor?");
        }

        return packet;
    }

}
