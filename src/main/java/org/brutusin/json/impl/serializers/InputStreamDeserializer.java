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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.brutusin.json.ParseException;
import org.brutusin.json.spi.JsonCodec;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class InputStreamDeserializer extends StdDeserializer<InputStream> {

    public InputStreamDeserializer() {
        super(InputStream.class);
    }

    @Override
    public InputStream deserialize(JsonParser jp, com.fasterxml.jackson.databind.DeserializationContext dc) throws IOException, JsonProcessingException {
        SerializationContext ctx = SerializationContext.getCurrentContext();
        if (ctx == null) {
            return null;
        }
        TreeNode tree = jp.getCodec().readTree(jp);
        if (tree != null) {
            try {
                return (InputStream) ctx.getMap().get(JsonCodec.getInstance().parse(tree.toString(), String.class));
            } catch (ParseException ex) {
               throw new RuntimeException(ex);
            }
        }
        return null;
    }
}
