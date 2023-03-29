package com.example.vertx_starter.tls.psk;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.vertx.core.net.SSLEngineOptions;
import io.vertx.core.spi.tls.SslContextFactory;
import org.conscrypt.OpenSSLProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import java.util.HashSet;
import java.util.Set;

public class PskSSLEngineOptions extends SSLEngineOptions {
  @Override
  public SSLEngineOptions copy() {
    return new PskSSLEngineOptions();
  }

  @Override
  public SslContextFactory sslContextFactory() {
    return new MySslContextFactory();
  }

  static class MySslContextFactory implements SslContextFactory {

    private boolean forClient;
    private KeyManagerFactory kmf;
    private final Set<String> defaultCiphers = new HashSet<>();
    private final Set<String> defaultProtocols = new HashSet<>();

    public MySslContextFactory() {
      defaultCiphers.add("TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256");
      defaultCiphers.add("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA");
      defaultCiphers.add("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA");
      defaultCiphers.add("TLS_PSK_WITH_AES_128_CBC_SHA");
      defaultCiphers.add("TLS_PSK_WITH_AES_256_CBC_SHA");

      defaultProtocols.add("TLSv1.2");
    }


    @Override
    public SslContextFactory forClient(boolean forClient) {
      this.forClient = forClient;
      return this;
    }

    @Override
    public SslContextFactory keyMananagerFactory(KeyManagerFactory kmf) {
      this.kmf = kmf;
      return this;
    }

    @Override
    public SslContext create() throws SSLException {
      return createContext(forClient, kmf);
    }

    private SslContext createContext(boolean client, KeyManagerFactory kmf) throws SSLException {
      System.out.println("createContext client:" + client + " kmf:" + kmf);
      SslContextBuilder builder;
      if (client) {
        builder = SslContextBuilder.forClient();
        builder.keyManager(kmf);
      } else {
        builder = SslContextBuilder.forServer(kmf);
      }
      builder.sslContextProvider(new OpenSSLProvider());
      builder.protocols(defaultProtocols);
      builder.ciphers(defaultCiphers);

      return builder.build();
    }
  }

}
