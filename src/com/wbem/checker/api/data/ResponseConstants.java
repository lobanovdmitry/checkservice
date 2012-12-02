package com.wbem.checker.api.data;

public class ResponseConstants {
  
  /*
   * Severity - check status
   */
  public static final int SEVERITY_OK = 0;
  public static final int SEVERITY_WARNING = 1;
  public static final int SEVERITY_CRITICAL = 2;
  public static final int SEVERITY_UNKNOWN = 3;

  /*
   * Response status ( OK if the host is reachable and authentication is correct)
   */
  public static final boolean REQUEST_STATUS_OK = true;
  public static final boolean REQUEST_STATUS_KO = false;
}
