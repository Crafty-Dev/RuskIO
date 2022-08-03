package io.rusk.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.rusk.network.NetworkManager;
import io.rusk.network.packet.Packet;
import io.rusk.network.packet.PacketDecoder;
import io.rusk.network.packet.PacketEncoder;

public class Client {


    public final String host;
    public final int port;

    private ServerConnection server;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void connect() throws RuntimeException {

        EventLoopGroup group = NetworkManager.EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();


        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture future = bootstrap.group(group).channel(NetworkManager.EPOLL ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer<>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new PacketEncoder(), new PacketDecoder(), new ServerConnection(Client.this));
                }
            }).option(ChannelOption.SO_KEEPALIVE, true).connect(this.host, this.port).sync();

            future.channel().closeFuture().syncUninterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed connecting to " + this.host + ":" + this.port);
        } finally {
            group.shutdownGracefully();
        }

    }


    public void onConnect() {

    }

    public void onDisconnect() {

    }

    public void onPacketReceive(Packet packet) {

    }

    public void send(Packet packet) {
        this.server.send(packet);
    }

}
