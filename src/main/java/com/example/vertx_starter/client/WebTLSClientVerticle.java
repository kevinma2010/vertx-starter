package com.example.vertx_starter.client;

import com.example.vertx_starter.tls.psk.PskKeyCertOptions;
import com.example.vertx_starter.tls.psk.PskSSLEngineOptions;
import com.example.vertx_starter.tls.psk.example.ExampleSecretKey;
import com.example.vertx_starter.verticle.WebTLSServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

import java.util.concurrent.atomic.AtomicInteger;

public class WebTLSClientVerticle extends AbstractVerticle {
  public static final String NAME = WebTLSClientVerticle.class.getSimpleName();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpClientOptions options = new HttpClientOptions();

    options.setSsl(true);
    options.setSslEngineOptions(new PskSSLEngineOptions());
    options.setKeyCertOptions(PskKeyCertOptions.create("abc", new ExampleSecretKey()));

    HttpClient client = vertx.createHttpClient(options);


    client.webSocket(WebTLSServerVerticle.PORT, WebTLSServerVerticle.ADDRESS, WebTLSServerVerticle.WEBSOCKET_PATH).onSuccess(ws -> {
      System.out.println(NAME + " -- Connected!");

      ws.handler(buffer -> System.out.println(NAME + " -- I received message from server: " + buffer.toString()));

      AtomicInteger i = new AtomicInteger();
      vertx.setPeriodic(2000, id -> ws.writeBinaryMessage(Buffer.buffer("HelloYa " + i.getAndIncrement())));
    }).onFailure(startPromise::fail);
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    super.stop(stopPromise);
  }
}
