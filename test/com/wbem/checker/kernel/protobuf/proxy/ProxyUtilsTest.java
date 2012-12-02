package com.wbem.checker.kernel.protobuf.proxy;

import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.kernel.HostnameAndCredentials;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckRequest;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckResponse;

import junit.framework.TestCase;

public class ProxyUtilsTest extends TestCase {
  
  public void testCreateCheckResponse() throws Exception {
    new ProxyUtils();
    Response response = new Response();
    response.setCheckSeverity(ResponseConstants.SEVERITY_OK);
    response.setMessage("Message");
    response.setRequestCorrect(ResponseConstants.REQUEST_STATUS_KO);
    CheckResponse checkResponse = ProxyUtils.createCheckResponse(response);
    assertEquals(ResponseConstants.SEVERITY_OK, checkResponse.getCheckSeverity());
    assertEquals(ResponseConstants.REQUEST_STATUS_KO, checkResponse.getIsRequestCorrect());
    assertEquals("Message", checkResponse.getMessage());
  }
  
  public void test() throws Exception {
    CheckRequest.Builder request = CheckRequest.newBuilder();
    request.setHostname("hostname");
    request.setUsername("username");
    request.setPassword("password");
    request.setType("type");
    HostnameAndCredentials credentials = ProxyUtils.getHostAndCredentianls(request.build());
    assertEquals("hostname", credentials.getHostname());
    assertEquals("username", credentials.getLogin());
    assertEquals("password", credentials.getPassword());
  }
}
