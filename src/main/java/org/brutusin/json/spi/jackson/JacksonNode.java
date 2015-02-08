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

import com.fasterxml.jackson.databind.node.JsonNodeType;
import java.util.Iterator;
import org.brutusin.commons.json.spi.JsonNode;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonNode implements JsonNode {

    private final com.fasterxml.jackson.databind.JsonNode node;

    public JacksonNode(com.fasterxml.jackson.databind.JsonNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node can not be null");
        }
        this.node = node;
    }

    @Override
    public Type getNodeType() {
        JsonNodeType nodeType = node.getNodeType();
        switch (nodeType) {
            case ARRAY:
                return Type.ARRAY;
            case BOOLEAN:
                return Type.BOOLEAN;
            case NULL:
                return Type.NULL;
            case NUMBER:
                return Type.NUMBER;
            case OBJECT:
                return Type.OBJECT;
            case STRING:
                return Type.STRING;
            default:
                return Type.ANY;
        }
    }

    @Override
    public Boolean asBoolean() {
        return node.asBoolean();
    }

    @Override
    public Integer asInteger() {
        return node.asInt();
    }

    @Override
    public Long asLong() {
        return node.asLong();
    }

    @Override
    public Double asDouble() {
        return node.asDouble();
    }

    @Override
    public String asString() {
        return node.asText();
    }

    @Override
    public int getSize() {
        return node.size();
    }

    @Override
    public JsonNode get(int i) {
        return new JacksonNode(node.get(i));
    }

    @Override
    public Iterator<String> getProperties() {
        return node.fieldNames();
    }

    @Override
    public JsonNode get(String property) {
        return new JacksonNode(node.get(property));
    }

    public com.fasterxml.jackson.databind.JsonNode getNode() {
        return node;
    }
}
