package com.wbem.checker.api;

import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.exceptions.InvalidParametersException;


public interface RequestExecutor {

  void validateRequest(Request request) throws InvalidParametersException;

  Response execute(WBEMConnection connection) throws Exception;
    
  String[] getRequiredParameters();
  
  String getRequestType();
  
  boolean isAuthentificateNeeded();
}
