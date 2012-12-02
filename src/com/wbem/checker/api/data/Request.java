package com.wbem.checker.api.data;


public interface Request {
  String getHostname();

  String getUsername();

  String getPassword();

  String getParameterValue(String parameterName);
}
