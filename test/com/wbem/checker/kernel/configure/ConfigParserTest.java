package com.wbem.checker.kernel.configure;

import java.io.File;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.wbem.checker.api.DummyRequestExecutor;
import com.wbem.checker.kernel.configure.ServerConfiguration.ThreadPoolConfiguration;
import com.wbem.checker.kernel.configure.exceptions.IncorrectConfigurationValueException;
import com.wbem.checker.kernel.configure.exceptions.XmlParsingException;

public class ConfigParserTest extends TestCase {

  public void testNominalCase() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='SECONDS'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ServerConfiguration actualConfig = ConfigParser.parse(stringReader);
    ServerConfiguration expectedConfig = new ServerConfiguration();
    expectedConfig.setServerPort(4126);
    ThreadPoolConfiguration expected = expectedConfig.getThreadPoolConfiguration();
    expected.setCorePoolSize(10);
    expected.setMaxPoolSize(10);
    expected.setKeepAliveTime(10);
    expected.setTimeUnit(TimeUnit.SECONDS);
    assertEquals(expectedConfig, actualConfig);
    new ConfigParser();
  }
  
  public void testDefaultPortConfiguration() throws Exception {
    String configXml = 
        "<checkServiceConfig>" +
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ServerConfiguration actualConfig = ConfigParser.parse(stringReader);
    assertEquals(ServerConfiguration.DEFAULT_PORT, actualConfig.getServerPort());
    assertEquals(ServerConfiguration.DEFAULT_THREAD_POOL_CONFIGURATION, actualConfig.getThreadPoolConfiguration());
  }
  
  public void testDefaultThreadPoolConfiguration() throws Exception {
    String configXml = 
        "<checkServiceConfig>" +
        "  <threadPool/>" +
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ServerConfiguration actualConfig = ConfigParser.parse(stringReader);
    assertEquals(ServerConfiguration.DEFAULT_PORT, actualConfig.getServerPort());
    assertEquals(ServerConfiguration.DEFAULT_THREAD_POOL_CONFIGURATION, actualConfig.getThreadPoolConfiguration());
  }
  
  public void testTooSmallPortValue() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>12</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='SECONDS'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    try{
      ConfigParser.parse(stringReader);
      fail();
    } catch (IncorrectConfigurationValueException e) {
      //ok
    }
  }
  
  public void testEmptyPortValue() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port></port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='SECONDS'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    try{
      ConfigParser.parse(stringReader);
      fail();
    } catch (IncorrectConfigurationValueException e) {
      //ok
    }
  }
  
  public void testTooBigPortValue() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>65536</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='SECONDS'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    try{
      ConfigParser.parse(stringReader);
      fail();
    } catch (IncorrectConfigurationValueException e) {
      //ok
    }
  }
  
  public void testPoolConfigurationIsNotFound() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>6553</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" +
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='SECONDS'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ConfigParser.parse(stringReader);
  }
  
  public void testParseFile() throws Exception {
    File configFile = new File("resources/server_cfg.xml");
    assertTrue("File '" + configFile.getAbsolutePath() + "' is not found!", configFile.exists());
    ServerConfiguration actualConfig = ConfigParser.parse(configFile);
    ServerConfiguration expectedConfig = new ServerConfiguration();
    expectedConfig.setServerPort(4126);
    ThreadPoolConfiguration expected = expectedConfig.getThreadPoolConfiguration();
    expected.setCorePoolSize(10);
    expected.setMaxPoolSize(10);
    expected.setKeepAliveTime(10);
    expected.setTimeUnit(TimeUnit.SECONDS);
    assertEquals(expectedConfig, actualConfig);
  }
  
  public void testTimeAttributeIsNotFound() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    try{
      ConfigParser.parse(stringReader);
      fail();
    } catch (XmlParsingException e) {
      //ok
    }
  }
  
  public void testTimeAttributeIsIncorrect() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='xxx'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    try{
      ConfigParser.parse(stringReader);
      fail();
    } catch (IncorrectConfigurationValueException e) {
      //ok
    }
  }
  
  public void testTimeAttributeIsEmpty() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype=''>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    try{
      ConfigParser.parse(stringReader);
      fail();
    } catch (XmlParsingException e) {
      //ok
    }
  }
  
  public void testTimeAttributeIsDays() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='DAYS'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ServerConfiguration actualConfig = ConfigParser.parse(stringReader);
    assertEquals(TimeUnit.DAYS, actualConfig.getThreadPoolConfiguration().getTimeUnit());
  }
  
  public void testTimeAttributeIsHours() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='HOURS'>10</keepAlive>" + 
        "  </threadPool>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ServerConfiguration actualConfig = ConfigParser.parse(stringReader);
    assertEquals(TimeUnit.HOURS, actualConfig.getThreadPoolConfiguration().getTimeUnit());
  }
  
  public void testModulesTag() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='DAYS'>10</keepAlive>" + 
        "  </threadPool>" +
        "  <modules/>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ServerConfiguration actualConfig = ConfigParser.parse(stringReader);
    assertEquals(0, actualConfig.getModules().getModules().size());
  }
  
  public void testModulesTagNonEmpty() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='DAYS'>10</keepAlive>" + 
        "  </threadPool>" +
        "  <modules>" +
        "    <module class='" + DummyRequestExecutor.class.getCanonicalName() + "'/>" +
        "  </modules>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    ServerConfiguration actualConfig = ConfigParser.parse(stringReader);
    assertEquals(1, actualConfig.getModules().getModules().size());
    assertEquals(DummyRequestExecutor.class, actualConfig.getModules().getModules().iterator().next().getClass());
  }
  
  public void testModuleCannotBeLoaded() throws Exception {
    String configXml = 
        "<checkServiceConfig>" + 
        "  <port>4126</port>" + 
        "  <threadPool>" + 
        "    <coreSize>10</coreSize>" + 
        "    <maxSize>10</maxSize>" + 
        "    <keepAlive timetype='DAYS'>10</keepAlive>" + 
        "  </threadPool>" +
        "  <modules>" +
        "    <module class='fake'/>" +
        "  </modules>" + 
        "</checkServiceConfig>";
    StringReader stringReader = new StringReader(configXml);
    try{
      ConfigParser.parse(stringReader);
      fail();
    } catch (IncorrectConfigurationValueException e) {
      //OK
    }
    
  }
}
