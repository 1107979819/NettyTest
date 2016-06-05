import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter { // (1)

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("a");


        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("b");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("c");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("d");
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("e");
        super.channelReadComplete(ctx);
    }

    public ServerHandler() {
        super();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        // Discard the received data silently.

        System.out.println("f");
        ((ByteBuf) msg).release(); // (3)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                .remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();

        System.out.println("g"+cause.getMessage()+" ip:"+clientIP);
        cause.printStackTrace();
        ctx.close();
    }
}