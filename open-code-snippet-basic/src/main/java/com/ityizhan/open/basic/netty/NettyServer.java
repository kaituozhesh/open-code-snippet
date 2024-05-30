package com.ityizhan.open.basic.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * @Description: 消息接受服务端
 * @ClassName: NettyServer
 * @Auther: lin
 * @Date: 2024/5/30 17:28
 * @Version: 1.0
 */
public class NettyServer {
    private static int HEADER_LENGTH = 4;

    public static void main(String[] args) {
        try {
            new NettyServer().bind(1088);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void bind(int port) {
        ServerBootstrap server = new ServerBootstrap(
                new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // 构造对应的pipeline
        server.setPipelineFactory(() -> {
            ChannelPipeline pipelines = Channels.pipeline();
            pipelines.addLast(MessageHandler.class.getName(), new MessageHandler());
            return pipelines;
        });
        // 监听端口
        server.bind(new InetSocketAddress(port));
    }

    // 处理消息
    static class MessageHandler extends SimpleChannelHandler {

        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            // 接收客户端请求
            ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
            String message = new String(buffer.readBytes(buffer.readableBytes()).array(), StandardCharsets.UTF_8);
            System.out.println("<服务端>收到内容=" + message);

            // 给客户端发送回执
            byte[] body = "服务端已收到".getBytes();
            byte[] header = ByteBuffer.allocate(HEADER_LENGTH).order(ByteOrder.BIG_ENDIAN).putInt(body.length).array();
            Channels.write(ctx.getChannel(), ChannelBuffers.wrappedBuffer(header, body));
            System.out.println("<服务端>发送回执,time=" + System.currentTimeMillis());

        }
    }
}
