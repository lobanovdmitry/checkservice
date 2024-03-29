package com.wbem.checker.modules.executors;

import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger64;
import javax.wbem.WBEMException;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.api.exceptions.SourceIsNotFoundException;
import com.wbem.checker.modules.executors.dummy.FakeRequest;
import com.wbem.checker.modules.executors.dummy.FakeWBEMConnection;

public class MemoryUsageRequestExecutorTest extends ExecutorsTestCase {

    public void testRequestTypeAndAuth() throws Exception {
        assertRequestTypeAndAuth("memory_usage", true, "-warn", "-crit");
    }

    public void testNominalCase() throws Exception {
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public CIMInstance[] getAllInstances(String path) throws WBEMException {
                return new CIMInstance[] { new CIMInstance(new CIMObjectPath(MemoryUsageRequestExecutor.path), new CIMProperty[] {
                        new CIMProperty(MemoryUsageRequestExecutor.freeMemoryProperty, CIMDataType.UINT64_T, new UnsignedInteger64("500")),
                        new CIMProperty(MemoryUsageRequestExecutor.totalMemoryProperty, CIMDataType.UINT64_T, new UnsignedInteger64("1000")) }) };
            }
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_CRITICAL, response.getSeverity());
        assertEquals("ALARM: Current memory loading is 50%", response.getMessage());
    }

    public void testTotalValueIsZero() throws Exception {
        executor.validateRequest(new FakeRequest() {
           @Override
        public String getParameterValue(String parameterName) {
               if ( "-warn".equals(parameterName) ) {
                   return "10%";
               }
               if ( "-crit".equals(parameterName) ) {
                   return "20%";
               }
               return null;
        } 
        });
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public CIMInstance[] getAllInstances(String path) throws WBEMException {
                return new CIMInstance[] {
                        new CIMInstance(new CIMObjectPath(MemoryUsageRequestExecutor.path), new CIMProperty[] {
                            new CIMProperty(MemoryUsageRequestExecutor.freeMemoryProperty, CIMDataType.UINT64_T, new UnsignedInteger64("500")),
                            new CIMProperty(MemoryUsageRequestExecutor.totalMemoryProperty, CIMDataType.UINT64_T, new UnsignedInteger64("0"))
                         })
                    };
                };
        });
        assertTrue(response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_OK, response.getSeverity());
        assertEquals("Current memory loading is 0%", response.getMessage());
    }
    
    public void testSeveralInstancesFound() throws Exception {
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[0];
                }
            });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get current memory loading because: Instances of 'Linux_OperatingSystem' class are not found!", e.getMessage());
        }
    }
    
    public void testFreeSpacePropertyAreNotFound() throws Exception {
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[] {
                            new CIMInstance(new CIMObjectPath(path), new CIMProperty[] {
                                    new CIMProperty(MemoryUsageRequestExecutor.totalMemoryProperty, CIMDataType.UINT64_T, new UnsignedInteger64("1000"))
                            })
                    };
                }
            });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get current memory loading because: Property 'FreePhysicalMemory' of Linux_OperatingSystem's instance is not found!", e.getMessage());
        }
    }
    
    public void testTotalSpacePropertyAreNotFound() throws Exception {
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[] {
                            new CIMInstance(new CIMObjectPath(path), new CIMProperty[] {
                                    new CIMProperty(MemoryUsageRequestExecutor.freeMemoryProperty, CIMDataType.UINT64_T, new UnsignedInteger64("1000"))
                            })
                    };
                }
            });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get current memory loading because: Property 'TotalVisibleMemorySize' of Linux_OperatingSystem's instance is not found!", e.getMessage());
        }
    }

    @Override
    protected RequestExecutor initExecutor() {
        return new MemoryUsageRequestExecutor();
    }
}
