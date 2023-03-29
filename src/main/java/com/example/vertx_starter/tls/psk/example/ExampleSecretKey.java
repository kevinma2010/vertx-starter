package com.example.vertx_starter.tls.psk.example;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class ExampleSecretKey implements SecretKey {
  private final byte[] secret = "secret".getBytes(StandardCharsets.UTF_8);

  @Override
  public String getAlgorithm() {
    return null;
  }

  @Override
  public String getFormat() {
    return null;
  }

  @Override
  public byte[] getEncoded() {
    return this.secret;
  }
}
