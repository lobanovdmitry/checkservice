package com.wbem.checker.modules.executors;

import com.wbem.checker.api.RequestExecutor;

public class FSVolumeRequestExecutorTest extends ExecutorsTestCase {
    
    public void testRequestTypeAndAuth() throws Exception {
        assertRequestTypeAndAuth("disk_usage", true, "-w", "-c");
    }

    @Override
    protected RequestExecutor initExecutor() {
        return new FSVolumeRequestExecutor();
    }
}
