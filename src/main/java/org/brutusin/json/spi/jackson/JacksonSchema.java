/*
 * Copyright 2015 Ignacio del Valle Alles idelvall@brutusin.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brutusin.json.spi.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.brutusin.commons.json.ParseException;
import org.brutusin.commons.json.spi.JsonNode;
import org.brutusin.commons.json.spi.JsonSchema;
import org.brutusin.commons.json.ValidationException;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonSchema extends JacksonNode implements JsonSchema {

    private transient com.github.fge.jsonschema.main.JsonSchema validator;
    private final ObjectMapper mapper;

    public JacksonSchema(String schema, ObjectMapper mapper) throws ParseException {
        super(load(schema, mapper));
        this.mapper = mapper;
    }

    private static com.fasterxml.jackson.databind.JsonNode load(String schema, ObjectMapper mapper) throws ParseException {
        if (schema == null || schema.trim().isEmpty()) {
            return null;
        }
        try {
            return mapper.readTree(JacksonCodec.addDraftv3(schema));
        } catch (JsonProcessingException ex) {
            throw new ParseException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public final void validate(JsonNode node) throws ValidationException {
        if (!(node instanceof JacksonNode)) {
            throw new IllegalArgumentException("node is not an intance of " + JacksonNode.class.getSimpleName());
        }
        JacksonNode nodeImpl = (JacksonNode) node;
        ProcessingReport report = null;
        try {
            report = getValidator().validate(nodeImpl.getNode());
        } catch (ProcessingException ex) {
            List<String> messages = new ArrayList();
            messages.add(ex.getProcessingMessage().getMessage());
            throw new ValidationException(messages);
        }
        if (!report.isSuccess()) {
            Iterator<ProcessingMessage> iterator = report.iterator();
            List<String> messages = new ArrayList();
            while (iterator.hasNext()) {
                ProcessingMessage processingMessage = iterator.next();
                messages.add(processingMessage.getMessage());
            }
            throw new ValidationException(messages);
        }
    }

    /**
     * Lazy intialization of validator instance
     *
     * @return
     * @throws ProcessingException
     */
    private com.github.fge.jsonschema.main.JsonSchema getValidator() throws ProcessingException {
        if (validator == null) {
            synchronized (this) {
                if (validator == null) {
                    validator = JsonSchemaFactory.byDefault().getJsonSchema(getNode());
                }
            }
        }
        return validator;
    }

    @Override
    public Type getSchemaType() {
        JsonNode node = get("type");
        if (node == null) {
            return null;
        }
        return Type.valueOf(node.asString().toUpperCase());
    }

    @Override
    public JsonSchema getPropertySchema(String property) {
        JacksonNode propNode = get("properties");
        if (propNode == null) {
            return null;
        }
        JacksonNode node = propNode.get(property);
        if (node == null) {
            return null;
        }
        try {
            return new JacksonSchema(node.getNode().toString(), this.mapper);
        } catch (ParseException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public JsonSchema getItemSchema() {
        return getSubSchema("items");
    }

    @Override
    public JsonSchema getAdditionalPropertySchema() {
        return getSubSchema("additionalProperties");
    }

    private JsonSchema getSubSchema(String name) {
        JacksonNode node = get(name);
        if (node == null) {
            return null;
        }
        try {
            return new JacksonSchema(node.getNode().toString(), this.mapper);
        } catch (ParseException ex) {
            throw new AssertionError();
        }
    }

}
