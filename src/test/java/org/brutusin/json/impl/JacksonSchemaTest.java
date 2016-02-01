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

import java.util.Map;
import org.brutusin.commons.Bean;
import static org.junit.Assert.*;
import org.brutusin.json.spi.JsonCodec;
import org.brutusin.json.spi.JsonNode;
import org.brutusin.json.spi.JsonSchema;
import org.brutusin.json.spi.SchemaCodecTest;
import org.junit.Test;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonSchemaTest extends SchemaCodecTest {

    @Test
    public void testIssue1() throws Exception {
        int initialThreadNumber = Thread.getAllStackTraces().keySet().size();
        JsonSchema schema = JsonCodec.getInstance().getSchema(String.class);
        JsonNode node = JsonCodec.getInstance().parse("\"a\"");
        schema.validate(node);
        assertEquals(Thread.getAllStackTraces().keySet().size(), initialThreadNumber);
    }

    @Test
    public void testIssue2() throws Throwable {
        int numThreads = 1000;
        Thread[] ts = new Thread[numThreads];
        final Bean<Throwable> thBean = new Bean<Throwable>();
        for (int i = 0; i < 100; i++) {
            ts[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        JsonCodec.getInstance().getSchema(TestClass.class);
                    } catch (Throwable th) {
                        thBean.setValue(th);
                    }
                }
            };
            ts[i].start();
        }
        for (int i = 0; i < 100; i++) {
            ts[i].join();
        }
        if (thBean.getValue() != null) {
            throw thBean.getValue();
        }
    }

    public static class TestClass {

        private Map<String, String> properties;

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }

}
