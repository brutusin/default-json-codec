package org.brutusin.json.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.brutusin.json.annotations.IndexableProperty;

/**
 * Workaround to add custom formats. Jackson uses a predefined enumeration
 *
 * @author Ignacio del Valle Alles idelvall@dreamgenics.com
 */
public class StringSchema extends com.fasterxml.jackson.module.jsonSchema.types.StringSchema {

    @JsonIgnore
    private final JacksonSchemaFactory factory;
    @com.fasterxml.jackson.annotation.JsonProperty
    private String[] dependsOn;
    @JsonIgnore
    public JsonValueFormat format;
    @com.fasterxml.jackson.annotation.JsonProperty("format")
    public String stringFormat;
    @com.fasterxml.jackson.annotation.JsonProperty("default")
    private Object def;
    @com.fasterxml.jackson.annotation.JsonProperty("enum")
    private List values;
    @JsonIgnore
    public String id;
    @com.fasterxml.jackson.annotation.JsonProperty
    private IndexableProperty.IndexMode index;

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

    @Override
    public void setEnums(Set<String> enums) {
        super.setEnums(enums);
        if (values == null) {
            setValues(new ArrayList(enums));
        }
    }

    public IndexableProperty.IndexMode getIndex() {
        return index;
    }

    public void setIndex(IndexableProperty.IndexMode index) {
        this.index = index;
    }

    public StringSchema(JacksonSchemaFactory factory) {
        this.factory = factory;
    }

    @Override
    public void enrichWithBeanProperty(BeanProperty beanProperty) {
        factory.enrich(this, beanProperty);
    }

    @JsonIgnore
    @Override
    public String getId() {
        return super.getId();
    }

    @JsonIgnore
    @Override
    public JsonValueFormat getFormat() {
        return super.getFormat();
    }

    public String getStringFormat() {
        return stringFormat;
    }

    public void setStringFormat(String stringFormat) {
        this.stringFormat = stringFormat;
    }
}
