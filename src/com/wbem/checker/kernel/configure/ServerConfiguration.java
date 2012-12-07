package com.wbem.checker.kernel.configure;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.wbem.checker.api.RequestExecutor;

public class ServerConfiguration {
  public static final int DEFAULT_PORT = 1399;
  public static final ThreadPoolConfiguration DEFAULT_THREAD_POOL_CONFIGURATION = new ThreadPoolConfiguration();
  
  private int serverPort = DEFAULT_PORT;
  private ThreadPoolConfiguration threadPoolConfiguration = DEFAULT_THREAD_POOL_CONFIGURATION;
  private Modules modules = new Modules();
  
  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }
  
  public int getServerPort() {
    return serverPort;
  }
  
  public Modules getModules() {
    return modules;
  }
  
  public void setModules(Modules modules) {
    this.modules = modules;
  }
  
  public ThreadPoolConfiguration getThreadPoolConfiguration() {
    return threadPoolConfiguration;
  }
  
  public void setThreadPoolConfiguration(ThreadPoolConfiguration threadPoolConfiguration) {
    this.threadPoolConfiguration = threadPoolConfiguration;
  }
  
  @Override
  public boolean equals(Object obj) {
    if ( this == obj) {
      return true;
    }
    if ( obj == null || !(obj instanceof ServerConfiguration)) {
      return false;
    }
    ServerConfiguration o = (ServerConfiguration) obj;
    if ( serverPort != o.serverPort ) {
      return false;
    }
    if ( threadPoolConfiguration != null ? !threadPoolConfiguration.equals(o.threadPoolConfiguration) : o.threadPoolConfiguration != null) {
      return false;
    }
    if ( modules != null ? !modules.equals(o.modules) : o.modules != null) {
      return false;
    }
    return true;
  }
  
  @Override
  public int hashCode() {
    int result = 29;
    result += serverPort;
    result += threadPoolConfiguration != null ? threadPoolConfiguration.hashCode() : 0;
    result += modules != null ? modules.hashCode() : 0;
    return result;
  }
  
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Check service configuration:\n")
          .append(" * Port = ").append(serverPort)
          .append("\n")
          .append(threadPoolConfiguration)
          .append("\n")
          .append(modules);
    return buffer.toString();
  }
  
  public static class ThreadPoolConfiguration {
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAX_POOL_SIZE = 20;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 10000;
    
    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    private int keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    
    public ThreadPoolConfiguration() {
      //all by default
    }
    
    public ThreadPoolConfiguration(int corePoolSize, int maxPoolSize, int keepAliveTime, TimeUnit timeUnit) {
      this.corePoolSize = corePoolSize;
      this.maxPoolSize = maxPoolSize;
      this.keepAliveTime = keepAliveTime;
      this.timeUnit = timeUnit; 
    }
    
    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }
    
    public void setKeepAliveTime(int keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
    }
    
    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }
    
    public void setTimeUnit(TimeUnit timeUnit) {
      this.timeUnit = timeUnit;
    }
    
    public int getCorePoolSize() {
      return corePoolSize;
    }
    
    public int getMaxPoolSize() {
      return maxPoolSize;
    }
    
    public int getKeepAliveTime() {
      return keepAliveTime;
    }
    
    public TimeUnit getTimeUnit() {
      return timeUnit;
    }
    
    @Override
    public boolean equals(Object obj) {
      if ( this == obj ) {
        return true;
      }
      if ( obj == null || !(obj instanceof ThreadPoolConfiguration)) {
        return false;
      }
      ThreadPoolConfiguration o = (ThreadPoolConfiguration) obj;
      if ( corePoolSize != o.corePoolSize ) {
        return false;
      }
      if ( maxPoolSize != o.maxPoolSize ) {
        return false;
      }
      if ( keepAliveTime != o.keepAliveTime ) {
        return false;
      }
      if ( timeUnit != null ? !timeUnit.equals(o.timeUnit) : o.timeUnit != null) {
        return false;
      }
      return true;
    }
    
    @Override
    public int hashCode() {
      int result = 29;
      result += corePoolSize;
      result += maxPoolSize;
      result += keepAliveTime;
      result += timeUnit != null ? timeUnit.hashCode() : 0;
      return result;
    }
    
    @Override
    public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(" * Thread pool configuration:\n")
            .append("    Core pool size = ").append(corePoolSize).append("\n")
            .append("    Max pool size = ").append(maxPoolSize).append("\n")
            .append("    Keep alive time = ").append(keepAliveTime);
      switch( timeUnit) {
        case DAYS:
          buffer.append(" days");
          break;
        case HOURS:
          buffer.append(" hours");
          break;
        case MINUTES:
          buffer.append(" minutes");
          break;
        case SECONDS:
          buffer.append(" seconds");
          break;
        default:
          break;
      }
      return buffer.toString();
    }
  }
  
  public static class Modules {
    private Set<RequestExecutor> modules = new HashSet<RequestExecutor>();
    
    public void addModule(RequestExecutor executor) {
      modules.add(executor);
    }
    
    public Set<RequestExecutor> getModules() {
      return modules;
    }
    
    @Override
    public boolean equals(Object obj) {
      if ( this == obj ) {
        return true;
      }
      if ( obj == null || !( obj instanceof Modules)) {
        return false;
      }
      Modules o = (Modules) obj;
      if ( modules != null ? !(modules.equals(o.modules)) : o.modules != null ) {
        return false;
      }
      return true;
    }
    @Override
    public int hashCode() {
      return modules == null ? 29 : 29 * modules.hashCode();
    }
    
    @Override
    public String toString() {
      StringBuffer buffer = new StringBuffer(" * Modules: \n");
      for( RequestExecutor executor : modules ) {
        buffer.append(executor.getClass().getCanonicalName()).append("\n");
      }
      return buffer.toString();
    }
  }
}
