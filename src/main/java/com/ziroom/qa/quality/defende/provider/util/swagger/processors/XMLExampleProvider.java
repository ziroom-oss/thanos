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


import com.ziroom.qa.quality.defende.provider.util.swagger.XmlExampleSerializer;
import com.ziroom.qa.quality.defende.provider.util.swagger.models.Example;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

@Provider
@Produces({MediaType.APPLICATION_XML})
public class XMLExampleProvider extends AbstractExampleProvider implements MessageBodyWriter<Example> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        List<EntityProcessor> processors = EntityProcessorFactory.getProcessors();
        for(EntityProcessor p : processors) {
            if (p.supports(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeTo(Example data,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> headers,
                        OutputStream out) throws IOException {
        if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            out.write(new XmlExampleSerializer().serialize(data).getBytes("utf-8"));
        }
    }
}

