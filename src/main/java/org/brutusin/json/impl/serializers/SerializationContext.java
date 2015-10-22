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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class SerializationContext {

    private static final ThreadLocal<SerializationContext> instances = new ThreadLocal<SerializationContext>() {
        @Override
        protected SerializationContext initialValue() {
            return null;
        }
    };

    private final Map<InputStream, String> inverseMap;
    private final Map<String, InputStream> map;
    private int counter;

    public SerializationContext() {
        this(null);
    }

    public SerializationContext(Map<String, InputStream> map) {
        this.inverseMap = new HashMap();
        if (map == null) {
            this.map = new HashMap();
        } else {
            this.map = map;
            for (Map.Entry<String, InputStream> entrySet : map.entrySet()) {
                String key = entrySet.getKey();
                InputStream value = entrySet.getValue();
                this.inverseMap.put(value, key);
            }
        }
        this.counter = 1;
    }

    public static void setCurrentContext(SerializationContext ctx) {
        instances.set(ctx);
    }

    public static SerializationContext getCurrentContext() {
        return instances.get();
    }

    public static void closeCurrentContext() {
        instances.remove();
    }

    public String addStream(InputStream stream) {
        String str = inverseMap.get(stream);
        if (str == null) {
            str = "#" + (counter++) + "#" + System.identityHashCode(stream);
            inverseMap.put(stream, str);
            map.put(str, stream);
        }
        return str;
    }

    public Map<String, InputStream> getMap() {
        return map;
    }

    public void clear() {
        inverseMap.clear();
        map.clear();
    }

}
