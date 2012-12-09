package com.wbem.checker.modules.executors;

import java.util.HashSet;

import junit.framework.TestCase;

import com.wbem.checker.api.RequestExecutor;

public abstract class ExecutorsTestCase extends TestCase {
    
    protected RequestExecutor executor = initExecutor();
    
    protected abstract RequestExecutor initExecutor();
    
    protected void assertRequestTypeAndAuth(String expectedRequestType, boolean isAuthNeeded, String... params) {
        assertEquals(expectedRequestType, executor.getRequestType());
        assertEquals(isAuthNeeded, executor.isAuthentificateNeeded());
        HashSet<String> expectedParams = new HashSet<String>();
        for (String param : params) {
            expectedParams.add(param);
        }
        for ( String param : executor.getRequiredParameters() ) {
            assertTrue(expectedParams.contains(param));
        }
    }
}
