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

package com.ziroom.qa.quality.defende.provider.util.swagger.models;

import javax.xml.bind.annotation.XmlAttribute;

public interface Example {
    String getName();
    void setName(String name);

    @XmlAttribute
    String getNamespace();
    void setNamespace(String namespace);

    @XmlAttribute
    String getPrefix();
    void setPrefix(String prefix);

    Boolean getAttribute();
    void setAttribute(Boolean attribute);

    Boolean getWrapped();
    void setWrapped(Boolean wrapped);

    String getWrappedName();
    void setWrappedName(String name);

    String asString();
    String getTypeName();
}
