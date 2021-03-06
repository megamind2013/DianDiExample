package net.itdiandi.java.utils.netty.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyCatClient {
	public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new MyChatClientInitalizer());

//            ChannelFuture channelFuture = bootstrap.connect("localhost", 7397).sync();
            ChannelFuture channelFuture = bootstrap.connect("localhost", 7397).sync();
            // channelFuture.channel().closeFuture().sync();

            // 从控制台不断的读取输入
            boolean running = true;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
                while (running) {
                    channelFuture.channel().writeAndFlush(br.readLine() + "\r\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}