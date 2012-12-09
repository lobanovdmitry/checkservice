package com.wbem.checker.modules.executors;

import javax.wbem.WBEMException;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.modules.executors.dummy.FakeRequest;
import com.wbem.checker.modules.executors.dummy.FakeWBEMConnection;

public class ProcessAliveRequestExecutorTest extends ExecutorsTestCase {

    public void testRequestTypeAndAuth() throws Exception {
        assertRequestTypeAndAuth("is_process_alive", true, "-n");
    }
    
    public void testNominalCase() throws Exception {
        executor.validateRequest(new FakeRequest() {
            @Override
            public String getParameterValue(String parameterName) {
                return "-n".equals(parameterName) ? "sshd" : null;
            }
        });
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public boolean isInstanceExist(String path, String property, Object value) throws WBEMException {
                return true;
            }
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_OK, response.getSeverity());
        assertEquals("Process 'sshd' is alive", response.getMessage());
    }
    
    public void testProcessIsNotAlive() throws Exception {
        executor.validateRequest(new FakeRequest() {
            @Override
            public String getParameterValue(String parameterName) {
                return "-n".equals(parameterName) ? "sshd" : null;
            }
        });
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public boolean isInstanceExist(String path, String property, Object value) throws WBEMException {
                return false;
            }
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_CRITICAL, response.getSeverity());
        assertEquals("Process 'sshd' is not alive", response.getMessage());
    }

    @Override
    protected RequestExecutor initExecutor() {
        return new ProcessAliveRequestExecutor();
    }
}
