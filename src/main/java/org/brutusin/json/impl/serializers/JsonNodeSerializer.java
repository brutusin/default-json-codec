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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.brutusin.json.spi.JsonNode;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JsonNodeSerializer extends StdSerializer<JsonNode> {
    
    public JsonNodeSerializer(){
        super(JsonNode.class);
    }
    
    @Override
    public void serialize(JsonNode value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        JsonWriteContext ctx = (JsonWriteContext) gen.getOutputContext();

        int status = ctx.writeValue();
        switch (status) {
            case JsonWriteContext.STATUS_OK_AFTER_COMMA:
                gen.writeRaw(',');
                break;
            case JsonWriteContext.STATUS_OK_AFTER_COLON:
                gen.writeRaw(':');
                break;
            case JsonWriteContext.STATUS_OK_AFTER_SPACE:
                gen.writeRaw(' ');
                break;
        };
        if (value == null) {
            gen.writeRaw("null");
        } else {
            gen.writeRaw(value.toString());
        }
    }
}
