package com.wbem.checker.api.exceptions;


public class UnknownRequestTypeException extends InvalidParametersException {
  private static final long serialVersionUID = 1L;
  
  public UnknownRequestTypeException(String type) {
    super("Type '" + type + "' of the request is unknown!");
  }
}
