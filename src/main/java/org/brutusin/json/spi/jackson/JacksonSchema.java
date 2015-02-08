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

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.brutusin.commons.json.spi.JsonNode;
import org.brutusin.commons.json.spi.JsonSchema;
import org.brutusin.commons.json.ValidationException;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonSchema implements JsonSchema {

    private com.github.fge.jsonschema.main.JsonSchema impl;

    public com.github.fge.jsonschema.main.JsonSchema getImpl() {
        return impl;
    }

    public void setImpl(com.github.fge.jsonschema.main.JsonSchema impl) {
        this.impl = impl;
    }

    public void validate(JsonNode node) throws ValidationException {
        if (!(node instanceof JacksonNode)) {
            throw new IllegalArgumentException("node is not an intance of " + JacksonNode.class.getSimpleName());
        }
        JacksonNode nodeImpl = (JacksonNode) node;
        ProcessingReport report = null;
        try {
            report = impl.validate(nodeImpl.getNode());
        } catch (ProcessingException ex) {
            throw new RuntimeException(ex);
        }
        if (!report.isSuccess()) {
            Iterator<ProcessingMessage> iterator = report.iterator();
            List<String> messages = new ArrayList();
            while (iterator.hasNext()) {
                ProcessingMessage processingMessage = iterator.next();
                messages.add(processingMessage.getMessage());
            }
            throw new ValidationException(messages);
        }
    }
}
