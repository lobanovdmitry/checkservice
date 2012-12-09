package com.wbem.checker.modules.executors;

import javax.cim.UnsignedInteger16;

import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Response;

public class CpuUsageRequestExecutor extends AbstractLoadPercentageExecutor {
  public static final String REQUEST_TYPE = "cpu_usage";

  private static final String path = "CIM_Processor";
  private static final String property = "LoadPercentage";

  @Override
  public Response execute(WBEMConnection connection) throws Exception {
    Object[] values = connection.getPropertyValuesOfAllInstances(path, property);
    int result = 0;
    for (Object value : values) {
      UnsignedInteger16 integer16 = (UnsignedInteger16) value;
      result += integer16.intValue();
    }
    return createResponse(result / values.length);
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
    return "Current CPU loading is ";
  }
}
