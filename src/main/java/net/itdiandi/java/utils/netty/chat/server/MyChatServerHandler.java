package net.itdiandi.java.utils.netty.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MyChatServerHandler extends SimpleChannelInboundHandler<String> {
	
	/**
	 * ChannelGroup，它是线程安全的，ChannelGroup存储了已连接的Channel，Channel关闭会自动从ChannelGroup中移除，无需担心Channel生命周期。同时，可以对这些Channel做各种批量操作，可以以广播的形式发送一条消息给所有的Channels，调用它的writeAndFlush方法来实现。
ChannelGroup可以进一步理解为设计模式中的发布-订阅模型，其底层是通过ConcurrentHashMap进行存储所有Channel的
	 */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override 
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 这里要区分下是否是自己发的消息
        Channel channel = ctx.channel();
        // 这里使用了Java8的lambda表达式
        channelGroup.forEach(ch -> {
        	if(ch.isWritable()){
	            if (ch == channel) { // 两个channel对象地址相同
	                System.out.println("服务器端转发聊天消息：【自己】发送的消息, 内容：" + msg + "\n");
	                ch.writeAndFlush("【自己】发送的消息, 内容：" + msg + "\n");
	            } else {
	                System.out.println("服务器端转发聊天消息："+ ch.remoteAddress() + "发送的消息，内容：" + msg + "\n");
	                ch.writeAndFlush(ch.remoteAddress() + "发送的消息，内容：" + msg + "\n");
	            }
	            
        	}
        });
    }
    
    // -----------以下覆写的方法是ChannelInboundHandlerAdapter中的方法---------------
    @Override public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[服务器] - " + channel.remoteAddress() + " 加入了\n");

        // 先写入到客户端，最后再将自己添加到ChannelGroup中
        channelGroup.add(channel);
    }

    @Override public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[服务器] - " + channel.remoteAddress() + " 离开了\n");

        // 这里channelGroup会自动进行调用，所以这行代码不写也是可以的。
        channelGroup.remove(channel);
    }

    /**
     * 当一个新的连接已经被建立时，channelActive将会被调用
     */
    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了\n");
    }

    @Override public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 下线了\n");
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
