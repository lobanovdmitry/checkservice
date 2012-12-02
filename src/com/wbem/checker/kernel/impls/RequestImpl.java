package com.wbem.checker.kernel.impls;

import java.util.HashMap;
import java.util.Map;

import com.wbem.checker.api.data.Request;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckRequest;

public class RequestImpl implements Request {
  
  private CheckRequest checkRequest;
  private Map<String, String> parameters = new HashMap<String, String>();
  
  public RequestImpl(CheckRequest checkRequest) {
    this.checkRequest = checkRequest;
    for ( com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.Parameter parameter : checkRequest.getParametersList()) {
      parameters.put(parameter.getName(), parameter.getValue());
    }
  }
  
  @Override
  public String getParameterValue(String parameterName) {
    return parameters.get(parameterName);
  }
  
  @Override
  public String getUsername() {
    return checkRequest.getUsername();
  }
  
  @Override
  public String getPassword() {
    return checkRequest.getPassword();
  }
  
  @Override
  public String getHostname() {
    return checkRequest.getHostname();
  }
}
