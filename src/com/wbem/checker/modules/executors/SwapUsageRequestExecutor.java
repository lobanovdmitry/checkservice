package com.wbem.checker.modules.executors;

import javax.cim.CIMInstance;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger64;

import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Response;

public class SwapUsageRequestExecutor extends AbstractLoadPercentageExecutor {
  private static final String REQUEST_TYPE = "swap_usage";
  public static final String path = "Linux_OperatingSystem";
  public static final String totalSwapProperty = "TotalSwapSpaceSize";
  public static final String freeSwapProperty = "FreeSpaceInPagingFiles";

  @Override
  public Response execute(WBEMConnection connection) throws Exception {
    CIMInstance os = connection.findSingleInstance(path);
    CIMProperty total = os.getProperty(totalSwapProperty);
    CIMProperty free = os.getProperty(freeSwapProperty);
    return createResponse(computeLoadPercentage(total.getValue(), free.getValue()));
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

  private int computeLoadPercentage(Object total, Object free) throws Exception {
    UnsignedInteger64 totalValue = (UnsignedInteger64) total;
    if ( totalValue.intValue() == 0) {
      return 0;
    }
    UnsignedInteger64 freeValue = (UnsignedInteger64) free;
    return 100 - (int) Math.round(freeValue.doubleValue() / totalValue.doubleValue() * 100);
  }
}
