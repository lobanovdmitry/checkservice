package com.wbem.checker.kernel;

import com.wbem.checker.api.connection.WBEMConnection;

public class ConnectionPoolTest extends KernelTestCase {
    
    public void testNominalCase() throws Exception {
        WBEMConnection connection1 = ConnectionPool.findOrCreateConnection(DEFAULT_CREDENTIALS);
        Thread.sleep(1000);
        WBEMConnection connection2 = ConnectionPool.findOrCreateConnection(DEFAULT_CREDENTIALS);
        assertTrue(connection1 == connection2);
    }
    
    public void testRemoveConnection() throws Exception {
        WBEMConnection connection1 = ConnectionPool.findOrCreateConnection(DEFAULT_CREDENTIALS);
        Thread.sleep(1000);
        ConnectionPool.removeConnection(DEFAULT_CREDENTIALS);
        WBEMConnection connection2 = ConnectionPool.findOrCreateConnection(DEFAULT_CREDENTIALS);
        assertTrue(connection1 != connection2);
    }
}
