package io.rusk.network;

import io.netty.channel.epoll.Epoll;
import io.rusk.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {


    public static final boolean EPOLL = Epoll.isAvailable();

    private static final List<Class<? extends Packet>> PACKET_REGISTRY = new ArrayList<>();

    public static void registerPacket(Class<? extends Packet> packet) {
        PACKET_REGISTRY.add(packet);
    }

    public static int getPacketId(Packet packet) {
        return PACKET_REGISTRY.indexOf(packet.getClass());
    }

    public static Packet getPacket(int packetId) {

        Packet packet = null;

        try {
            packet = PACKET_REGISTRY.get(packetId).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {

        }

        return packet;
    }

}
