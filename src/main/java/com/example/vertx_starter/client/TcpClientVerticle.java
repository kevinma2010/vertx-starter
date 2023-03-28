package com.example.vertx_starter.client;

import com.example.vertx_starter.verticle.TcpServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

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
        socket.handler(buffer -> {
          System.out.println("[TcpClient] I received message: " + buffer.toString());
        });
        socket.write("HelloYa");
      } else {
        System.out.println("Failed to connect: " + res.cause().getMessage());
      }
    });
  }
}
