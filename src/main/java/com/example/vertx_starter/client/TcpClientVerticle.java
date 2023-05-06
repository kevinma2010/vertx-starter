package com.example.vertx_starter.client;

import com.example.vertx_starter.codec.XcpCodec;
import com.example.vertx_starter.verticle.TcpServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

public class TcpClientVerticle extends AbstractVerticle {
  public static final String NAME = "TcpClientVerticle";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
    options.setReconnectInterval(3000).setLogActivity(true);
    NetClient client = vertx.createNetClient(options);
    client.connect(TcpServerVerticle.PORT, TcpServerVerticle.ADDRESS, res -> {
      if (res.succeeded()) {
        System.out.println("[TcpClient] Connected!");
        NetSocket socket = res.result();

        socket.handler(XcpCodec.createDecoder(buffer -> {
          System.out.println("[TcpClient] I received message: " + buffer.toString());
          }
        ));

        this.write(socket, "HelloYa1");
        this.write(socket, "HelloYa2");
        this.write(socket, "HelloYa3");
        this.write(socket, "HelloYa4");
        this.write(socket, "HelloYa5");
        this.write(socket, "HelloYa6");
      } else {
        System.out.println("Failed to connect: " + res.cause().getMessage());
      }
    });
  }

  private void write(NetSocket socket, String message) {
    socket.write(XcpCodec.encode(Buffer.buffer(message)));
  }
}
