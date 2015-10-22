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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.module.jsonSchema.factories.FormatVisitorFactory;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;
import java.util.Map;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonFactoryWrapper extends SchemaFactoryWrapper {

    private final Map<Class, String> formatMap;

    private final WrapperFactory wrapperFactory = new WrapperFactory() {
        @Override
        public SchemaFactoryWrapper getWrapper(SerializerProvider p) {
            return new JacksonFactoryWrapper(p, formatMap);
        }

        @Override
        public SchemaFactoryWrapper getWrapper(SerializerProvider provider, VisitorContext rvc) {
            JacksonFactoryWrapper wrapper = new JacksonFactoryWrapper(provider, formatMap);
            wrapper.setVisitorContext(rvc);
            return wrapper;
        }

    };

    public JacksonFactoryWrapper() {
        this(null, null);
    }
    
    public JacksonFactoryWrapper(Map<Class, String> formatMap) {
        this(null, formatMap);
    }

    public JacksonFactoryWrapper(SerializerProvider p) {
        this(p, null);
    }

    public JacksonFactoryWrapper(SerializerProvider p, Map<Class, String> formatMap) {
        this.provider = p;
        this.visitorFactory = new FormatVisitorFactory(wrapperFactory);
        this.schemaProvider = new JacksonSchemaFactory();
        this.formatMap = formatMap;
        // Disable using references:
        this.visitorContext = new VisitorContext() {
            @Override
            public String getSeenSchemaUri(JavaType aSeenSchema) {
                return null;
            }
        };
    }

    private String getFormat(Class clazz) {
        if (formatMap == null || clazz == null) {
            return null;
        }
        String format = formatMap.get(clazz);
        if (format != null) {
            return format;
        }
        return getFormat(clazz.getSuperclass());
    }

    @Override
    public JsonAnyFormatVisitor expectAnyFormat(JavaType convertedType) {
        String format = getFormat(convertedType.getRawClass());
        if (format != null) {
            StringSchema s = (StringSchema) schemaProvider.stringSchema();
            s.setStringFormat(format);
            schema = s;
            return null;
        }
        return super.expectAnyFormat(convertedType);
    }

    @Override
    public JsonStringFormatVisitor expectStringFormat(JavaType convertedType) {
        StringSchema s = (StringSchema) schemaProvider.stringSchema();
        schema = s;
        String format = getFormat(convertedType.getRawClass());
        if (format != null) {
            s.setStringFormat(format);
        }
        return visitorFactory.stringFormatVisitor(s);
    }

    @Override
    public SchemaFactoryWrapper setVisitorContext(VisitorContext rvc) {
        return this;
    }
}
