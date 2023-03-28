package com.example.vertx_starter.tls;

import javax.crypto.SecretKey;

public interface PskKeyStore {
  public SecretKey getSecretKey(String identity);
}
