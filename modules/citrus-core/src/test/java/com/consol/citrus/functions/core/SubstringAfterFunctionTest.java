/*
 * Copyright 2006-2010 the original author or authors.
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

package com.consol.citrus.functions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.exceptions.InvalidFunctionUsageException;
import com.consol.citrus.functions.core.SubstringAfterFunction;
import com.consol.citrus.testng.AbstractTestNGUnitTest;

/**
 * @author Christoph Deppisch
 */
public class SubstringAfterFunctionTest extends AbstractTestNGUnitTest {
    SubstringAfterFunction function = new SubstringAfterFunction();
    
    @Test
    public void testFunction() {
        List<String> params = new ArrayList<String>();
        params.add("Hallo,TestFramework");
        params.add(",");
        Assert.assertEquals(function.execute(params), "TestFramework");
        
        params.clear();
        params.add("This is a test");
        params.add("a");
        Assert.assertEquals(function.execute(params), " test");
    }
    
    @Test(expectedExceptions = {InvalidFunctionUsageException.class})
    public void testNoParameters() {
        function.execute(Collections.<String>emptyList());
    }
}
