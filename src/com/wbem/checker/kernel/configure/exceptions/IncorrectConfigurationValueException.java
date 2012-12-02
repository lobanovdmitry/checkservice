package com.wbem.checker.kernel.configure.exceptions;

public class IncorrectConfigurationValueException extends XmlParsingException {
  private static final long serialVersionUID = 1L;
  
  public IncorrectConfigurationValueException(String message) {
    super(message);
  }
}
