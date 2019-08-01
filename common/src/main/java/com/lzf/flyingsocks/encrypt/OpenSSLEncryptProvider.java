package com.lzf.flyingsocks.encrypt;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import java.io.InputStream;
import java.util.Map;

public class OpenSSLEncryptProvider implements EncryptProvider {
    static final String NAME = "OpenSSL";

    private volatile SslContext sslContext;

    private boolean client;

    private boolean initialize = false;

    @Override
    public synchronized void initialize(Map<String, Object> params) throws Exception {
        if(initialize)
            throw new IllegalStateException("OpenSSLEncryptProvider instance has been initialize");
        client = (boolean)params.get("client");
        sslContext = buildSSLContext(params);
        initialize = true;
    }

    @Override
    public boolean isInboundHandlerSameAsOutboundHandler() {
        return true;
    }

    @Override
    public ChannelInboundHandler decodeHandler(Map<String, Object> params) throws Exception {
        if(!initialize)
            throw new IllegalStateException("OpenSSLEncryptProvider instance must be initial first !");
        return createSSLHandler(params);
    }

    @Override
    public ChannelOutboundHandler encodeHandler(Map<String, Object> params) throws Exception {
        if(!initialize)
            throw new IllegalStateException("OpenSSLEncryptProvider instance must be initial first !");
        return createSSLHandler(params);
    }

    private SslHandler createSSLHandler(Map<String, Object> params) throws Exception {
        if (params == null || params.get("alloc") == null)
            return sslContext.newHandler(ByteBufAllocator.DEFAULT);
        return sslContext.newHandler((ByteBufAllocator) params.get("alloc"));
    }

    private SslContext buildSSLContext(Map<String, Object> params) throws Exception {
        if(client) {
            try (InputStream x509crt = (InputStream) params.get("file.cert.root")) {
                return SslContextBuilder.forClient().trustManager(x509crt).build();
            }
        } else {
            try (InputStream crt = (InputStream) params.get("file.cert");
                 InputStream key = (InputStream) params.get("file.key");
                 InputStream x509crt = (InputStream) params.get("file.cert.root")) {
                return SslContextBuilder.forServer(crt, key)
                        .trustManager(x509crt).clientAuth(ClientAuth.NONE).build();
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}