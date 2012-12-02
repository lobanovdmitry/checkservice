package com.wbem.checker.kernel;

import junit.framework.TestCase;

public class HostNameCredentialsTest extends TestCase {

  public void testEqualsAndHashCode() throws Exception {
    HostnameAndCredentials hn1 = new HostnameAndCredentials("name", "login", "pass");
    HostnameAndCredentials hn2 = new HostnameAndCredentials("name", "login", "pass");
    
    assertEquals(hn1, hn2);
    
    HostnameAndCredentials diff_name = new HostnameAndCredentials("xxx", "login", "pass");
    HostnameAndCredentials null_name = new HostnameAndCredentials(null, "login", "pass");
    HostnameAndCredentials diff_login = new HostnameAndCredentials("name", "xxx", "pass");
    HostnameAndCredentials null_login = new HostnameAndCredentials("name", null, "pass");
    HostnameAndCredentials diff_pass = new HostnameAndCredentials("name", "login", "xxx");
    HostnameAndCredentials null_pass = new HostnameAndCredentials("name", "login", null);
    
    assertFalse(hn1.equals(null));
    assertFalse(hn1.equals(""));
    
    checkEquals(hn1, diff_name, null_name);
    checkEquals(hn1, diff_login, null_login);
    checkEquals(hn1, diff_pass, null_pass);
    
    checkHashCode(hn1, diff_name, null_name);
    checkHashCode(hn1, diff_login, null_login);
    checkHashCode(hn1, diff_pass, null_pass);
  }
  
  private static void checkEquals(HostnameAndCredentials hn1, HostnameAndCredentials hn2, HostnameAndCredentials hn_null) {
    assertTrue(hn1.equals(hn1));
    assertTrue(hn2.equals(hn2));
    assertTrue(hn_null.equals(hn_null));
    
    assertFalse(hn1.equals(hn2));
    assertFalse(hn2.equals(hn1));
    
    assertFalse(hn2.equals(hn_null));
    assertFalse(hn_null.equals(hn2));
    
    assertFalse(hn1.equals(hn_null));
    assertFalse(hn_null.equals(hn1));
  }
  
  private static void checkHashCode(HostnameAndCredentials hn1, HostnameAndCredentials hn2, HostnameAndCredentials hn_null) {
    assertFalse( hn1.hashCode() == hn2.hashCode());
    assertFalse( hn1.hashCode() == hn_null.hashCode());
  }
}
