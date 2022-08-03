package io.rusk.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rusk.network.packet.Packet;

public class ServerConnection extends SimpleChannelInboundHandler<Packet> {


    private final Client client;
    private Channel channel;


    public ServerConnection(Client client) {
        this.client = client;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        this.client.onConnect();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.client.onDisconnect();
        this.channel = null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        this.client.onPacketReceive(packet);
    }


    protected void send(Packet packet) {
        this.channel.writeAndFlush(packet);
    }
}
