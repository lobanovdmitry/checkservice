package com.wbem.checker.kernel;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.wbem.WBEMException;

import com.wbem.checker.api.connection.WBEMConnection;

public class ConnectionPool {
    
  private static final int DEFAULT_TIME_OUT = 1*60*1000;
  
  private static Map<HostnameAndCredentials, WBEMConnectionImpl> allConnections = new HashMap<HostnameAndCredentials, WBEMConnectionImpl>();
  private static Map<HostnameAndCredentials, Long> liveConnectionTime = new HashMap<HostnameAndCredentials, Long>();
  private static ConnectionsHeartBeat heartBeatDeamon = new ConnectionsHeartBeat(DEFAULT_TIME_OUT);
  
  static {
    heartBeatDeamon.start();
  }
  
  public static synchronized WBEMConnection findOrCreateConnection(HostnameAndCredentials credentials) throws MalformedURLException, IllegalArgumentException, WBEMException {
    if (allConnections.containsKey(credentials)) {
      WBEMConnectionImpl connectionHolder = allConnections.get(credentials);
      if ( connectionHolder.isAlive() ) {
        return connectionHolder;
      } else {
        allConnections.remove(credentials);
        liveConnectionTime.remove(credentials);
        return findOrCreateConnection(credentials);
      }
    }
    WBEMConnectionImpl connectionHolder = new WBEMConnectionImpl(credentials);
    connectionHolder.createSession();
    allConnections.put(credentials, connectionHolder);
    liveConnectionTime.put(credentials, new Date().getTime());
    return connectionHolder;
  }
  
  public static synchronized void removeConnection(HostnameAndCredentials credentials) {
    allConnections.remove(credentials);
    liveConnectionTime.remove(credentials);
  }
  
  private static class ConnectionsHeartBeat extends Thread {
    private static final int DEFAULT_HEARTBEAT_TIMEOUT = 1*60*1000;
    
    private int timeout = 0;
    
    public ConnectionsHeartBeat(int timeout) {
      this.timeout = timeout;
      setDaemon(true);
      setName("CONNECTIONS_GC_HEARTBEAT_DEAMON");
    }
    
    @Override
    public void run() {
      try {
        while(true) {
          Thread.sleep(DEFAULT_HEARTBEAT_TIMEOUT);
          cleanObsoleteConnections();
        }
      } catch (InterruptedException e) {
        // nothing to do
      }
    }

    private synchronized void cleanObsoleteConnections() {
      long currentTime = new Date().getTime();
      for ( Entry<HostnameAndCredentials, WBEMConnectionImpl> entry : allConnections.entrySet() ) {
        long liveTime = liveConnectionTime.get(entry.getKey());
        if ( currentTime - liveTime > timeout ) {
          removeConnection(entry.getKey());
        }
      }
    }
  }
}