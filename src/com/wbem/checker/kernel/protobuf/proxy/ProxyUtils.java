package com.wbem.checker.kernel.protobuf.proxy;

import com.wbem.checker.api.data.Response;
import com.wbem.checker.kernel.HostnameAndCredentials;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckRequest;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckResponse;


public class ProxyUtils {
  
  public static CheckResponse createCheckResponse(Response response) {
    WbemCheckProxy.CheckResponse.Builder result = WbemCheckProxy.CheckResponse.newBuilder();
    result.setCheckSeverity(response.getSeverity());
    result.setIsRequestCorrect(response.isRequestCorrect());
    result.setMessage(response.getMessage());
    return result.build();
  }
  
  
  public static HostnameAndCredentials getHostAndCredentianls(CheckRequest request) {
    return new HostnameAndCredentials(request.getHostname(),
                                      request.getUsername(),
                                      request.getPassword());
  }
  
}
