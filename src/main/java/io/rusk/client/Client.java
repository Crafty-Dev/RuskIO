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

/**
 * The Client
 */
public abstract class Client {


    public final String host;
    public final int port;

    private ServerConnection server;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }


    /**
     * Connects the client to the server
     * @throws RuntimeException When the client failed to connect
     */
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

    /**
     * Sets the instance of the Server connection
     * @param server The server connection
     */
    protected void registerServerConnection(ServerConnection server) {
        this.server = server;
    }

    /**
     * Clears the server connection
     */
    protected void unregisterServerConnection() {
        this.server = null;
    }

    /**
     * Called when the client connected to the server
     */
    public void onConnect() {

    }

    /**
     * Called when Client disconnects from the server
     */
    public void onDisconnect() {

    }

    /**
     * Called when the client receives a packet from the server
     * @param packet The received packet
     */
    public void onPacketReceive(Packet packet) {

    }

    /**
     * Sends a packet to the server
     * @param packet The packet
     */
    public void send(Packet packet) {
        this.server.send(packet);
    }

    public boolean isConnected(){
        return this.server != null;
    }
}
