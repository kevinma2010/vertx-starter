package com.example.vertx_starter.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class WebServerVerticle extends AbstractVerticle {
  public static final String NAME = "WebServerVerticle";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.get("/hello").handler(this::handleHello);
    router.route().handler(this::handleAll);
    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  void handleHello(RoutingContext context) {
    String address = context.request().connection().remoteAddress().toString();
    MultiMap queryParams = context.queryParams();
    String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";

    JsonObject result = new JsonObject()
      .put("name", name)
      .put("address", address)
      .put("message", "Hello " + name + " connected from " + address);
    vertx.eventBus().publish("web.access.hello", result.toString());
    context.json(result);
  }

  void handleAll(RoutingContext context) {
    context.response()
      .putHeader("content-type", "text/plain")
      .end("Hello from Vert.x!");
  }
}
