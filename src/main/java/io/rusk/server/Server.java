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

public class Server {


    public final int port;
    private final List<ClientConnection> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }


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

    protected void registerClient(ClientConnection client) {
        this.clients.add(client);
    }

    protected void unregisterClient(ClientConnection client) {
        this.clients.remove(client);
    }


    public void onListening() {

    }

    public void onPacketReceive(ClientConnection client, Packet packet) {

    }

    public void onClientConnect(ClientConnection client) {

    }

    public void onClientDisconnect(ClientConnection client) {

    }

    public void broadcastPacket(Packet packet) {
        this.clients.forEach(client -> client.send(packet));
    }
}
