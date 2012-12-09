package com.wbem.checker.modules.executors;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.modules.executors.dummy.FakeRequest;
import com.wbem.checker.modules.executors.dummy.FakeWBEMConnection;

public class PingRequestExecutorTest extends ExecutorsTestCase {
    
    public void testRequestTypeAndAuth() throws Exception {
        assertRequestTypeAndAuth("ping", false);
    }
    
    public void testNominalCase() throws Exception {
        executor.validateRequest(new FakeRequest() {
            @Override
            public String getHostname() {
                return "localhost";
            }
        });
        Response response = executor.execute(new FakeWBEMConnection());
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_OK, response.getSeverity());
        assertEquals("Host 'localhost' is reachable through 'ping'", response.getMessage());
    }
    
    public void testUnknownHost() throws Exception {
        executor.validateRequest(new FakeRequest() {
            @Override
            public String getHostname() {
                return "fake.ru";
            }
        });
        Response response = executor.execute(new FakeWBEMConnection());
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_UNKNOWN, response.getSeverity());
        assertEquals("Unknown host: 'fake.ru'", response.getMessage());
    }
    
    public void testIsNotReachable() throws Exception {
        executor.validateRequest(new FakeRequest() {
            @Override
            public String getHostname() {
                return "192.168.56.101";
            }
        });
        Response response = executor.execute(new FakeWBEMConnection());
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_CRITICAL, response.getSeverity());
        assertEquals("Host '192.168.56.101' is not reachable through 'ping'", response.getMessage());
    }

    @Override
    protected RequestExecutor initExecutor() {
        return new PingRequestExecutor();
    }
}
