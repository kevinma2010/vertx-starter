package com.example.vertx_starter.tls;

import org.conscrypt.PSKKeyManager;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class PskServerKeyManager implements PSKKeyManager {
  private final AtomicLong identityHintId = new AtomicLong();
  private final PskKeyStore keyStore;

  public PskServerKeyManager(PskKeyStore keyStore) {
    this.keyStore = keyStore;
  }

  public String getIdentityHint() {
    return String.valueOf(identityHintId.getAndIncrement());
  }

  @Override
  public String chooseServerKeyIdentityHint(Socket socket) {
    System.out.println("PskServerKeyManager chooseServerKeyIdentityHint socket:" + socket);
    return getIdentityHint();
  }

  @Override
  public String chooseServerKeyIdentityHint(SSLEngine engine) {
    System.out.println("PskServerKeyManager chooseServerKeyIdentityHint engine: " + engine);
    return getIdentityHint();
  }

  @Override
  public String chooseClientKeyIdentity(String identityHint, Socket socket) {
    return null;
  }

  @Override
  public String chooseClientKeyIdentity(String identityHint, SSLEngine engine) {
    return null;
  }

  @Override
  public SecretKey getKey(String identityHint, String identity, Socket socket) {
    System.out.println("PskServerKeyManager getKey socket:" + socket + " identityHint:" + identityHint + " identity:" + identity);
    return this.keyStore.getSecretKey(identity);
  }

  @Override
  public SecretKey getKey(String identityHint, String identity, SSLEngine engine) {
    System.out.println("PskServerKeyManager getKey engine:" + engine + " identityHint:" + identityHint + " identity:" + identity);
    return this.keyStore.getSecretKey(identity);
  }
}
