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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.jsonSchema.factories.FormatVisitorFactory;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.Visitor;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonFactoryWrapper extends SchemaFactoryWrapper {

    private final WrapperFactory wrapperFactory = new WrapperFactory() {
        @Override
        public SchemaFactoryWrapper getWrapper(SerializerProvider p) {
            return new JacksonFactoryWrapper(p);
        }

        @Override
        public SchemaFactoryWrapper getWrapper(SerializerProvider provider, VisitorContext rvc) {
            JacksonFactoryWrapper wrapper = new JacksonFactoryWrapper(provider);
            wrapper.setVisitorContext(rvc);
            return wrapper;
        }

    };

    public JacksonFactoryWrapper() {
        this(null);
    }

    public JacksonFactoryWrapper(SerializerProvider p) {
        super(p);
        visitorFactory = new FormatVisitorFactory(wrapperFactory);
        schemaProvider = new JacksonSchemaFactory();
        // Disable using references:
        this.visitorContext = new VisitorContext(){
            @Override
            public String getSeenSchemaUri(JavaType aSeenSchema) {
                return null;
            }
        };
    }

    @Override
    public Visitor setVisitorContext(VisitorContext rvc) {
        return this;
    }
    
    
}
