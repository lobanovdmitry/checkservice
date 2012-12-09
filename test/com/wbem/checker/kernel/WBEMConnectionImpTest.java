package com.wbem.checker.kernel;

import javax.cim.CIMInstance;
import javax.cim.UnsignedInteger32;

public class WBEMConnectionImpTest extends KernelTestCase {
    
    private static final WBEMConnectionImpl connection = new WBEMConnectionImpl(DEFAULT_CREDENTIALS);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        connection.createSession();
        assertTrue(connection.isAlive());
    }
    
    @Override
    protected void tearDown() throws Exception {
        connection.close();
        super.tearDown();
    }

    public void testFindSingleInstance() throws Exception {
        CIMInstance opSystem = connection.getAllInstances("CIM_OperatingSystem")[0];
        assertEquals("CentOS", opSystem.getProperty("Name").getValue());
    }

    public void testIsInstanceExists() throws Exception {
        assertEquals(true, connection.isInstanceExist("CIM_OperatingSystem", "Name", "CentOS"));
        assertEquals(false, connection.isInstanceExist("CIM_OperatingSystem", "Name", "fake"));
    }

    public void testGetAverageValues() throws Exception {
        Object[] values = connection.getPropertyValuesOfAllInstances("CIM_OperatingSystem", "NumberOfUsers");
        assertTrue(((UnsignedInteger32)values[0]).intValue() > 0);
        assertTrue(((UnsignedInteger32)values[1]).intValue() > 0);
    }

}