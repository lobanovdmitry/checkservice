package com.wbem.checker.kernel.impls;

import com.wbem.checker.api.data.Request;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckRequest;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.Parameter;

import junit.framework.TestCase;

public class RequestImplTest extends TestCase {
  
  public void testRequestImpl() throws Exception {
    CheckRequest.Builder builder = CheckRequest.newBuilder();
    builder.setHostname("hostname");
    builder.setUsername("username");
    builder.setPassword("password");
    builder.setType("type");
    Parameter.Builder parameterBuilder = Parameter.newBuilder();
    parameterBuilder.setName("Name");
    parameterBuilder.setValue("Value");
    builder.addParameters(parameterBuilder.build());
    Request request = new RequestImpl(builder.build());
    
    assertEquals("hostname", request.getHostname());
    assertEquals("username", request.getUsername());
    assertEquals("password", request.getPassword());
    assertEquals("Value", request.getParameterValue("Name"));
  }

}
