package com.lzf.flyingsocks.client.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class ProxyRequest implements Comparable<ProxyRequest> {

    protected String host;

    protected int port;

    protected Channel clientChannel;

    protected boolean proxy;

    private int hashCode;

    protected ProxyRequest() {
    }

    protected ProxyRequest(String host, int port, Channel channel) {
        this.host = host;
        this.port = port;
        this.clientChannel = channel;
    }

    public final String getHost() {
        return host;
    }

    public final int getPort() {
        return port;
    }

    public boolean needProxy() {
        return proxy;
    }

    protected void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    protected Channel getClientChannel() {
        return clientChannel;
    }

    protected void closeClientChannel() {
        if(clientChannel.isActive())
            clientChannel.close();
    }

    /**
     * 获取客户端发送的请求信息
     * @return 字节容器
     */
    public abstract ByteBuf getClientMessage();

    /**
     * @return 是否确定客户端发送的消息仅有一条，例如调用
     * 一次getClientMessage()方法后不可能再次调用getClientMessage()方法后获取到ByteBuf实例
     */
    public abstract boolean sureMessageOnlyOne();


    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final int hashCode() {
        if(hashCode == 0)
            return hashCode = super.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "ProxyRequest [host:" + host + ", port:" + port + "]";
    }

    @Override
    public int compareTo(ProxyRequest request) {
        if(this.equals(request))
            return 0;

        if(this.hashCode() > request.hashCode())
            return 1;
        else
            return -1;
    }
}