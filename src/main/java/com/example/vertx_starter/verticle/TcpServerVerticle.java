package com.example.vertx_starter.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

public class TcpServerVerticle extends AbstractVerticle {
  public static final String NAME = "TcpServerVerticle";
  public static final String ADDRESS = "localhost";
  public static final int PORT = 4321;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    NetServerOptions options = new NetServerOptions()
      .setPort(PORT).setLogActivity(true);
    NetServer server = vertx.createNetServer(options);
    server.connectHandler(this::handleConnection);
    server.exceptionHandler(this::handleException);
    server.listen(res -> {
      if (res.succeeded()) {
        System.out.println("Tcp Server is now listening!");
        startPromise.complete();
      } else {
        System.out.println("Failed to bind!");
        startPromise.fail(res.cause().getMessage());
      }
    });
  }

  void handleConnection(NetSocket socket) {
    socket.handler(buffer -> {
      System.out.println("[TcpServer] I received some bytes: " + buffer.length());
      socket.write("--------echo------");
      socket.write(buffer);
    });
  }

  void handleException(Throwable cause) {
    System.out.println("[TcpServer] handle exception: " + cause.getMessage());
  }
}
