#!/bin/sh

java -Dlogback.configurationFile=config/logback.xml \
     -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory \
     -jar app.jar