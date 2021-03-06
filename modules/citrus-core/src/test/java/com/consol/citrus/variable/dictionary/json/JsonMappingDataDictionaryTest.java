/*
 * Copyright 2006-2013 the original author or authors.
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

package com.consol.citrus.variable.dictionary.json;

import com.consol.citrus.message.*;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import com.consol.citrus.variable.dictionary.DataDictionary;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 * @since 1.4
 */
public class JsonMappingDataDictionaryTest extends AbstractTestNGUnitTest {
    @Test
    public void testTranslateExactMatchStrategy() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":\"Hello World!\",\"OtherText\":\"No changes\", \"OtherNumber\": 10}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value = new HashMap<>();
        value.put("value", "NotFound");
        mappings.put("Something.Else", value);
        value.put("value", "Hello!");
        mappings.put("TestMessage.Text", value);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\",\"OtherText\":\"No changes\",\"OtherNumber\":10}}");
    }

    @Test
    public void testTranslateStartsWithStrategy() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":\"Hello World!\",\"OtherText\":\"No changes\"}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> valueText = new HashMap<>();
        Map<String, String> valueOther = new HashMap<>();
        valueText.put("value", "Hello!");
        mappings.put("TestMessage.Text", valueText);
        valueOther.put("value", "Bye!");
        mappings.put("TestMessage.Other", valueOther);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);
        dictionary.setPathMappingStrategy(DataDictionary.PathMappingStrategy.STARTS_WITH);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\",\"OtherText\":\"Bye!\"}}");
    }

    @Test
    public void testTranslateEndsWithStrategy() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":\"Hello World!\",\"OtherText\":\"No changes\"}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value = new HashMap<>();
        value.put("value", "Hello!");
        mappings.put("Text", value);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);
        dictionary.setPathMappingStrategy(DataDictionary.PathMappingStrategy.ENDS_WITH);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\",\"OtherText\":\"Hello!\"}}");
    }

    @Test
    public void testTranslateWithVariables() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":\"Hello World!\",\"OtherText\":\"No changes\"}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value = new HashMap<>();
        value.put("value", "${helloText}");
        mappings.put("TestMessage.Text", value);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);

        context.setVariable("helloText", "Hello!");

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\",\"OtherText\":\"No changes\"}}");
    }

    @Test
    public void testTranslateWithArrays() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":[\"Hello World!\",\"Hello Galaxy!\"],\"OtherText\":\"No changes\"}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value0 = new HashMap<>();
        Map<String, String> value1 = new HashMap<>();
        value0.put("value", "Hello!");
        mappings.put("TestMessage.Text[0]", value0);
        value1.put("value", "Hello Universe!");
        mappings.put("TestMessage.Text[1]", value1);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":[\"Hello!\",\"Hello Universe!\"],\"OtherText\":\"No changes\"}}");
    }

    @Test
    public void testTranslateWithArraysAndObjects() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Greetings\":[{\"Text\":\"Hello World!\"},{\"Text\":\"Hello Galaxy!\"}],\"OtherText\":\"No changes\"}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value0 = new HashMap<>();
        Map<String, String> value1 = new HashMap<>();
        value0.put("value", "Hello!");
        mappings.put("TestMessage.Greetings[0].Text", value0);
        value1.put("value", "Hello Universe!");
        mappings.put("TestMessage.Greetings[1].Text", value1);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Greetings\":[{\"Text\":\"Hello!\"},{\"Text\":\"Hello Universe!\"}],\"OtherText\":\"No changes\"}}");
    }

    @Test
    public void testTranslateFromMappingFile() throws Exception {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":\"Hello World!\",\"OtherText\":\"No changes\"}}");

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappingFile(new ClassPathResource("jsonmapping.properties", DataDictionary.class));
        dictionary.afterPropertiesSet();

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\",\"OtherText\":\"No changes\"}}");
    }

    @Test
    public void testTranslateWithNullValues() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":null,\"OtherText\":null}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value = new HashMap<>();
        value.put("value", "Hello!");
        mappings.put("TestMessage.Text", value);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello!\",\"OtherText\":null}}");
    }

    @Test
    public void testTranslateWithNumberValues() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Number\":0,\"OtherNumber\":100}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value = new HashMap<>();
        value.put("value", "99");
        mappings.put("TestMessage.Number", value);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Number\":99,\"OtherNumber\":100}}");
    }

    @Test
    public void testTranslateNoResult() {
        Message message = new DefaultMessage("{\"TestMessage\":{\"Text\":\"Hello World!\",\"OtherText\":\"No changes\"}}");

        Map<String, Map<String, String>> mappings = new HashMap<>();
        Map<String, String> value = new HashMap<>();
        value.put("value", "NotFound");
        mappings.put("Something.Else", value);

        JsonMappingDataDictionary dictionary = new JsonMappingDataDictionary();
        dictionary.setMappings(mappings);

        Message intercepted = dictionary.interceptMessage(message, MessageType.JSON.toString(), context);
        Assert.assertEquals(intercepted.getPayload(String.class), "{\"TestMessage\":{\"Text\":\"Hello World!\",\"OtherText\":\"No changes\"}}");
    }
}
