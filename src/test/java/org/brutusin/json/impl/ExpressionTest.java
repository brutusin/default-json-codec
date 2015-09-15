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

import org.brutusin.json.ParseException;
import org.brutusin.json.spi.JsonCodec;
import org.brutusin.json.spi.JsonNode;
import org.brutusin.json.spi.JsonSchema;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class ExpressionTest extends org.brutusin.json.spi.ExpressionTest {

    @Override
    protected JsonNode getJsonNode() throws ParseException {
        return JsonCodec.getInstance().parse("{\"map\":{\"aa\":{\"booleanMap\":{\"key1\":true},\"s1\":\"s11Value\",\"s2\":[\"s2Value11\",\"s2Value21\"]},\"bb\":{\"booleanMap\":{\"key2\":false},\"s1\":\"s12Value\",\"s2\":[\"s2Value12\",\"s2Value22\"]}}}{\"map\":{\"aa\":{\"booleanMap\":{\"key1\":true},\"s1\":\"s11Value\",\"s2\":[\"s2Value11\",\"s2Value21\"]},\"bb\":{\"booleanMap\":{\"key2\":false},\"s1\":\"s12Value\",\"s2\":[\"s2Value12\",\"s2Value22\"]}}}");
    }

    @Override
    protected JsonSchema getJsonSchema() throws ParseException {
        return JsonCodec.getInstance().parseSchema("{\"$schema\":\"http://json-schema.org/draft-03/schema#\",\"type\":\"object\",\"id\":\"urn:jsonschema:org:brutusin:fleadb:impl:SchemaImpl:TestClass\",\"properties\":{\"map\":{\"type\":\"object\",\"title\":\"map\",\"additionalProperties\":{\"type\":\"object\",\"id\":\"urn:jsonschema:org:brutusin:fleadb:impl:SchemaImpl:Class2\",\"properties\":{\"booleanMap\":{\"type\":\"object\",\"title\":\"booleanMap\",\"additionalProperties\":{\"type\":\"boolean\"},\"index\":\"facet\"},\"s1\":{\"type\":\"string\",\"title\":\"s1\"},\"s2\":{\"type\":\"array\",\"title\":\"s2\",\"items\":{\"type\":\"string\"},\"index\":\"index\"}}}}}}");
    }

    @Override
    protected String[] getExpressions() {
        return new String[]{
            "$",
            "$.map",
            "$.map[*]",
            "$.map[*].booleanMap",
            "$.map[*].booleanMap[*]",
            "$.map[*].s2[#]",
            "$.map[*].s2[0]",
            "$.map[*].s2[$]"};
    }

    @Override
    protected String[] getInvalidExpressions() {
        return new String[]{
            "$.map2"};
    }

}
