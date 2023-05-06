package com.example.vertx_starter.verticle;

import io.vertx.core.*;

import java.time.LocalDateTime;

public class FutureVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new FutureVerticle());
  }

  @Override
  public void start() throws Exception {
    Future<Void> future = doSomething(0);

    for (int i = 1; i < 10; i++) {
      int finalI = i;
//      future = future.compose(v -> doSomething(finalI));
      future = future.compose(v -> {
        Promise<Void> promise = Promise.promise();
        vertx.setTimer(1000, id -> {
          System.out.println(LocalDateTime.now() + ": Future "+ finalI +" fired");
          promise.complete();
        });
        return promise.future();
      });
    }

    future.onComplete(ar -> {
      if (ar.succeeded()) {
        System.out.println("All futures completed successfully");
      } else {
        System.err.println("One or more futures failed");
      }
      vertx.close();
    });

  }

  private Future<Void> doSomething(int index) {
    Promise<Void> promise = Promise.promise();
    vertx.setTimer(1000, id -> {
      System.out.println(LocalDateTime.now() + ": Future "+ index +" fired");
      promise.complete();
    });
    return promise.future();
  }
}
