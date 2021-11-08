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

import java.math.BigInteger;

public class BigIntegerExample extends AbstractExample {
    private BigInteger value;

    public BigIntegerExample() {
        super.setTypeName("biginteger");
    }

    public BigIntegerExample(BigInteger value) {
        this();
        this.value = value;
    }

    public String asString() {
        return value.toString();
    }

    public BigInteger getValue() {
        return value != null ? value : new BigInteger("1180591620717411303424");
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }
}
