package com.example.vertx_starter.tls;

import io.netty.handler.ssl.util.SimpleKeyManagerFactory;
import org.conscrypt.PSKKeyManager;

import javax.net.ssl.KeyManager;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;
import java.util.Arrays;

public class PskKeyManagerFactory extends SimpleKeyManagerFactory {
  private final PSKKeyManager pskKeyManager;

  public PskKeyManagerFactory(PSKKeyManager pskKeyManager) {
    this.pskKeyManager = pskKeyManager;
  }

  @Override
  protected void engineInit(KeyStore keyStore, char[] var2) throws Exception {
    System.out.println("PskKeyManagerFactory engineInit: " + keyStore + " " + Arrays.toString(var2));
  }

  @Override
  protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    System.out.println("PskKeyManagerFactory engineInit: " + managerFactoryParameters);
  }

  @Override
  protected KeyManager[] engineGetKeyManagers() {
    KeyManager[] keyManagers = new KeyManager[1];
    keyManagers[0] = pskKeyManager;
    return keyManagers;
  }
}
