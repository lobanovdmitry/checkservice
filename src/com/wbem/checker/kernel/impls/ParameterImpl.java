package com.wbem.checker.kernel.impls;

import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.Parameter;

public class ParameterImpl implements com.wbem.checker.api.data.Parameter {
  
  private Parameter parameter;
  
  public ParameterImpl(Parameter parameter) {
    this.parameter = parameter;
  }
  
  @Override
  public String getName() {
    return parameter.getName();
  }
  
  @Override
  public String getValue() {
    return parameter.getValue();
  }

}
