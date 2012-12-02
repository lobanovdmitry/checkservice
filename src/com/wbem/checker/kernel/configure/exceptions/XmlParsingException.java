package com.wbem.checker.kernel.configure.exceptions;

public class XmlParsingException extends Exception {
  private static final long serialVersionUID = 1L;

  public XmlParsingException(String message) {
    super(message);
  }

  public XmlParsingException(Exception e) {
    super(e);
  }
}