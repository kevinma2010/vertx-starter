package com.example.vertx_starter.verticle;

import com.example.vertx_starter.tls.psk.PskKeyCertOptions;
import com.example.vertx_starter.tls.psk.PskSSLEngineOptions;
import com.example.vertx_starter.tls.psk.example.ExampleSecretKey;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

public class WebTLSServerVerticle extends AbstractVerticle {
  public static final String NAME = WebTLSServerVerticle.class.getSimpleName();

  public static final int PORT = 8889;
  public static final String ADDRESS = "localhost";
  public static final String WEBSOCKET_PATH = "/websocket";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.get("/hello").handler(this::handleHello);

    HttpServerOptions serverOptions = new HttpServerOptions();

    serverOptions.setSsl(true);
    serverOptions.setSslEngineOptions(new PskSSLEngineOptions());
    serverOptions.setKeyCertOptions(PskKeyCertOptions.create(identity -> new ExampleSecretKey()));

    /*
    String identify = "xxxxxx@device-id"
    String identify = "urn@device-type"
     */

    HttpServer server = vertx.createHttpServer(serverOptions);
    server.webSocketHandler(webSocket -> {
      if (!Objects.equals(webSocket.path(), WEBSOCKET_PATH)) {
        webSocket.reject();
        return;
      }

      webSocket.binaryMessageHandler(buffer -> {
        System.out.println(NAME + " -- I received message from client: " + buffer.toString());
        webSocket.write(Buffer.buffer("Echo: " + buffer));
      });
    });

    server.requestHandler(router).listen(PORT, ADDRESS, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8889");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  void handleHello(RoutingContext context) {
    context.end("hello");
  }
}
