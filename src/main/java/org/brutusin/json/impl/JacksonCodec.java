/*
 * Copyright 2015 brutusin.org
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
package org.brutusin.json.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.brutusin.commons.Pair;
import org.brutusin.commons.io.MetaDataInputStream;
import org.brutusin.json.spi.JsonNode;
import org.brutusin.json.spi.JsonSchema;
import org.brutusin.json.ParseException;
import org.brutusin.json.impl.serializers.InputStreamDeserializer;
import org.brutusin.json.impl.serializers.InputStreamSerializer;
import org.brutusin.json.impl.serializers.JsonNodeDeserializer;
import org.brutusin.json.impl.serializers.JsonNodeSerializer;
import org.brutusin.json.impl.serializers.SerializationContext;
import org.brutusin.json.spi.JsonCodec;

/**
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonCodec extends JsonCodec {

    private static final Map<Class, String> DEFAULT_FORMAT_MAP = new HashMap();

    static {
        DEFAULT_FORMAT_MAP.put(File.class, "file");
        DEFAULT_FORMAT_MAP.put(InputStream.class, "inputstream");
        DEFAULT_FORMAT_MAP.put(MetaDataInputStream.class, "inputstream");
    }

    private final ObjectMapper mapper;
    private final JacksonFactoryWrapper schemaFactory;

    public JacksonCodec() {
        this(null, null);
    }

    public JacksonCodec(ObjectMapper mapper, JacksonFactoryWrapper schemaFactory) {
        if (mapper == null) {
            mapper = new ObjectMapper();

            mapper.setVisibility(
                    mapper.getSerializationConfig().
                    getDefaultVisibilityChecker().
                    withFieldVisibility(JsonAutoDetect.Visibility.ANY).
                    withGetterVisibility(JsonAutoDetect.Visibility.NONE).
                    withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            SimpleModule testModule = new SimpleModule("json-provider-module", new Version(1, 0, 0, null, "org.brutusin", "json-provider"));
            testModule.addSerializer(new JsonNodeSerializer());
            testModule.addDeserializer(JsonNode.class, new JsonNodeDeserializer());
            testModule.addSerializer(new InputStreamSerializer());
            testModule.addDeserializer(InputStream.class, new InputStreamDeserializer());
            testModule.addDeserializer(MetaDataInputStream.class, new InputStreamDeserializer());
            mapper.registerModule(testModule);
        }
        if (schemaFactory == null) {
            schemaFactory = new JacksonFactoryWrapper(new HashMap<Class, String>(DEFAULT_FORMAT_MAP));
        }
        this.mapper = mapper;
        this.schemaFactory = schemaFactory;
    }

    static String addVersion(String jsonSchema) {
        jsonSchema = jsonSchema.replaceAll("\"\\$schema\"\\s*:\\s*\"[^\"]*\"\\s*,?", "");
        if (!jsonSchema.contains("\"$schema\"")) {
            if (jsonSchema.startsWith("{\"type\":")) {
                StringBuilder sb = new StringBuilder(jsonSchema);
                sb.insert(1, "\"$schema\":\"http://brutusin.org/json/json-schema-spec\",");
                return sb.toString();
            }
        }
        return jsonSchema;
    }

    static String addDraftv3(String jsonSchema) {
        jsonSchema = jsonSchema.replaceAll("\"\\$schema\"\\s*:\\s*\"[^\"]*\"\\s*,?", "");
        if (!jsonSchema.contains("\"$schema\"")) {
            if (jsonSchema.startsWith("{\"type\":")) {
                StringBuilder sb = new StringBuilder(jsonSchema);
                sb.insert(1, "\"$schema\":\"http://json-schema.org/draft-03/schema#\",");
                return sb.toString();
            }
        }
        return jsonSchema;
    }

    @Override
    public void registerStringFormat(Class clazz, String format) {
        this.schemaFactory.registerStringFormat(clazz, format);
        SimpleModule testModule = new SimpleModule("json-provider-module:" + format, new Version(1, 0, 0, null, "org.brutusin", "json-provider:" + format));
        testModule.addSerializer(new StdSerializer(clazz) {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(value.toString());
            }
        });
        mapper.registerModule(testModule);
    }

    @Override
    public String transform(Object o) {
        return transformAndGetSerializationCtx(o).getElement1();
    }

    private Pair<String, Map> transformAndGetSerializationCtx(Object o) {
        try {
            String json = mapper.writeValueAsString(o);
            SerializationContext sCtx = SerializationContext.getCurrentContext();
            return new Pair<String, Map>(json, sCtx == null ? null : sCtx.getMap());
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        } finally {
            SerializationContext.closeCurrentContext();
        }
    }

    @Override
    public JsonNode toJsonNode(Object o) {
        Pair<String, Map> pair = transformAndGetSerializationCtx(o);
        try {
            return parse(pair.getElement1(), pair.getElement2());
        } catch (ParseException pe) {
            throw new AssertionError();
        }
    }

    @Override
    public JsonNode parse(String json) throws ParseException {
        return parse(json, (Map) null);
    }

    @Override
    public JsonNode parse(String json, Map<String, InputStream> streams) throws ParseException {
        com.fasterxml.jackson.databind.JsonNode node = load(json);
        return new JacksonNode(node, streams);
    }

    @Override
    public Map<String, InputStream> getStreams(JsonNode node) {
        if (node instanceof JacksonNode) {
            JacksonNode jacksonNode = (JacksonNode) node;
            return jacksonNode.getStreams();
        }
        return null;
    }

    @Override
    public Object parse(String json, Type type) throws ParseException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            JavaType jt = TypeFactory.defaultInstance().constructType(type);
            return mapper.readValue(json, jt);
        } catch (JsonParseException ex) {
            throw new ParseException(ex);
        } catch (JsonMappingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <T> T parse(String json, Class<T> clazz) throws ParseException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonParseException ex) {
            throw new ParseException(ex);
        } catch (JsonMappingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public Pair<Object, Integer> parse(String json, Type type, Map<String, InputStream> streams) throws ParseException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            SerializationContext sc = new SerializationContext(streams);
            SerializationContext.setCurrentContext(sc);
            JavaType jt = TypeFactory.defaultInstance().constructType(type);
            return new Pair<Object, Integer>(mapper.readValue(json, jt), sc.getDeclaredStreams());
        } catch (JsonParseException ex) {
            throw new ParseException(ex);
        } catch (JsonMappingException ex) {
            throw new ParseException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            SerializationContext.closeCurrentContext();
        }
    }

    public <T> Pair<T, Integer> parse(String json, Class<T> clazz, Map<String, InputStream> streams) throws ParseException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            SerializationContext sc = new SerializationContext(streams);
            SerializationContext.setCurrentContext(sc);
            return new Pair<T, Integer>(mapper.readValue(json, clazz), sc.getDeclaredStreams());
        } catch (JsonParseException ex) {
            throw new ParseException(ex);
        } catch (JsonMappingException ex) {
            throw new ParseException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            SerializationContext.closeCurrentContext();
        }
    }

    @Override
    public <T> T load(JsonNode node, Class<T> clazz) {
        if (JsonNode.class.equals(clazz)) {
            return (T) node;
        }
        try {
            Map<String, InputStream> streams;
            if (node instanceof JacksonNode) {
                JacksonNode jn = (JacksonNode) node;
                streams = jn.getStreams();
            } else {
                streams = null;
            }
            return parse(node.toString(), clazz, streams).getElement1();
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object load(JsonNode node, Type type) {
        if (JsonNode.class.equals(type)) {
            return node;
        }
        try {
            Map<String, InputStream> streams;
            if (node instanceof JacksonNode) {
                JacksonNode jn = (JacksonNode) node;
                streams = jn.getStreams();
            } else {
                streams = null;
            }
            return parse(node.toString(), type, streams).getElement1();
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public JsonSchema parseSchema(String json) throws ParseException {
        return new JacksonSchema(json, mapper);
    }

    @Override
    public String getSchemaString(Type type) {
        try {
            mapper.acceptJsonFormatVisitor(mapper.constructType(type), schemaFactory);
            com.fasterxml.jackson.module.jsonSchema.JsonSchema finalSchema = schemaFactory.finalSchema();
            return addVersion(mapper.writeValueAsString(finalSchema));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String quoteAsUTF8(String s) {
        return new String(JsonStringEncoder.getInstance().quoteAsUTF8(s));
    }

    @Override
    public String prettyPrint(String json) throws ParseException {
        try {
            Object obj = parse(json, Object.class);
            return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            throw new ParseException(ex);
        }
    }

    private com.fasterxml.jackson.databind.JsonNode load(String json) throws ParseException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException ex) {
            throw new ParseException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class A extends File {

        public A(String pathname) {
            super(pathname);
        }
    }
}
