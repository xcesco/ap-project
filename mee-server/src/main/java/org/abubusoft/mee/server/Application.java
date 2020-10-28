/*
 * Copyright 2020 Francesco Benincasa (info@abubusoft.com).
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.abubusoft.mee.server;

import org.abubusoft.mee.server.services.MeeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.google.common.primitives.Ints.tryParse;

@SpringBootApplication(scanBasePackages = {"org.abubusoft.mee.server"})
public class Application implements CommandLineRunner {
  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  private MeeServer meeServer;

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
  }

  @Autowired
  public void setMeeServer(MeeServer meeServer) {
    this.meeServer = meeServer;
  }

  @Override
  public void run(String... args) {
    if (args.length == 1) {
      Integer port = tryParse(args[0]);

      if (port != null && port > 0) {
        logger.info("Listening port {} is specified via command line args", port);
        meeServer.start(port);
      } else {
        logger.error("No valid listening port is specified via command line args");
      }
    } else {
      logger.error("Too many line args");
    }
  }

}
