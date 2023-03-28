package com.example.vertx_starter.tls;

import org.conscrypt.PSKKeyManager;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLEngine;
import java.net.Socket;

public class PskClientKeyManager implements PSKKeyManager {
  private final String identity;
  private final SecretKey secret;

  public PskClientKeyManager(String identity, SecretKey secret) {
    this.identity = identity;
    this.secret = secret;
  }

  @Override
  public String chooseServerKeyIdentityHint(Socket socket) {
    return null;
  }

  @Override
  public String chooseServerKeyIdentityHint(SSLEngine engine) {
    return null;
  }

  @Override
  public String chooseClientKeyIdentity(String identityHint, Socket socket) {
    System.out.println("PskClientKeyManager chooseClientKeyIdentity socket:" + socket + " identityHint:" + identityHint);
    return this.identity;
  }

  @Override
  public String chooseClientKeyIdentity(String identityHint, SSLEngine engine) {
    System.out.println("PskClientKeyManager chooseClientKeyIdentity engine:" + engine + " identityHint:" + identityHint);
    return this.identity;
  }

  @Override
  public SecretKey getKey(String identityHint, String identity, Socket socket) {
    System.out.println("PskClientKeyManager getKey socket:" + socket + " identityHint:" + identityHint + " identity:" + identity);
    return this.secret;
  }

  @Override
  public SecretKey getKey(String identityHint, String identity, SSLEngine engine) {
    System.out.println("PskClientKeyManager getKey engine:" + engine + " identityHint:" + identityHint + " identity:" + identity);
    return this.secret;
  }
}
