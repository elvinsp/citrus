/*
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.validation.json;

import com.consol.citrus.message.*;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class JsonPathMessageConstructionInterceptorTest extends AbstractTestNGUnitTest {
    
    @Test
    public void testConstructWithJsonPath() {
        Message message = new DefaultMessage("{ \"TestMessage\": { \"Text\": \"Hello World!\" }}");
        
        Map<String, Map<String, String>> jsonPathExpressions = new HashMap<>();
        Map<String, String> valueInfos = new HashMap<>();
        valueInfos.put("value", "Hello!");
        jsonPathExpressions.put("$.TestMessage.Text", valueInfos);
        
        JsonPathMessageConstructionInterceptor interceptor = new JsonPathMessageConstructionInterceptor(jsonPathExpressions);
        Message intercepted = interceptor.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\"}}");
    }

    @Test
    public void testConstructWithJsonPathMultipleValues() {
        Message message = new DefaultMessage("{ \"TestMessage\": { \"Text\": \"Hello World!\", \"Id\": 1234567}}");

        Map<String, Map<String, String>> jsonPathExpressions = new HashMap<>();
        Map<String, String> valueInfos = new HashMap<>();
        valueInfos.put("value", "Hello!");
        jsonPathExpressions.put("$.TestMessage.Text", valueInfos);
        valueInfos.put("value", "9999999");
        jsonPathExpressions.put("$.TestMessage.Id", valueInfos);
        valueInfos.put("value", "8888888");
        valueInfos.put("type", "String");
        jsonPathExpressions.put("$.TestMessage.Id2", valueInfos);

        JsonPathMessageConstructionInterceptor interceptor = new JsonPathMessageConstructionInterceptor(jsonPathExpressions);
        Message intercepted = interceptor.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\",\"Id\":9999999,\"Id2\":\"8888888\"}}");
    }

    @Test
    public void testConstructWithJsonPathWithArrays() {
        Message message = new DefaultMessage("{ \"TestMessage\": [{ \"Text\": \"Hello World!\" }, { \"Text\": \"Another Hello World!\" }]}");

        Map<String, Map<String, String>> jsonPathExpressions = new HashMap<>();
        Map<String, String> valueInfos = new HashMap<>();
        valueInfos.put("value", "Hello!");
        jsonPathExpressions.put("$..Text", valueInfos);

        JsonPathMessageConstructionInterceptor interceptor = new JsonPathMessageConstructionInterceptor(jsonPathExpressions);
        Message intercepted = interceptor.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":[{\"Text\":\"Hello!\"},{\"Text\":\"Hello!\"}]}");
    }

    @Test
    public void testConstructWithJsonPathNoResult() {
        Message message = new DefaultMessage("{ \"TestMessage\": { \"Text\": \"Hello World!\" }}");

        Map<String, Map<String, String>> jsonPathExpressions = new HashMap<>();
        Map<String, String> valueInfos = new HashMap<>();
        valueInfos.put("value", "Hello!");
        jsonPathExpressions.put("$.TestMessage.Unknown", valueInfos);

        JsonPathMessageConstructionInterceptor interceptor = new JsonPathMessageConstructionInterceptor(jsonPathExpressions);
        Message intercepted = interceptor.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello World!\"}}");
    }
}
