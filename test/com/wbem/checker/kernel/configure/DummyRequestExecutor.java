package com.wbem.checker.kernel.configure;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.exceptions.InvalidParametersException;

public class DummyRequestExecutor implements RequestExecutor {

  private static final String REQUEST_TYPE = "dummy";

  @Override
  public void validateRequest(Request request) throws InvalidParametersException {
  }

  @Override
  public Response execute(WBEMConnection connection) throws Exception {
    return null;
  }

  @Override
  public String[] getRequiredParameters() {
    return new String[0];
  }

  @Override
  public String getRequestType() {
    return REQUEST_TYPE;
  }

  @Override
  public boolean isAuthentificateNeeded() {
    return true;
  }

}
