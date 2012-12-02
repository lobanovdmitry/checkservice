package com.wbem.checker.modules.executors;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.api.exceptions.InvalidParametersException;

public class ProcessAliveRequestExecutor implements RequestExecutor {
  private static final String NAME_ATTR = "Name";
  private static final String CIM_PROCESS = "CIM_Process";
  private static final String REQUEST_TYPE = "is_process_alive";
  private static final String PROCESS_NAME_PARAMETER = "-n";
  
  private String processName;
  
  @Override
  public Response execute(WBEMConnection connection) throws Exception {
    return createResponse( connection.isInstanceExist(CIM_PROCESS, NAME_ATTR, processName), processName);
  }

  @Override
  public String[] getRequiredParameters() {
    return new String[]{PROCESS_NAME_PARAMETER};
  }
  
  @Override
  public void validateRequest(Request request) throws InvalidParametersException {
    processName = request.getParameterValue(PROCESS_NAME_PARAMETER);
  }

  @Override
  public String getRequestType() {
    return REQUEST_TYPE;
  }

  @Override
  public boolean isAuthentificateNeeded() {
    return true;
  }

  private Response createResponse(boolean isExist, String processName) {
    Response response = new Response();
    response.setCheckSeverity(isExist ? ResponseConstants.SEVERITY_OK : ResponseConstants.SEVERITY_CRITICAL);
    response.setRequestCorrect(ResponseConstants.REQUEST_STATUS_OK);
    response.setMessage(isExist ? "Process " + processName + " is alive" : "Process " + processName + " is not alive");
    return response;
  }
}
