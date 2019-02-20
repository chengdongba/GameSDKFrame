/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.creative.gamesdk.common.utils_base.frame.google.gson.internal.bind;

import com.creative.gamesdk.common.utils_base.frame.google.gson.Gson;
import com.creative.gamesdk.common.utils_base.frame.google.gson.JsonSyntaxException;
import com.creative.gamesdk.common.utils_base.frame.google.gson.TypeAdapter;
import com.creative.gamesdk.common.utils_base.frame.google.gson.TypeAdapterFactory;
import com.creative.gamesdk.common.utils_base.frame.google.gson.reflect.TypeToken;
import com.creative.gamesdk.common.utils_base.frame.google.gson.stream.JsonReader;
import com.creative.gamesdk.common.utils_base.frame.google.gson.stream.JsonToken;
import com.creative.gamesdk.common.utils_base.frame.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Adapter for java.sql.Date. Although this class appears stateless, it is not.
 * DateFormat captures its time zone and locale when it is created, which gives
 * this class state. DateFormat isn't thread safe either, so this class has
 * to synchronize its read and write methods.
 */
public final class SqlDateTypeAdapter extends TypeAdapter<Date> {
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
    @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      return typeToken.getRawType() == Date.class
          ? (TypeAdapter<T>) new SqlDateTypeAdapter() : null;
    }
  };

  private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

  @Override
  public synchronized Date read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    try {
      final long utilDate = format.parse(in.nextString()).getTime();
      return new Date(utilDate);
    } catch (ParseException e) {
      throw new JsonSyntaxException(e);
    }
  }

  @Override
  public synchronized void write(JsonWriter out, Date value) throws IOException {
    out.value(value == null ? null : format.format(value));
  }
}
