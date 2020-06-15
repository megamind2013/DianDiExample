package net.itdiandi.stream.flink.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import net.itdiandi.flink.rpc.common.IMessageHandler;
import net.itdiandi.flink.rpc.common.MessageInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler implements IMessageHandler<MessageInput> {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultHandler.class);
    @Override
    public void handle(ChannelHandlerContext ctx, String requestId, MessageInput input) {
        LOG.error("unrecognized message type {} comes", input.getType());
        ctx.close();
    }
}
