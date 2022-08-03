package io.rusk.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rusk.network.packet.Packet;

public class ClientConnection extends SimpleChannelInboundHandler<Packet> {


    private final Server server;
    private Channel channel;

    public ClientConnection(Server server) {
        this.server = server;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        this.server.registerClient(this);

        this.server.onClientConnect(this);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.server.onClientDisconnect(this);

        this.channel = null;
        this.server.unregisterClient(this);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        this.server.onPacketReceive(this, packet);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }

    public void send(Packet packet) {
        this.channel.writeAndFlush(packet);
    }


}
