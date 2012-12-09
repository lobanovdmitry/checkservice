package com.wbem.checker.modules.executors;

import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Response;

public class FSVolumeRequestExecutor extends AbstractLoadPercentageExecutor {

    private static final String REQUEST_TYPE = "disk_usage";
    
    @Override
    public Response execute(WBEMConnection connection) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    public boolean isAuthentificateNeeded() {
        return true;
    }

    @Override
    protected String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }
}
