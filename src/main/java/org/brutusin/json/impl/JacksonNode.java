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
package org.brutusin.json.impl;

import com.fasterxml.jackson.databind.node.JsonNodeType;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import org.brutusin.json.spi.JsonNode;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonNode implements JsonNode {

    private final com.fasterxml.jackson.databind.JsonNode node;
    private final Map<String, InputStream> streams;
    private final JacksonNode parentNode;

    public JacksonNode(com.fasterxml.jackson.databind.JsonNode node) {
        this(node, (JacksonNode) null);
    }

    public JacksonNode(com.fasterxml.jackson.databind.JsonNode node, Map<String, InputStream> streams) {
        if (node == null) {
            throw new IllegalArgumentException("node can not be null");
        }
        this.node = node;
        this.streams = streams;
        this.parentNode = null;
    }

    public JacksonNode(com.fasterxml.jackson.databind.JsonNode node, JacksonNode parentNode) {
        if (node == null) {
            throw new IllegalArgumentException("node can not be null");
        }
        this.node = node;
        this.parentNode = parentNode;
        this.streams = parentNode == null ? null : parentNode.getStreams();
    }

    public final Map<String, InputStream> getStreams() {
        return streams;
    }

    @Override
    public JacksonNode getParentNode() {
        return parentNode;
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
    public InputStream asStream() {
        if (getNodeType() != Type.STRING) {
            throw new UnsupportedOperationException("Node is of type " + getNodeType());
        }
        String str = asString();
        if (streams == null) {
            return null;
        }
        InputStream stream = streams.get(str);
        return stream;
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
    public JacksonNode get(int i) {
        com.fasterxml.jackson.databind.JsonNode nodeImpl = node.get(i);
        if (nodeImpl == null) {
            return null;
        }
        return new JacksonNode(nodeImpl, this);
    }

    @Override
    public Iterator<String> getProperties() {
        return node.fieldNames();
    }

    @Override
    public JacksonNode get(String property) {
        com.fasterxml.jackson.databind.JsonNode nodeImpl = node.get(property);
        if (nodeImpl == null) {
            return null;
        }
        return new JacksonNode(nodeImpl, this);
    }

    @Override
    public String toString() {
        return this.node.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JacksonNode)) {
            return false;
        }
        return this.node.equals(((JacksonNode) obj).getNode());
    }

    @Override
    public int hashCode() {
        return this.node.hashCode();
    }

    public com.fasterxml.jackson.databind.JsonNode getNode() {
        return node;
    }
}
