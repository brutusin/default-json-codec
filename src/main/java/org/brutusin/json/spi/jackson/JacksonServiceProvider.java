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
package org.brutusin.json.spi.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import java.io.IOException;
import org.brutusin.commons.json.spi.JsonNode;
import org.brutusin.commons.json.spi.JsonSchema;
import org.brutusin.commons.json.ParseException;
import org.brutusin.commons.json.spi.JsonService;

/**
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonServiceProvider extends JsonService {

    private final ObjectMapper mapper;
    private final SchemaFactoryWrapper schemaFactory;

    public JacksonServiceProvider() {
        this(null, null);
    }

    public JacksonServiceProvider(ObjectMapper mapper, SchemaFactoryWrapper schemaFactory) {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        if (schemaFactory == null) {
            schemaFactory = new JacksonFactoryWrapper();
        }
        this.mapper = mapper;
        this.schemaFactory = schemaFactory;
    }

    public String transform(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JsonNode parse(String json) throws ParseException {
        com.fasterxml.jackson.databind.JsonNode node = load(json);
        return new JacksonNode(node);
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

    public <T> T parse(String json, Class<T> clazz) throws ParseException {
        try {
            if (json == null || json.trim().isEmpty()) {
                return null;
            }
            return mapper.readValue(json, clazz);
        } catch (JsonParseException ex) {
            throw new ParseException(ex);
        } catch (JsonMappingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JsonSchema parseSchema(String json) throws ParseException {
        com.fasterxml.jackson.databind.JsonNode node = load(json);
        try {
            com.github.fge.jsonschema.main.JsonSchema jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(node);
            JacksonSchema ret = new JacksonSchema();
            ret.setImpl(jsonSchema);
            return ret;
        } catch (ProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getSchema(Class clazz) {
        try {
            mapper.acceptJsonFormatVisitor(mapper.constructType(clazz), schemaFactory);
            com.fasterxml.jackson.module.jsonSchema.JsonSchema finalSchema = schemaFactory.finalSchema();
            return mapper.writeValueAsString(finalSchema);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String quoteAsUTF8(String s) {
        return new String(JsonStringEncoder.getInstance().quoteAsUTF8(s));
    }
}
