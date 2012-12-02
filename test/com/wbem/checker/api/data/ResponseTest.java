package com.wbem.checker.api.data;

import junit.framework.TestCase;

public class ResponseTest extends TestCase {
  
  public void testNominalCase() throws Exception {
    Response response = new Response();
    response.setCheckSeverity(ResponseConstants.SEVERITY_CRITICAL);
    response.setMessage("Message");
    response.setRequestCorrect(ResponseConstants.REQUEST_STATUS_KO);
    
    assertEquals(ResponseConstants.REQUEST_STATUS_KO, response.isRequestCorrect());
    assertEquals("Message", response.getMessage());
    assertEquals(ResponseConstants.SEVERITY_CRITICAL, response.getSeverity());
  }
  
  public void testResponseConstans() throws Exception {
    new ResponseConstants();
    assertTrue(ResponseConstants.REQUEST_STATUS_OK);
    assertFalse(ResponseConstants.REQUEST_STATUS_KO);
    
    assertEquals(3, ResponseConstants.SEVERITY_UNKNOWN);
    assertEquals(2, ResponseConstants.SEVERITY_CRITICAL);
    assertEquals(1, ResponseConstants.SEVERITY_WARNING);
    assertEquals(0, ResponseConstants.SEVERITY_OK);
  }

}
