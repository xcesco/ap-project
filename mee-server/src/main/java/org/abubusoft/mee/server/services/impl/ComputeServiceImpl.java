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

package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.aop.LogExecutionTime;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.services.ComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ComputeServiceImpl implements ComputeService {
  private final AsyncTaskExecutor executor;

  @Autowired
  public ComputeServiceImpl(@Qualifier(ApplicationConfiguration.COMPUTE_EXECUTOR) AsyncTaskExecutor executor) {
    this.executor = executor;
  }

  @LogExecutionTime
  @Override
  public CommandResponse execute(ComputeCommand command) {
    try {
      return executor.submit(command::execute).get();
    } catch (InterruptedException e1) {
      Thread.currentThread().interrupt();
      return CommandResponse.error(e1.getCause());
    } catch (ExecutionException e2) {
      return CommandResponse.error(e2.getCause());
    }
  }

}
