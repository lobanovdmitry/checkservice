package com.wbem.checker.kernel.configure.exceptions;


public class IncorrectRootTagException extends XmlParsingException {
  private static final long serialVersionUID = 1L;

  public IncorrectRootTagException(String invalidRootTagMessage) {
    super(invalidRootTagMessage);
  }
}