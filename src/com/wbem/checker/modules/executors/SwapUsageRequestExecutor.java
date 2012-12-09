package com.wbem.checker.modules.executors;

import javax.cim.CIMInstance;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger64;

import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.exceptions.SourceIsNotFoundException;

public class SwapUsageRequestExecutor extends AbstractLoadPercentageExecutor {
    private static final String REQUEST_TYPE = "swap_usage";
    private static final String PURPOSE = "get current SWAP loading";

    public static final String path = "Linux_OperatingSystem";
    public static final String totalSwapProperty = "TotalSwapSpaceSize";
    public static final String freeSwapProperty = "FreeSpaceInPagingFiles";

    @Override
    public Response execute(WBEMConnection connection) throws Exception {
        CIMInstance[] os = connection.getAllInstances(path);
        if (os.length != 1) {
            throw new SourceIsNotFoundException(path, null, PURPOSE);
        }
        CIMProperty total = os[0].getProperty(totalSwapProperty);
        CIMProperty free = os[0].getProperty(freeSwapProperty);
        return createResponse(computeLoadPercentage(total, free));
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
        return "Current swap usage is ";
    }

    private int computeLoadPercentage(CIMProperty total, CIMProperty free) throws Exception {
        if (total == null || free == null ){
            throw new SourceIsNotFoundException(path, total == null ? totalSwapProperty : freeSwapProperty, PURPOSE);
        }
        UnsignedInteger64 totalValue = (UnsignedInteger64) total.getValue();
        if (totalValue.intValue() == 0) {
            return 0;
        }
        UnsignedInteger64 freeValue = (UnsignedInteger64) free.getValue();
        return 100 - (int) Math.round(freeValue.doubleValue() / totalValue.doubleValue() * 100);
    }
}
