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

package org.abubusoft.mee.server.exceptions;

public abstract class AppAssert {
  private AppAssert() {

  }

  public static void assertTrue(boolean condition, Class<? extends AppRuntimeException> exceptionClazz, String message, Object... params) {
    if (!condition) {
      AppRuntimeException exception;
      try {
        exception = exceptionClazz.getConstructor(String.class).newInstance(String.format(message, params));
      } catch (Exception e) {
        exception = new AppRuntimeException(String.format("Can not instantiate %s for: %s", exceptionClazz.getSimpleName(), String.format(message, params)));
      }
      throw exception;
    }
  }

  public static void fail(Class<? extends AppRuntimeException> exceptionClazz, String message, Object... params) {
    assertTrue(false, exceptionClazz, message, params);
  }
}
