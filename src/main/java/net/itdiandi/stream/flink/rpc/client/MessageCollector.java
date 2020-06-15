package net.itdiandi.stream.flink.rpc.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.itdiandi.flink.rpc.common.MessageInput;
import net.itdiandi.flink.rpc.common.MessageOutput;
import net.itdiandi.flink.rpc.common.MessageRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class MessageCollector extends ChannelInboundHandlerAdapter {

    private final static Logger LOG = LoggerFactory.getLogger(MessageCollector.class);
    private MessageRegistry registry;
    private RPCClient client;
    private ChannelHandlerContext context;
    private Throwable ConnectionClosed = new Exception("rpc connection not active error");
    private ConcurrentMap<String, RpcFuture<?>> pendingTasks = new ConcurrentHashMap<>();


    public MessageCollector(MessageRegistry registry, RPCClient client) {
        this.registry = registry;
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.context = null;
        pendingTasks.forEach((__, future) -> {
            future.fail(ConnectionClosed);
        });
        pendingTasks.clear();
        // 尝试重连
        ctx.channel().eventLoop().schedule(() -> {
            client.reconnect();
        }, 1, TimeUnit.SECONDS);
    }

    public <T> RpcFuture<T> send(MessageOutput output) {
        ChannelHandlerContext ctx = context;
        RpcFuture<T> future = new RpcFuture<T>();
        if (ctx != null) {
            ctx.channel().eventLoop().execute(() -> {
                pendingTasks.put(output.getRequestId(), future);
                ctx.writeAndFlush(output);
            });
        } else {
            future.fail(ConnectionClosed);
        }
        return future;
    }

   //客户端拿到服务端返回的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof MessageInput)) {
            return;
        }
        MessageInput input = (MessageInput) msg;
        // 业务逻辑在这里
        Class<?> clazz = registry.get(input.getType());
        if (clazz == null) {
            LOG.error("unrecognized msg type {}", input.getType());
            return;
        }
        Object o = input.getPayload(clazz);
        @SuppressWarnings("unchecked")
        RpcFuture<Object> future = (RpcFuture<Object>) pendingTasks.remove(input.getRequestId());
        if (future == null) {
            LOG.error("future not found with type {}", input.getType());
            return;
        }
        System.out.println("客户端拿到服务端返回的数据: " + o);
        future.success(o);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    public void close() {
        ChannelHandlerContext ctx = context;
        if (ctx != null) {
            ctx.close();
        }
    }
}
