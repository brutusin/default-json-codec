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
package org.brutusin.json.impl.serializers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.brutusin.json.ParseException;
import org.brutusin.json.spi.JsonCodec;
import org.brutusin.json.spi.JsonNode;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JsonNodeDeserializer extends StdDeserializer<JsonNode> {

    public JsonNodeDeserializer() {
        super(JsonNode.class);
    }

    @Override
    public JsonNode deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        try {
            TreeNode tree = jp.getCodec().readTree(jp);
            return JsonCodec.getInstance().parse(tree.toString(), SerializationContext.getCurrentContext() != null ? SerializationContext.getCurrentContext().getMap() : null);
        } catch (ParseException ex) {
            throw new JsonParseException(ex.getMessage(), null);
        }
    }
}
