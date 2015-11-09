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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.module.jsonSchema.types.AnySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema;
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NullSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.SimpleTypeSchema;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.brutusin.json.annotations.DependentProperty;
import org.brutusin.json.annotations.IndexableProperty;
import org.brutusin.json.annotations.JsonProperty;
import org.brutusin.json.spi.JsonCodec;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonSchemaFactory extends com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory {

    void enrich(SimpleTypeSchema schema, BeanProperty beanProperty) {
        JsonProperty jsonAnnot = beanProperty.getAnnotation(JsonProperty.class);
        IndexableProperty indexAnnot = beanProperty.getAnnotation(IndexableProperty.class);
        DependentProperty dependsAnnot = beanProperty.getAnnotation(DependentProperty.class);

        if (schema instanceof StringSchema) {
            StringSchema sschema = (StringSchema) schema;
            try {
                Set<String> enums = sschema.getEnums();
                if (enums != null) {
                    Method method = schema.getClass().getMethod("setValues", List.class);
                    method.invoke(schema, new ArrayList(enums));
                }
            } catch (Exception parseException) {
                throw new Error("Error setting enum value from enumeration for " + beanProperty.getFullName(), parseException);
            }
        }

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
            String valuesMethodName = jsonAnnot.valuesMethod();
            if (valuesMethodName != null && !valuesMethodName.isEmpty()) {
                try {
                    Method valuesMethod = beanProperty.getMember().getDeclaringClass().getMethod(valuesMethodName, null);
                    valuesMethod.setAccessible(true);
                    Object valuesValue = valuesMethod.invoke(null, null);
                    Method method = schema.getClass().getMethod("setValues", List.class);
                    method.invoke(schema, valuesValue);
                } catch (Exception ex) {
                    throw new Error("Error setting enum value from @JsonProperty.valuesMethod() for " + beanProperty.getFullName(), ex);
                }
            } else {
                String values = jsonAnnot.values();
                if (values != null && !values.isEmpty()) {
                    try {
                        Object valuesValue = JsonCodec.getInstance().parse(values, List.class);
                        Method method = schema.getClass().getMethod("setValues", List.class);
                        method.invoke(schema, valuesValue);
                    } catch (Exception parseException) {
                        throw new Error("Error setting enum value from @JsonProperty.values() for " + beanProperty.getFullName(), parseException);
                    }
                }
            }
        }
        if (indexAnnot != null) {
            try {
                Method method = schema.getClass().getMethod("setIndex", IndexableProperty.IndexMode.class);
                method.invoke(schema, indexAnnot.mode());
            } catch (Exception parseException) {
                throw new Error("Error setting index value for " + beanProperty.getFullName(), parseException);
            }
        }
        if (dependsAnnot != null) {
            try {
                Method method = schema.getClass().getMethod("setDependsOn", String[].class);
                method.invoke(schema, (Object) dependsAnnot.dependsOn());
            } catch (Exception parseException) {
                throw new Error("Error setting dependsOn value for " + beanProperty.getFullName(), parseException);
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
            @com.fasterxml.jackson.annotation.JsonProperty
            private String[] dependsOn;
            @JsonIgnore
            public String id;

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

            public String[] getDependsOn() {
                return dependsOn;
            }

            public void setDependsOn(String[] dependsOn) {
                this.dependsOn = dependsOn;
            }

            @JsonIgnore
            @Override
            public String getId() {
                return super.getId();
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
            @com.fasterxml.jackson.annotation.JsonProperty
            private String[] dependsOn;
            @JsonIgnore
            public String id;

            @JsonIgnore
            @Override
            public String getId() {
                return super.getId();
            }

            public String[] getDependsOn() {
                return dependsOn;
            }

            public void setDependsOn(String[] dependsOn) {
                this.dependsOn = dependsOn;
            }

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
            @com.fasterxml.jackson.annotation.JsonProperty
            private String[] dependsOn;
            @JsonIgnore
            public String id;

            @JsonIgnore
            @Override
            public String getId() {
                return super.getId();
            }

            public String[] getDependsOn() {
                return dependsOn;
            }

            public void setDependsOn(String[] dependsOn) {
                this.dependsOn = dependsOn;
            }

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
            @com.fasterxml.jackson.annotation.JsonProperty
            private String[] dependsOn;
            @JsonIgnore
            public String id;

            @JsonIgnore
            @Override
            public String getId() {
                return super.getId();
            }

            public String[] getDependsOn() {
                return dependsOn;
            }

            public void setDependsOn(String[] dependsOn) {
                this.dependsOn = dependsOn;
            }

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
            @com.fasterxml.jackson.annotation.JsonProperty
            private String[] dependsOn;
            @JsonIgnore
            public String id;

            @JsonIgnore
            @Override
            public String getId() {
                return super.getId();
            }

            public String[] getDependsOn() {
                return dependsOn;
            }

            public void setDependsOn(String[] dependsOn) {
                this.dependsOn = dependsOn;
            }

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
            @com.fasterxml.jackson.annotation.JsonProperty
            private String[] dependsOn;

            @JsonIgnore
            public String id;

            @JsonIgnore
            @Override
            public String getId() {
                return super.getId();
            }

            public String[] getDependsOn() {
                return dependsOn;
            }

            public void setDependsOn(String[] dependsOn) {
                this.dependsOn = dependsOn;
            }

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
            @com.fasterxml.jackson.annotation.JsonProperty
            private String[] dependsOn;

            @JsonIgnore
            public String id;

            @JsonIgnore
            @Override
            public String getId() {
                return super.getId();
            }

            public String[] getDependsOn() {
                return dependsOn;
            }

            public void setDependsOn(String[] dependsOn) {
                this.dependsOn = dependsOn;
            }

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
    public com.fasterxml.jackson.module.jsonSchema.types.StringSchema stringSchema() {
        return new StringSchema(this);
    }
}
