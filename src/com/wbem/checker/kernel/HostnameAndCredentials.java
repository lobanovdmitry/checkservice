package com.wbem.checker.kernel;

public class HostnameAndCredentials {
  
  private String hostname;
  private String login;
  private String password;
  
  public HostnameAndCredentials(String hostname, String login, String password) {
    this.hostname = hostname;
    this.login = login;
    this.password = password;
  }
  
  public String getHostname() {
    return hostname;
  }
  
  public String getLogin() {
    return login;
  }
  
  public String getPassword() {
    return password;
  }
  
  @Override
  public boolean equals(Object obj) {
    if ( this == obj ) {
      return true;
    }
    
    if (!( obj instanceof HostnameAndCredentials) ) {
      return false;
    }
    HostnameAndCredentials o = (HostnameAndCredentials) obj;
    
    if ( hostname != null ?  !hostname.equals(o.hostname) : o.hostname != null ) {
      return false;
    }
    if ( login != null ?  !login.equals(o.login) : o.login != null ) {
      return false;
    }
    if ( password != null ?  !password.equals(o.password) : o.password != null ) {
      return false;
    }
    return true;
  }
  
  @Override
  public int hashCode() {
    int result = 29;
    result += hostname != null ? hostname.hashCode() : 0;
    result += login != null ? login.hashCode() : 0;
    result += password != null ? password.hashCode() : 0;
    return result;
  }
}
