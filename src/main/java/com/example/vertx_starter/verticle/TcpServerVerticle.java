package com.example.vertx_starter.verticle;

import com.example.vertx_starter.codec.XcpCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

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
    socket.handler(XcpCodec.createDecoder(buffer -> onReceive(socket, buffer)));
  }

  void onReceive(NetSocket socket, Buffer buffer) {
      System.out.println("[TcpServer] I received some bytes: " + buffer.length());
      write(socket, Buffer.buffer("--------echo------").appendBuffer(buffer));
  }

  void handleException(Throwable cause) {
    System.out.println("[TcpServer] handle exception: " + cause.getMessage());
  }


  void write(NetSocket socket, Buffer out) {
    socket.write(XcpCodec.encode(out));
  }
}
