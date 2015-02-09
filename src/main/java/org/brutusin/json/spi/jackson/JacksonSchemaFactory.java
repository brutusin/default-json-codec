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

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.module.jsonSchema.types.AnySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema;
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NullSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.SimpleTypeSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import java.lang.reflect.Method;
import java.util.List;
import org.brutusin.commons.json.annotations.IndexableProperty;
import org.brutusin.commons.json.annotations.JsonProperty;
import org.brutusin.commons.json.spi.JsonCodec;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonSchemaFactory extends com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory {

    void enrich(SimpleTypeSchema schema, BeanProperty beanProperty) {
        JsonProperty jsonAnnot = beanProperty.getAnnotation(JsonProperty.class);
        IndexableProperty indexAnnot = beanProperty.getAnnotation(IndexableProperty.class);

        if (jsonAnnot == null) {
            schema.setTitle(beanProperty.getName());
        } else {
            if (jsonAnnot.title() != null) {
                schema.setTitle(jsonAnnot.title());
            } else {
                schema.setTitle(beanProperty.getName());
            }
            schema.setDescription(jsonAnnot.description());
            schema.setRequired(jsonAnnot.required());
            String def = jsonAnnot.defaultJsonExp();
            if (def != null) {
                try {
                    Object defaultValue = JsonCodec.getInstance().parse(def, beanProperty.getType().getRawClass());
                    Method method = schema.getClass().getMethod("setDef", Object.class);
                    method.invoke(schema, defaultValue);
                } catch (Exception parseException) {
                    throw new Error("Error setting default value for " + beanProperty.getFullName(), parseException);
                }
            }
            String values = jsonAnnot.values();
            if (values != null) {
                try {
                    Object valuesValue = JsonCodec.getInstance().parse(values, List.class);
                    Method method = schema.getClass().getMethod("setValues", List.class);
                    method.invoke(schema, valuesValue);
                } catch (Exception parseException) {
                    throw new Error("Error setting enum value for " + beanProperty.getFullName(), parseException);
                }
            }
        }
        if (indexAnnot != null) {
            try {
                Method method = schema.getClass().getMethod("setIndex", IndexableProperty.IndexMode.class);
                method.invoke(schema, indexAnnot.mode());
            } catch (Exception parseException) {
                throw new Error("Error setting enum value for " + beanProperty.getFullName(), parseException);
            }
        }
    }

    @Override
    public AnySchema anySchema() {
        return new AnySchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }

    @Override
    public ArraySchema arraySchema() {
        return new ArraySchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }

    @Override
    public BooleanSchema booleanSchema() {
        return new BooleanSchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }

    @Override
    public IntegerSchema integerSchema() {
        return new IntegerSchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }

    @Override
    public NullSchema nullSchema() {
        return new NullSchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }

    @Override
    public NumberSchema numberSchema() {
        return new NumberSchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }

    @Override
    public ObjectSchema objectSchema() {
        return new ObjectSchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }

    @Override
    public StringSchema stringSchema() {
        return new StringSchema() {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Object def;
            @com.fasterxml.jackson.annotation.JsonProperty("enum")
            private List values;
            @com.fasterxml.jackson.annotation.JsonProperty
            private IndexableProperty.IndexMode index;

            public Object getDef() {
                return def;
            }

            public void setDef(Object def) {
                this.def = def;
            }

            public List getValues() {
                return values;
            }

            public void setValues(List values) {
                this.values = values;
            }

            public IndexableProperty.IndexMode getIndex() {
                return index;
            }

            public void setIndex(IndexableProperty.IndexMode index) {
                this.index = index;
            }

            @Override
            public void enrichWithBeanProperty(BeanProperty beanProperty) {
                enrich(this, beanProperty);
            }
        };
    }
}
