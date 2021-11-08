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

import com.fasterxml.jackson.databind.JavaType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BinaryProcessor implements EntityProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryProcessor.class);
    private static List<MediaType> SUPPORTED_TYPES = new ArrayList<>();

    static {
        SUPPORTED_TYPES.add(MediaType.APPLICATION_OCTET_STREAM_TYPE);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return new ArrayList(SUPPORTED_TYPES);
    }

    @Override
    public void enableType(MediaType type) {
        MediaType t = type;
        if(t != null) {
            if(!SUPPORTED_TYPES.contains(t)) {
                SUPPORTED_TYPES.add(type);
            }
        }
    }

    @Override
    public boolean supports(MediaType mediaType) {
        return SUPPORTED_TYPES.contains(mediaType);
    }

    @Override
    public Object process(MediaType mediaType, InputStream entityStream, Class<?> cls) throws ConversionException {
        try {
            return IOUtils.toByteArray(entityStream);
        } catch (IOException e) {
            LOGGER.trace("unable to extract entity from content-type `" + mediaType + "` to byte[]", e);
            throw new ConversionException()
                    .message(new ValidationMessage()
                            .code(ValidationError.UNACCEPTABLE_VALUE)
                            .message("unable to convert input to " + cls.getCanonicalName()));
        }
    }

    @Override
    public Object process(MediaType mediaType, InputStream entityStream, JavaType javaType) {
        try {
            return IOUtils.toByteArray(entityStream);
        } catch (IOException e) {
            LOGGER.error("unable to extract entity from content-type `" + mediaType + "` to byte[]", e);
        }
        return null;
    }
}
