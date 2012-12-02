package com.wbem.checker.api.data;

public class Response {
  private boolean isRequestCorrect = false;
  private String message = "";
  private int severity = -1;
  
  public Response() {}

  public void setRequestCorrect(boolean isRequestCorrect) {
    this.isRequestCorrect = isRequestCorrect;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public void setCheckSeverity(int severity) {
    this.severity = severity;
  }
  
  public String getMessage() {
    return message;
  }
  
  public int getSeverity() {
    return severity;
  }
  
  public boolean isRequestCorrect() {
    return isRequestCorrect;
  }
}
