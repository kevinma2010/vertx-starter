package com.example.vertx_starter.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.streams.Pump;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Http 代理服务器
 */
public class ProxyServerVerticle extends AbstractVerticle {
  private HttpServer httpServer;
  private HttpClient httpClient;
  private final int port = 3001;

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new ProxyServerVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    this.httpClient = vertx.createHttpClient();
    this.httpServer = vertx.createHttpServer();
    this.httpServer.requestHandler(req -> {
      System.out.println("Received request: " + req.method() + " " + req.uri());
      if (req.method().toString().equals("CONNECT")) {
        this.handleConnect(req);
      } else {
        if (req.path().equals("/proxy")) {
          this.handleProxyRequest(req);
        } else {
          this.handleRequest(req);
        }
      }
    });

    this.httpServer.listen(port, ar -> {
      if (ar.succeeded()) {
        System.out.println("ProxyServerVerticle started on port " + port);
        startPromise.complete();
      } else {
        System.out.println("ProxyServerVerticle failed to start on port " + port + ", exception: " + ar.cause());
        startPromise.fail(ar.cause());
      }
    });
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    this.httpServer.close(ar -> {
      if (ar.succeeded()) {
        System.out.println("ProxyServerVerticle stopped");
        stopPromise.complete();
      } else {
        System.out.println("ProxyServerVerticle failed to stop, exception: " + ar.cause());
        stopPromise.fail(ar.cause());
      }
    });
  }

  private void handleConnect(HttpServerRequest request) {
    String[] parts = request.uri().split(":");
    String host = parts[0];
    int port = Integer.parseInt(parts[1]);

    request.toNetSocket()
      .onSuccess(clientSocket -> {
        long startTime = System.currentTimeMillis();
        vertx.createNetClient().connect(port, host)
          .onSuccess(serverSocket -> {
            serverSocket.closeHandler(v -> clientSocket.close().onSuccess(v1 -> {
              long endTime = System.currentTimeMillis();
              System.out.printf("Completed request: %s %s, cost: %d ms\n", request.method(), request.uri(), endTime - startTime);
            }));
            serverSocket.exceptionHandler(v -> clientSocket.close().onSuccess(v1 -> {
              long endTime = System.currentTimeMillis();
              System.out.printf("Completed request: %s %s, cost: %d ms\n", request.method(), request.uri(), endTime - startTime);
            }));

            // 连接建立成功，将连接返回给客户端
            clientSocket.pipeTo(serverSocket);
            serverSocket.pipeTo(clientSocket);
          })
          .onFailure(cause -> {
            // 连接建立失败，向客户端返回错误消息
            clientSocket.write("HTTP/1.1 502 Bad Gateway\r\n\r\n");
            clientSocket.close();
          });
      })
      .onFailure(cause -> request.response().setStatusCode(502).end(cause.getMessage()));
  }

  private void handleRequest(HttpServerRequest request) {
    String url = request.path();
    if (url == null) {
      request.response().setStatusCode(400).end("Missing URL parameter");
      return;
    }

    if (request.query() != null && !request.query().isEmpty()) {
      url += "?" + request.query();
    }

    // Connect to server B and send file download request
    RequestOptions requestOptions = new RequestOptions();
    requestOptions.setMethod(request.method());
    requestOptions.setHost(request.host());
    requestOptions.setURI(url);
    request.headers().forEach(requestOptions::putHeader);

    this.doRequest(request, requestOptions);
  }

  private void handleProxyRequest(HttpServerRequest request) {
    String url = request.getParam("url");
    if (url == null) {
      request.response().setStatusCode(400).end("Missing URL parameter");
      return;
    }

    if (request.method() != HttpMethod.GET) {
      request.response().setStatusCode(405).end("Method not allowed");
      return;
    }

    URI uri;
    try {
      uri = new URI(url);
    } catch (URISyntaxException e) {
      request.response().setStatusCode(400).end("Invalid URL parameter");
      return;
    }

    RequestOptions requestOptions = new RequestOptions();
    request.headers().forEach(requestOptions::putHeader);
    requestOptions.removeHeader("Host");
    requestOptions.setMethod(HttpMethod.GET);
    requestOptions.setHost(uri.getHost());
    requestOptions.setSsl(uri.getScheme().equals("https"));
    requestOptions.setURI(uri.getRawPath());
    if (uri.getPort() != -1)  {
      requestOptions.setPort(uri.getPort());
    } else {
      requestOptions.setPort(requestOptions.isSsl() ? 443 : 80);
    }

    this.doRequest(request, requestOptions);
  }

  private void doRequest(HttpServerRequest request, RequestOptions requestOptions) {
    // Connect to server B and send file download request
    httpClient.request(requestOptions)
      .compose(clientRequest -> {
        clientRequest.exceptionHandler(throwable -> {
          request.response().setStatusCode(500).end("Server error");
        });
        return clientRequest.send();
      })
      .onSuccess(clientResponse -> {
        // Forward server B's response to the client
        HttpServerResponse response = request.response();
        response.setStatusCode(clientResponse.statusCode());
        response.setStatusMessage(clientResponse.statusMessage());
        clientResponse.headers().forEach(response::putHeader);
        response.setChunked(true);
        Pump.pump(clientResponse, response).start();
        clientResponse.endHandler(v -> response.end());
      })
      .onFailure(Throwable::printStackTrace);
  }

}
