package net.itdiandi.java.utils.netty.chat.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class ChildChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel e) throws Exception {
    	ChannelPipeline channelPipeline = e.pipeline();

        // 添加基于\r \n界定符的解码器
        channelPipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
        channelPipeline.addLast(new StringDecoder(CharsetUtil.UTF_8)); // 添加字符串解码器
        channelPipeline.addLast(new StringEncoder(CharsetUtil.UTF_8)); // 添加字符串编码器
        
//        e.pipeline().addLast("http-codec", new HttpServerCodec());
//        e.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
//        e.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        channelPipeline.addLast(new MyChatServerHandler());// 自定义处理器
//        channelPipeline.addLast("handler", new MyWebSocketServerHandler());
    }
}
