package com.example.vertx_starter;

import com.example.vertx_starter.client.*;
import com.example.vertx_starter.verticle.*;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

public class Launcher {
  public static void main(String[] args) {
    VertxOptions vertxOptions = new VertxOptions()
      .setWorkerPoolSize(40);

    Vertx vertx = Vertx.vertx(vertxOptions);

    JsonObject config = new JsonObject()
      .put("name", "tim")
      .put("directory", "/blah");
    DeploymentOptions deploymentOptions = new DeploymentOptions()
      .setConfig(config)
      .setInstances(1)
      .setWorkerPoolName("the-specific-pool");

//    deployVerticle(vertx, deploymentOptions, MainVerticle.NAME, new MainVerticle());
//    deployVerticle(vertx, deploymentOptions, WebVerticle.NAME, new WebServerVerticle());
//    deployVerticle(vertx, deploymentOptions, TcpServerVerticle.NAME, new TcpServerVerticle());
//    deployVerticle(vertx, deploymentOptions, TcpClientVerticle.NAME, new TcpClientVerticle());
//    deployVerticle(vertx, deploymentOptions, UdpVerticle.NAME, new UdpVerticle());
    deployVerticle(vertx, deploymentOptions, WebTLSServerVerticle.NAME, new WebTLSServerVerticle());
    deployVerticle(vertx, deploymentOptions, WebTLSClientVerticle.NAME, new WebTLSClientVerticle());
    deployVerticle(vertx, deploymentOptions, TcpTLSServerVerticle.NAME, new TcpTLSServerVerticle());
    deployVerticle(vertx, deploymentOptions, TcpTLSClientVerticle.NAME, new TcpTLSClientVerticle());
  }

  static void deployVerticle(Vertx vertx, DeploymentOptions deploymentOptions, String verticleName, Verticle verticle) {
    vertx.deployVerticle(verticle, deploymentOptions, res -> {
      if (res.succeeded()) {
        System.out.println(verticleName + " deploy success! Deployment id is: " + res.result());
      } else {
        System.out.println(verticleName + " deploy failure: " + res.cause().getMessage());
      }
    });
  }
}
