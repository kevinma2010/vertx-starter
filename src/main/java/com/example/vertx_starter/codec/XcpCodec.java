package com.example.vertx_starter.codec;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class XcpCodec {
  private static final String HEAD_FLAG = "XCP";

  public static Buffer encode(Buffer buffer) {
    return Buffer.buffer(HEAD_FLAG).appendInt(buffer.length()).appendBuffer(buffer);
  }

  public static Handler<Buffer> createDecoder(Handler<Buffer> handler) {
    RecordParser parser = RecordParser.newFixed(7);

    parser.setOutput(new Handler<Buffer>() {
      private int size = -1;

      @Override
      public void handle(Buffer event) {
        if (size == -1) {
          byte[] flag = event.getBytes(0, 3);
          if (!new String(flag).equals("XCP")) {
            return;
          }
          size = event.getInt(3);
          parser.fixedSizeMode(size);
        } else {
          handler.handle(event.copy());
          parser.fixedSizeMode(7);
          size = -1;
        }
      }
    });

    return parser;
  }
}
