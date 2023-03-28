package com.example.vertx_starter.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;


public class UdpVerticle extends AbstractVerticle {
  public static final String NAME = "UdpVerticle";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    DatagramSocket socket = vertx.createDatagramSocket(new DatagramSocketOptions());
    socket.listen(1234, "0.0.0.0", asyncResult -> {
      if (asyncResult.succeeded()) {
        System.out.println("Listen udp success.");
        socket.handler(packet -> {
          System.out.println("Received an udp message: " + packet.data().toString() + " From: " + packet.sender().toString());
        });
      } else {
        System.out.println("Listen udp failure: " + asyncResult.cause().getMessage());
      }
    });
    send();

    startPromise.complete();
  }

  void send() {
    DatagramSocket socket = vertx.createDatagramSocket(new DatagramSocketOptions());
    Buffer buffer = Buffer.buffer("content");
    vertx.setPeriodic(5000, id -> {
      // send buffer
      socket.send(buffer, 1234, "127.0.0.1", asyncResult -> {
        System.out.println("Send udp succeeded? " + asyncResult.succeeded());
      });
      // send a string
      socket.send("A string used as content", 1234, "127.0.0.1", asyncResult -> {
        System.out.println("Send udp succeeded? " + asyncResult.succeeded());
      });
    });
  }
}
