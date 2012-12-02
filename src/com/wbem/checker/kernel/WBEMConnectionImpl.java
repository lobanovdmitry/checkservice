package com.wbem.checker.kernel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientFactory;

import com.wbem.checker.api.connection.WBEMConnection;

public class WBEMConnectionImpl implements WBEMConnection {
  private static final String DEFAULT_PROTOCOL_TYPE = "CIM-XML";
  private static final String DEFAULT_TRANSPORT_PROTOCOL = "http";
  private static final String DEFAULT_NAMESPACE = "root/cimv2";
  private static final int DEFAULT_PORT = 5988;
  
  private WBEMClient wbemClient;
  private HostnameAndCredentials credentials;
  private boolean isAlive = false;
  
  public WBEMConnectionImpl(HostnameAndCredentials credentials) {
    this.credentials = credentials;
  }
 
  public Object[] getAverageValue(String path, String property) throws WBEMException {
    CloseableIterator iterator = wbemClient.enumerateInstances(new CIMObjectPath(path, DEFAULT_NAMESPACE), true, true, true, new String[]{property});
    List<Object> properties = new ArrayList<Object>();
    while( iterator.hasNext() ) {
      properties.add(((CIMInstance)iterator.next()).getProperty(property).getValue());
    }
    return properties.toArray(new Object[0]);
  }
  
  public boolean isInstanceExist(String path, String property, Object value) throws WBEMException {
    CloseableIterator iterator = wbemClient.enumerateInstances(new CIMObjectPath(path, DEFAULT_NAMESPACE), false, false, false, new String[]{property});
    while( iterator.hasNext() ) {
      CIMInstance instance = (CIMInstance) iterator.next();
      if ( instance.getPropertyValue(property).equals(value) ) {
        return true;
      }
    }
    return false;
  }
  
  public CIMInstance findSingleInstance(String path) throws WBEMException {
    CloseableIterator iterator = wbemClient.enumerateInstances(new CIMObjectPath(path, DEFAULT_NAMESPACE), false, false, false, null);
    if ( iterator.hasNext() ) {
      return (CIMInstance) iterator.next(); 
    }
    return null;
  }
   
  public boolean isAlive() {
    return isAlive;
  }

  void createSession() throws MalformedURLException, IllegalArgumentException, WBEMException {
    URL cimomUrl = new URL(DEFAULT_TRANSPORT_PROTOCOL + "://" + credentials.getHostname() + ":" + DEFAULT_PORT);
    CIMObjectPath path = new CIMObjectPath(cimomUrl.getProtocol(), cimomUrl.getHost(), String.valueOf(cimomUrl.getPort()), null, null, null);
    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(credentials.getLogin()));
    subject.getPrivateCredentials().add(new PasswordCredential(credentials.getPassword()));
    wbemClient = WBEMClientFactory.getClient(DEFAULT_PROTOCOL_TYPE);
    wbemClient.initialize(path, subject, Locale.getAvailableLocales());
    isAlive = true;
  }
  
  void close() {
    wbemClient.close();
    isAlive = false;
  }
}
