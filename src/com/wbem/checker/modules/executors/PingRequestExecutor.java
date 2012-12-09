package com.wbem.checker.modules.executors;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.api.exceptions.InvalidParametersException;

public class PingRequestExecutor implements RequestExecutor {
  private static final int DEFAULT_TIMEOUT = 10000;
  public static final String REQUEST_TYPE = "ping";
  
  private String hostname;

  @Override
  public String[] getRequiredParameters() {
    return new String[0];
  }

  @Override
  public void validateRequest(Request request) throws InvalidParametersException {
    hostname = request.getHostname();
  }

  @Override
  public Response execute(WBEMConnection connection) throws Exception {
    Response response = new Response();
    try {
      boolean pingResult = InetAddress.getByName(hostname).isReachable(DEFAULT_TIMEOUT);
      response.setCheckSeverity(pingResult ? ResponseConstants.SEVERITY_OK : ResponseConstants.SEVERITY_CRITICAL);
      response.setRequestCorrect(ResponseConstants.REQUEST_STATUS_OK);
      response.setMessage("Host '" + hostname + "' is " + (pingResult ? "" : "not ") + "reachable through 'ping'");
    } catch (UnknownHostException e) {
      response.setRequestCorrect(ResponseConstants.REQUEST_STATUS_OK);
      response.setCheckSeverity(ResponseConstants.SEVERITY_UNKNOWN);
      response.setMessage("Unknown host: '" + hostname + "'");
    }
    return response;
  }

  @Override
  public String getRequestType() {
    return REQUEST_TYPE;
  }

  @Override
  public boolean isAuthentificateNeeded() {
    return false;
  }
}
