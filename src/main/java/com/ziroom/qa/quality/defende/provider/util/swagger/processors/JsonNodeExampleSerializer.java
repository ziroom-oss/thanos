/*
 *  Copyright 2017 SmartBear Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ziroom.qa.quality.defende.provider.util.swagger.processors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ziroom.qa.quality.defende.provider.util.swagger.models.*;

import java.io.IOException;
import java.util.List;

public class JsonNodeExampleSerializer extends JsonSerializer<Example> {

    @Override
    public void serialize(Example value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException, JsonProcessingException {

        if (value instanceof ObjectExample) {
            ObjectExample obj = (ObjectExample) value;
            jgen.writeStartObject();
            writeTo(jgen, obj);
            jgen.writeEndObject();
        } else if (value instanceof ArrayExample) {
            ArrayExample obj = (ArrayExample) value;
            jgen.writeStartArray();
            for (Example item : obj.getItems()) {
                if(item instanceof ObjectExample) {
                    jgen.writeStartObject();
                }
                writeTo(jgen, item);
                if(item instanceof ObjectExample) {
                    jgen.writeEndObject();
                }
            }
            jgen.writeEndArray();
        } else {
            writeTo(jgen, value);
        }
    }

    public void writeTo(JsonGenerator jgen, Example o) throws IOException {
        if (o instanceof ObjectExample) {
            ObjectExample obj = (ObjectExample) o;
            for (String key : obj.keySet()) {
                Example value = (Example) obj.get(key);
                writeValue(jgen, key, value);
            }
        } else if (o instanceof ArrayExample) {
            jgen.writeStartArray();
            List<Example> items = ((ArrayExample) o).getItems();
            if( !items.isEmpty()){
                serialize( items.get(0), jgen, null );
            }
            jgen.writeEndArray();
        } else {
            writeValue(jgen, null, o);
        }
    }

    public void writeValue(JsonGenerator jgen, String field, Example o) throws IOException {
        if (o instanceof ArrayExample) {
            ArrayExample obj = (ArrayExample) o;
            jgen.writeArrayFieldStart(field);
            for (Example item : obj.getItems()) {
                if (item.getName() != null || item instanceof ObjectExample) {
                    jgen.writeStartObject();
                    writeTo(jgen, item);
                    jgen.writeEndObject();
                } else {
                    writeTo(jgen, item);
                }
            }
            jgen.writeEndArray();
        } else if (o instanceof BooleanExample) {
            BooleanExample obj = (BooleanExample) o;
            if (field != null) {
                jgen.writeBooleanField(field, obj.getValue());
            } else {
                jgen.writeBoolean(obj.getValue());
            }
        } else if (o instanceof DecimalExample) {
            DecimalExample obj = (DecimalExample) o;
            if (field != null) {
                jgen.writeNumberField(field, obj.getValue());
            } else {
                jgen.writeNumber(obj.getValue());
            }
        }  else if (o instanceof BigIntegerExample) {
            BigIntegerExample obj = (BigIntegerExample) o;
            if (field != null) {
                jgen.writeFieldName(field);
                jgen.writeNumber(obj.getValue());
            } else {
                jgen.writeNumber(obj.getValue());
            }
        } else if (o instanceof DoubleExample) {
            DoubleExample obj = (DoubleExample) o;
            if (field != null) {
                jgen.writeNumberField(field, obj.getValue());
            } else {
                jgen.writeNumber(obj.getValue());
            }
        } else if (o instanceof FloatExample) {
            FloatExample obj = (FloatExample) o;
            if (field != null) {
                jgen.writeNumberField(field, obj.getValue());
            } else {
                jgen.writeNumber(obj.getValue());
            }
        } else if (o instanceof IntegerExample) {
            IntegerExample obj = (IntegerExample) o;
            if (field != null) {
                jgen.writeNumberField(field, obj.getValue());
            } else {
                jgen.writeNumber(obj.getValue());
            }
        } else if (o instanceof LongExample) {
            LongExample obj = (LongExample) o;
            if (field != null) {
                jgen.writeNumberField(field, obj.getValue());
            } else {
                jgen.writeNumber(obj.getValue());
            }
        } else if (o instanceof ObjectExample) {
            ObjectExample obj = (ObjectExample) o;
            if (field != null) {
                jgen.writeObjectField(field, obj);
            }
        } else if (o instanceof StringExample) {
            StringExample obj = (StringExample) o;
            if (field != null) {
                jgen.writeStringField(field, obj.getValue());
            } else {
                jgen.writeString(obj.getValue());
            }
        }
    }

    @Override
    public Class<Example> handledType() {
        return Example.class;
    }
}