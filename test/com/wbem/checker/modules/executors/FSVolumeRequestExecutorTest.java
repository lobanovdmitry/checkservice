package com.wbem.checker.modules.executors;

import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger64;
import javax.wbem.WBEMException;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.api.exceptions.SourceIsNotFoundException;
import com.wbem.checker.modules.executors.dummy.FakeRequest;
import com.wbem.checker.modules.executors.dummy.FakeWBEMConnection;

public class FSVolumeRequestExecutorTest extends ExecutorsTestCase {

    public void testRequestTypeAndAuth() throws Exception {
        assertRequestTypeAndAuth("disk_usage", true, "-warn", "-crit", "-mount_point");
    }

    public void testFSsAreNotFound() throws Exception {
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[0];
                }
            });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get loading of file system because: Instances of 'CIM_FileSystem' class are not found!", e.getMessage());
        }
    }
    
    public void testFSInstancesAreNotFound() throws Exception {
        executor.validateRequest(createRequest("/boot", "80%", "98%"));
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[0];
                }
            });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get loading of file system because: Instances of 'CIM_FileSystem' class are not found!", e.getMessage());
        }
    }
    
    public void testPropertyElementNameIsNotFound() throws Exception {
        executor.validateRequest(createRequest("/boot", "80%", "98%"));
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[] {
                            new CIMInstance(new CIMObjectPath(path), new CIMProperty[] {
                                new CIMProperty(FSVolumeRequestExecutor.totalSystemSizeProperty, CIMDataType.UINT64_T, "2000")
                            })
                    };
                }
            });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get loading of file system because: Instances of 'CIM_FileSystem' class are not found!", e.getMessage());
        }
    }
    
    public void testFreeMemoryIsNotFound() throws Exception {
        executor.validateRequest(createRequest("/boot", "80%", "98%"));
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[] {
                            new CIMInstance(new CIMObjectPath(path), new CIMProperty[] {
                                    new CIMProperty(FSVolumeRequestExecutor.mountPointProperty, CIMDataType.STRING_T, "/boot"),
                                    new CIMProperty(FSVolumeRequestExecutor.totalSystemSizeProperty, CIMDataType.UINT64_T, "2000")
                                })
                        };
                    };
                });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get loading of file system because: Property 'AvailableSpace' of CIM_FileSystem's instance is not found!", e.getMessage());
        }
    }
    
    public void testTotalMemoryIsNotFound() throws Exception {
        executor.validateRequest(createRequest("/boot", "80%", "98%"));
        try {
            executor.execute(new FakeWBEMConnection() {
                @Override
                public CIMInstance[] getAllInstances(String path) throws WBEMException {
                    return new CIMInstance[] {
                            new CIMInstance(new CIMObjectPath(path), new CIMProperty[] {
                                    new CIMProperty(FSVolumeRequestExecutor.mountPointProperty, CIMDataType.STRING_T, "/boot"),
                                    new CIMProperty(FSVolumeRequestExecutor.freeSpaceProperty, CIMDataType.UINT64_T, "2000")
                                })
                        };
                    };
                });
            fail();
        } catch (SourceIsNotFoundException e) {
            assertEquals("Impossible to get loading of file system because: Property 'FileSystemSize' of CIM_FileSystem's instance is not found!", e.getMessage());
        }
    }
    
    public void testCorrectRequest() throws Exception {
        executor.validateRequest(createRequest("/boot", "80%", "98%"));
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public CIMInstance[] getAllInstances(String path) throws WBEMException {
                return new CIMInstance[] {
                        new CIMInstance(new CIMObjectPath(path),
                        new CIMProperty[] {
                            new CIMProperty(FSVolumeRequestExecutor.mountPointProperty, CIMDataType.STRING_T, "/boot"),
                            new CIMProperty(FSVolumeRequestExecutor.totalSystemSizeProperty, CIMDataType.UINT64_T, new UnsignedInteger64("1000")),
                            new CIMProperty(FSVolumeRequestExecutor.freeSpaceProperty, CIMDataType.UINT64_T, new UnsignedInteger64("500"))
                        }),
                        new CIMInstance(new CIMObjectPath(path),
                                new CIMProperty[] {
                                    new CIMProperty(FSVolumeRequestExecutor.mountPointProperty, CIMDataType.STRING_T, "/"),
                                    new CIMProperty(FSVolumeRequestExecutor.totalSystemSizeProperty, CIMDataType.UINT64_T, new UnsignedInteger64("1000")),
                                    new CIMProperty(FSVolumeRequestExecutor.freeSpaceProperty, CIMDataType.UINT64_T, new UnsignedInteger64("500"))
                                })
                };
            };
        });
        assertEquals(true, response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_OK, response.getSeverity());
        assertEquals("Loading of file system mounted at '/boot' is 50%", response.getMessage());
    }
    
    public void testTotalMemorySizeIsZero() throws Exception {
        executor.validateRequest(createRequest("/boot", "80%", "98%"));
        Response response = executor.execute(new FakeWBEMConnection() {
            @Override
            public CIMInstance[] getAllInstances(String path) throws WBEMException {
                return new CIMInstance[] { new CIMInstance(new CIMObjectPath(path),
                        new CIMProperty[] {
                            new CIMProperty(FSVolumeRequestExecutor.mountPointProperty, CIMDataType.STRING_T, "/boot"),
                            new CIMProperty(FSVolumeRequestExecutor.totalSystemSizeProperty, CIMDataType.UINT64_T, new UnsignedInteger64("0")),
                            new CIMProperty(FSVolumeRequestExecutor.freeSpaceProperty, CIMDataType.UINT64_T, new UnsignedInteger64("500"))
                        })
                };
            };
        });
        assertEquals(true, response.isRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_OK, response.getSeverity());
        assertEquals("Loading of file system mounted at '/boot' is 0%", response.getMessage());
    }
    

    @Override
    protected RequestExecutor initExecutor() {
        return new FSVolumeRequestExecutor();
    }
    
    private Request createRequest(final String mountPoint, final String warning, final String critical) {
        return new FakeRequest() {
            public String getParameterValue(String parameterName) {
                if ("-mount_point".equals(parameterName)) {
                    return mountPoint;
                }
                if ("-warn".equals(parameterName)) {
                    return warning;
                }

                if ("-crit".equals(parameterName)) {
                    return critical;
                }
                return null;
            };
        };
    }
}
