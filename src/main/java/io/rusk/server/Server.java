package io.rusk.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.rusk.network.NetworkManager;
import io.rusk.network.packet.Packet;
import io.rusk.network.packet.PacketDecoder;
import io.rusk.network.packet.PacketEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * The Server
 */
public abstract class Server {


    public final int port;

    /**
     * A list of all connected Clients
     */
    private final List<ClientConnection> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }


    /**
     * Starts the server
     * @throws RuntimeException When the server failed to start
     */
    public void run() throws RuntimeException {

        EventLoopGroup group = NetworkManager.EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture future = bootstrap.group(group).channel(NetworkManager.EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), new ClientConnection(Server.this));
                }
            }).childOption(ChannelOption.SO_KEEPALIVE, true).bind(this.port).sync();
            this.onListening();
            future.channel().closeFuture().syncUninterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to start server on :" + this.port);
        } finally {
            group.shutdownGracefully();
        }

    }

    /**
     * Adds a client to the list
     * @param client The client that should get registered
     */
    protected void registerClient(ClientConnection client) {
        this.clients.add(client);
    }

    /**
     * Removes a client from the list
     * @param client The client that should get unregistered
     */
    protected void unregisterClient(ClientConnection client) {
        this.clients.remove(client);
    }


    /**
     * Called when the server has finished starting
     */
    public void onListening() {

    }

    /**
     * Called when the server receives a packet from a client
     * @param client The client that has sent the packet
     * @param packet The packet the client has sent
     */
    public void onPacketReceive(ClientConnection client, Packet packet) {

    }

    /**
     * Called when a client has connected to the server
     * @param client The client that has connected
     */
    public void onClientConnect(ClientConnection client) {

    }

    /**
     * Called when a client has disconnected
     * @param client The client that has disconnected
     */
    public void onClientDisconnect(ClientConnection client) {

    }

    /**
     * Sends a packet to all clients
     * @param packet The packet that should be broadcasted
     */
    public void broadcastPacket(Packet packet) {
        this.clients.forEach(client -> client.send(packet));
    }
}
