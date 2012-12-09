package com.wbem.checker.modules.executors;

import javax.cim.UnsignedInteger16;
import javax.wbem.WBEMException;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.api.exceptions.InvalidParametersException;
import com.wbem.checker.api.exceptions.SourceIsNotFoundException;
import com.wbem.checker.modules.executors.dummy.FakeRequest;
import com.wbem.checker.modules.executors.dummy.FakeWBEMConnection;

public class CpuUsageRequestExecutorTest extends ExecutorsTestCase {
    
    public void testRequestTypeAndAuth() throws Exception {
        assertRequestTypeAndAuth("cpu_usage", true, "-warn", "-crit");
    }

    public void testWarningCpuLoading() throws Exception {
        executor.validateRequest(new ParameterizedFakeRequest("30", "45"));
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public Object[] getPropertyValuesOfAllInstances(String path, String property) throws WBEMException {
                return new UnsignedInteger16[] { new UnsignedInteger16(15), new UnsignedInteger16(55) };
            }
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_WARNING, response.getSeverity());
        assertEquals("WARNING: Current CPU loading is 35%", response.getMessage());
        
        executor.validateRequest(new ParameterizedFakeRequest("30%", "45%"));
        response = executor.execute(new FakeWBEMConnection() {
            @Override
            public Object[] getPropertyValuesOfAllInstances(String path, String property) throws WBEMException {
                return new UnsignedInteger16[] { new UnsignedInteger16(15), new UnsignedInteger16(55) };
            }
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_WARNING, response.getSeverity());
        assertEquals("WARNING: Current CPU loading is 35%", response.getMessage());
    }
    
    public void testCriticalCpuLoading() throws Exception {
        executor.validateRequest(new ParameterizedFakeRequest("30", "45"));
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public Object[] getPropertyValuesOfAllInstances(String path, String property) throws WBEMException {
                return new UnsignedInteger16[] { new UnsignedInteger16(10), new UnsignedInteger16(90) };
            }
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_CRITICAL, response.getSeverity());
        assertEquals("ALARM: Current CPU loading is 50%", response.getMessage());
    }
    
    public void testOKCpuLoading() throws Exception {
        executor.validateRequest(new ParameterizedFakeRequest("30", "45"));
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public Object[] getPropertyValuesOfAllInstances(String path, String property) throws WBEMException {
                return new UnsignedInteger16[] { new UnsignedInteger16(10), new UnsignedInteger16(5) };
            }
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_OK, response.getSeverity());
        assertEquals("Current CPU loading is 7%", response.getMessage());
    }
    
    public void testNegativePercetageValues() throws Exception {
        try {
            executor.validateRequest(new ParameterizedFakeRequest("-1%", "45%"));
            fail();
        } catch (InvalidParametersException e) {
            assertEquals("Warning percentage value must be integer from 0 to 100!", e.getMessage());
        }
        
        try {
            executor.validateRequest(new ParameterizedFakeRequest("1%", "-45%"));
            fail();
        } catch (InvalidParametersException e) {
            assertEquals("Critical percentage value must be integer from 0 to 100 and bigger than Warning percentage!", e.getMessage());
        }
    }
    
    public void testPercetageValuesAreTooBig() throws Exception {
        try {
            executor.validateRequest(new ParameterizedFakeRequest("101%", "102%"));
            fail();
        } catch (InvalidParametersException e) {
            assertEquals("Warning percentage value must be integer from 0 to 100!", e.getMessage());
        }
        
        try {
            executor.validateRequest(new ParameterizedFakeRequest("10%", "102%"));
            fail();
        } catch (InvalidParametersException e) {
            assertEquals("Critical percentage value must be integer from 0 to 100 and bigger than Warning percentage!", e.getMessage());
        }
    }
    
    public void testWarningValueIsBiggerThanCritical() throws Exception {
        try {
            executor.validateRequest(new ParameterizedFakeRequest("20%", "10%"));
            fail();
        } catch (InvalidParametersException e) {
            assertEquals("Critical percentage value must be integer from 0 to 100 and bigger than Warning percentage!", e.getMessage());
        }
    }
    
    public void testDoublePercetageValues() throws Exception {
        try {
            executor.validateRequest(new ParameterizedFakeRequest("1.90%", "45%"));
            fail();
        } catch (InvalidParametersException e) {
            assertEquals("'1.90' can not be parsed as Integer!", e.getMessage());
        }
    }
    
    public void testPropertyIsNotFound() throws Exception {
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public Object[] getPropertyValuesOfAllInstances(String path, String property) throws WBEMException {
                    return new Object[0];
                }
            });
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to evaluate current CPU loading because: Property 'LoadPercentage' of CIM_Processor's instance is not found!", e.getMessage());
        }
            
    }

    @Override
    protected RequestExecutor initExecutor() {
        return new CpuUsageRequestExecutor();
    }
    
    private static class ParameterizedFakeRequest extends FakeRequest {
        
        String warning;
        String critical;
        
        public ParameterizedFakeRequest(String warning, String critical) {
            this.critical = critical;
            this.warning = warning;
        }
        
        @Override
        public String getParameterValue(String parameterName) {
            if ("-warn".equals(parameterName)) {
                return warning;
            }

            if ("-crit".equals(parameterName)) {
                return critical;
            }
            return null;
        }
    }
}
