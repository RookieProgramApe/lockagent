package com.lxkj.common.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Guang YANG
 */
class JacksonImpl implements JsonConverter {

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private ObjectMapper mapper;

  {
    this.mapper = new ObjectMapper();
    this.mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    this.mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
      @Override
      public void serialize(
          Object value,
          JsonGenerator jg,
          SerializerProvider sp) throws IOException {
        jg.writeString("");
      }
    });
  }

  @SuppressWarnings("unchecked")
  public <K, V> Map<K, V> parseMap(String json) {
    return JSON.parse(json, Map.class);
  }

  @SuppressWarnings("unchecked")
  public <E> List<E> parseList(String json) {
    return JSON.parse(json, List.class);
  }

  @SuppressWarnings("unchecked")
  public <T> T parse(String json) {
    return (T) JSON.parse(json, Object.class);
  }

  public <T> T parse(String json, Class<T> objectType) {
    try {
      return this.mapper.readValue(json, objectType);
    } catch (IOException e) {
      this.logger.debug(e.getMessage());
      return null;
    }
  }

  public String stringify(Object o) {
    try {
      return this.mapper.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      this.logger.debug(e.getMessage());
      return null;
    }
  }
}
