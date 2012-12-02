package com.wbem.checker.kernel.configure.exceptions;

import org.w3c.dom.Node;


public class SingleChildExpectedException extends XmlParsingException {
  private static final long serialVersionUID = 1L;

  public SingleChildExpectedException(Node node, String name) {
    super("Single child with tag '" + name + "' is expected!");
  }
}