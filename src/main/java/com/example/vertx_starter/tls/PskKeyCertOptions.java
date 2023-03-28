package com.example.vertx_starter.tls;

import io.vertx.core.Vertx;
import io.vertx.core.net.KeyCertOptions;
import org.conscrypt.PSKKeyManager;

import javax.crypto.SecretKey;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;
import java.util.function.Function;

public class PskKeyCertOptions implements KeyCertOptions {
  private final PSKKeyManager pskKeyManager;

  public static PskKeyCertOptions create(PskKeyStore keyStore) {
    return new PskKeyCertOptions(new PskServerKeyManager(keyStore));
  }

  public static PskKeyCertOptions create(String identity, SecretKey secret) {
    return new PskKeyCertOptions(new PskClientKeyManager(identity, secret));
  }

  public PskKeyCertOptions(PSKKeyManager pskKeyManager) {
    this.pskKeyManager = pskKeyManager;
  }

  @Override
  public KeyCertOptions copy() {
    return new PskKeyCertOptions(pskKeyManager);
  }

  @Override
  public KeyManagerFactory getKeyManagerFactory(Vertx vertx) throws Exception {
    return new PskKeyManagerFactory(pskKeyManager);
  }

  @Override
  public Function<String, X509KeyManager> keyManagerMapper(Vertx vertx) throws Exception {
    return null;
  }
}
