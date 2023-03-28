package com.example.vertx_starter.verticle;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;

import java.util.*;

public class MainVerticle extends AbstractVerticle {
  public static final String NAME = "MainVerticle";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Hello Vert.x!");

    // 读取配置
    readConfig();

    // 读取系统环境变量
    readSystemEnv();

    // 上下文管理
    contextInfo();

    // 一次性延时执行器
    vertx.setTimer(1000, id -> {
      System.out.println("And one second later this is printed");
    });

    // 每5秒执行的定时器
    vertx.setPeriodic(5000, id -> {
      System.out.println("And every five second this is printed: timer fired! " + new Date());
    });

    // Future 异步
    futureResult();

    // Future 组合
    futureComposition();

    // 消息总线
    eventBus();

    startPromise.complete();
  }

  void executeBlocking() {

  }

  void eventBus() {
    EventBus eb = vertx.eventBus();
    eb.consumer("web.access.hello", message -> {
      System.out.println("I have received a message: " + message.body());
    });
  }

  void contextInfo() {
    if (context.isEventLoopContext()) {
      System.out.println("Context attached to Event Loop");
    } else if (context.isWorkerContext()) {
      System.out.println("Context attached to Worker Thread");
    } else if (!Context.isOnVertxThread()) {
      System.out.println("Context not attached to a thread managed by vert.x");
    }
  }

  void readConfig() {
    System.out.println("read config name: " + config().getString("name"));
  }

  void readSystemEnv() {
    System.out.println("JAVA_HOME: " + System.getenv("JAVA_HOME"));
  }

  /**
   * future 协作
   */
  void futureCoordination() {
    List<Future> futureList = Collections.emptyList();
    CompositeFuture.all(futureList);
    CompositeFuture.any(futureList);
    CompositeFuture.join(futureList);
  }

  /**
   * Future 组合
   */
  void futureComposition() {
    FileSystem fs = vertx.fileSystem();

    Future<Void> future = fs
      .createFile("/data/foo")
      .compose(v -> {
        return fs.writeFile("/data/foo", Buffer.buffer("hello"));
      })
      .compose(v -> {
        return fs.move("/data/foo", "/data/bar");
      });

    future.onComplete((AsyncResult<Void> ar) -> {
      if (ar.succeeded()) {
        System.out.println("create and write success!");
      } else {
        System.out.println("create and write failure: " + ar.cause().getMessage());
      }
    });
  }

  /**
   * Future 异步结果
   */
  void futureResult() {
    FileSystem fs = vertx.fileSystem();

    Future<FileProps> future = fs.props("/my_file.txt");
    future.onComplete((AsyncResult<FileProps> ar) -> {
      if (ar.succeeded()) {
        FileProps props = ar.result();
        System.out.println("File size: " + props.size());
      } else {
        System.out.println("Failure: " + ar.cause().getMessage());
      }
    });
  }

}
