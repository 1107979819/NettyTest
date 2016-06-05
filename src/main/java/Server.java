import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

/**
 * Created by WYL on 2016/6/5.
 */
public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new DiscardServerHandler());
                            ch.pipeline().addLast(new ServerHandler());
                            ch.pipeline().addLast(new Handler());
//

                            System.out.println("host:"+ch.localAddress().getHostName());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8099;
        }
        new Server(port).run();
    }

    public class Handler extends SimpleChannelInboundHandler<HttpObject> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
                throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest mReq = (HttpRequest) msg;
                String clientIP = mReq.headers().get("X-Forwarded-For");
                if (clientIP == null) {
                    InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                            .remoteAddress();
                    clientIP = insocket.getAddress().getHostAddress();
                }
                System.out.println(" clientIP :"+ clientIP );
            }else
            {
                System.out.println("is not httprequest");
            }
        }
    }

}
