package com.example.vertx_starter.verticle;

import com.example.vertx_starter.tls.psk.PskSSLEngineOptions;
import com.example.vertx_starter.tls.psk.PskKeyCertOptions;
import com.example.vertx_starter.tls.psk.example.ExampleSecretKey;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.*;

public class TcpTLSServerVerticle extends AbstractVerticle {
  public static final String NAME = TcpTLSServerVerticle.class.getSimpleName();
  public static final String ADDRESS = "localhost";
  public static final int PORT = 4322;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    NetServerOptions options = new NetServerOptions()
      .setPort(PORT).setLogActivity(true);

    options.setSsl(true);
    options.setSslEngineOptions(new PskSSLEngineOptions());
    options.setKeyCertOptions(PskKeyCertOptions.create(identity -> new ExampleSecretKey()));

    NetServer server = vertx.createNetServer(options);
    server.connectHandler(this::handleConnection);
    server.exceptionHandler(this::handleException);
    server.listen(res -> {
      if (res.succeeded()) {
        System.out.println(NAME + " is now listening!");
        startPromise.complete();
      } else {
        System.out.println(NAME + "Failed to bind!");
        startPromise.fail(res.cause().getMessage());
      }
    });
  }

  void handleConnection(NetSocket socket) {
    socket.handler(buffer -> {
      System.out.println(NAME +  " -- I received message from client: " + buffer);
      socket.write("Echo: ");
      socket.write(buffer);
    });
  }

  void handleException(Throwable cause) {
    System.out.println(NAME + " handle exception: " + cause.getMessage());
  }
}
