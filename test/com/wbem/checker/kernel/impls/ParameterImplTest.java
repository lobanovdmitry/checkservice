package com.wbem.checker.kernel.impls;

import junit.framework.TestCase;

import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.Parameter;

public class ParameterImplTest extends TestCase {
  
  public void testParameterImpl() throws Exception {
    Parameter.Builder parameterBuilder = Parameter.newBuilder();
    parameterBuilder.setName("Name");
    parameterBuilder.setValue("Value");
    com.wbem.checker.api.data.Parameter impl = new ParameterImpl(parameterBuilder.build());
    assertEquals("Name", impl.getName());
    assertEquals("Value", impl.getValue());
  }

}
