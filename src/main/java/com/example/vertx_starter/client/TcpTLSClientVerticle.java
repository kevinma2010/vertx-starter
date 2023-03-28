package com.example.vertx_starter.client;

import com.example.vertx_starter.tls.*;
import com.example.vertx_starter.tls.example.ExampleSecretKey;
import com.example.vertx_starter.verticle.TcpTLSServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.*;

import java.util.concurrent.atomic.AtomicInteger;

public class TcpTLSClientVerticle extends AbstractVerticle {
  public static final String NAME = TcpTLSClientVerticle.class.getSimpleName();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
    options.setReconnectInterval(3000).setLogActivity(true);

    options.setSsl(true);
    options.setSslEngineOptions(new PskSSLEngineOptions());
    options.setKeyCertOptions(PskKeyCertOptions.create("abc", new ExampleSecretKey()));

    NetClient client = vertx.createNetClient(options);
    client.connect(TcpTLSServerVerticle.PORT, TcpTLSServerVerticle.ADDRESS, res -> {
      if (res.succeeded()) {
        System.out.println(NAME + " -- Connected!");

        NetSocket socket = res.result();
        socket.handler(buffer -> System.out.println(NAME + " -- I received message from server: " + buffer.toString()));

        AtomicInteger i = new AtomicInteger();
        vertx.setPeriodic(2000, id -> socket.write("HelloYa " + i.getAndIncrement()));
      } else {
        System.out.println("Failed to connect: " + res.cause().getMessage());
      }
    });
  }
}
