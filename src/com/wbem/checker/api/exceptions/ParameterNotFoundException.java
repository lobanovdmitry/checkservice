package com.wbem.checker.api.exceptions;


public class ParameterNotFoundException extends InvalidParametersException {

  private static final long serialVersionUID = 1L;

  private String[] parameters;
  
  public ParameterNotFoundException(String... parameters) {
    super("");
    this.parameters = parameters;
  }
  
  @Override
  public String getMessage() {
    StringBuffer buffer = new StringBuffer("Required parameters are not found: ");
    for ( int i = 0; i < parameters.length; i++ ) {
      buffer.append(parameters[i]);
      if ( i + 1 < parameters.length ) {
        buffer.append(", ");
      }
    }
    return buffer.toString();
  }
}
