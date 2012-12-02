package com.wbem.checker.kernel.configure;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.kernel.configure.exceptions.IncorrectConfigurationValueException;
import com.wbem.checker.kernel.configure.exceptions.XmlParsingException;

public class ConfigParser {
  private static final String DEFAULT_WILL_BE_USED = " parameter is not found, default value will be used!";

  private static final String ROOT_TAG = "checkServiceConfig";
  private static final String PORT_TAG = "port";
  private static final String POOL_TAG = "threadPool";
  private static final String CORE_POOL_SIZE_TAG = "coreSize";
  private static final String MAX_POOL_SIZE_TAG = "maxSize";
  private static final String KEEP_ALIVE_TAG = "keepAlive";
  private static final String TIME_TYPE_ATTR = "timetype";
  private static final String MODULES_TAG = "modules";
  private static final String MODULE_TAG = "module";
  private static final String CLASS_ATTR = "class";

  public static ServerConfiguration parse(File file) throws XmlParsingException, IOException {
    return parse(XmlDomParser.ParserUtils.getBufferedFileUTF8Reader(file));
  }

  public static ServerConfiguration parse(Reader reader) throws XmlParsingException, IOException {
    ServerConfiguration cfg = new ServerConfiguration();
    Element root = XmlDomParser.parse(reader, ROOT_TAG);
    parsePort(root, cfg);
    parsePoolConfiguration(root, cfg);
    parseModules(root, cfg);
    log(cfg.toString());
    return cfg;
  }

  private static void parsePort(Element root, ServerConfiguration cfg) throws XmlParsingException {
    Element portElement = XmlDomParser.getSingleChild(root, PORT_TAG);
    if ( portElement == null ) {
      log("'Port'" + DEFAULT_WILL_BE_USED);
      return;
    }
    cfg.setServerPort(getIntValue(PORT_TAG, XmlDomParser.getNodeValue(portElement), 1000, 65525));
  }

  private static void parsePoolConfiguration(Element root, ServerConfiguration cfg) throws XmlParsingException {
    Element poolTag = XmlDomParser.getSingleChild(root, POOL_TAG);
    if ( poolTag == null ) {
      log("'Thread pool'" + DEFAULT_WILL_BE_USED);
      return;
    }
    Element corePoolTag = XmlDomParser.getSingleChild(poolTag, CORE_POOL_SIZE_TAG);
    if ( corePoolTag == null ) {
      log("'Core pool size'" + DEFAULT_WILL_BE_USED);
    } else {
      cfg.getThreadPoolConfiguration().setCorePoolSize(getIntValue(CORE_POOL_SIZE_TAG, XmlDomParser.getNodeValue(corePoolTag), 0, 100));
    }
    Element maxPoolTag = XmlDomParser.getSingleChild(poolTag, MAX_POOL_SIZE_TAG);
    if ( maxPoolTag == null ) {
      log("'Max pool size'" + DEFAULT_WILL_BE_USED);
    } else {
      cfg.getThreadPoolConfiguration().setMaxPoolSize(getIntValue(MAX_POOL_SIZE_TAG, XmlDomParser.getNodeValue(maxPoolTag), 0, 100));
    }
    Element keepAliveElement = XmlDomParser.getSingleChild(poolTag, KEEP_ALIVE_TAG);
    if ( keepAliveElement == null ) {
      log("'Keep alive time'" + DEFAULT_WILL_BE_USED);
    } else {
      cfg.getThreadPoolConfiguration().setKeepAliveTime(getIntValue(KEEP_ALIVE_TAG, XmlDomParser.getNodeValue(keepAliveElement), 0, Integer.MAX_VALUE));
      cfg.getThreadPoolConfiguration().setTimeUnit(getTimeUnit(XmlDomParser.getMandatoryAttribute(keepAliveElement, TIME_TYPE_ATTR)));
    }
  }

  private static void parseModules(Element root, ServerConfiguration cfg) throws XmlParsingException {
    Element modules = XmlDomParser.getSingleChild(root, MODULES_TAG);
    if ( modules == null ) {
      log("Modules are not detected in config");
      return;
    }
    NodeList moduleList = XmlDomParser.getChildrenWithName(modules, MODULE_TAG);
    for ( int i = 0; i < moduleList.getLength(); i++ ) {
      String className = XmlDomParser.getMandatoryAttribute((Element)moduleList.item(i), CLASS_ATTR);
      cfg.getModules().addModule(checkClass(className));
    }
  }

  private static int getIntValue(String tagName, String value, int min, int max) throws IncorrectConfigurationValueException {
    try {
      int intValue = Integer.valueOf(value);
      if (intValue < min || intValue > max) {
        throw new IncorrectConfigurationValueException("'" + tagName + "' value must be [" + min + "..." + max + "]");
      }
      return intValue;
    } catch (NumberFormatException e) {
      throw new IncorrectConfigurationValueException("'" + tagName + "' value can not be parsed as Integer!");
    }
  }
  
  private static TimeUnit getTimeUnit(String timeUnitValue) throws IncorrectConfigurationValueException {
    /*
     * SECONDS; MINUTES; HOURS; DAYS;
     */
    TimeUnit timeUnit = null;
    if ("DAYS".equals(timeUnitValue)) {
      timeUnit = TimeUnit.DAYS;
    }
    if ("HOURS".equals(timeUnitValue)) {
      timeUnit = TimeUnit.HOURS;
    }
    if ("MINUTES".equals(timeUnitValue)) {
      timeUnit = TimeUnit.MINUTES;
    }
    if ("SECONDS".equals(timeUnitValue)) {
      timeUnit = TimeUnit.SECONDS;
    }
    if ( timeUnit == null) {
      throw new IncorrectConfigurationValueException("'" + TIME_TYPE_ATTR + "' attribute value cannot be parsed!" );
    }
    return timeUnit;
  }

  private static void log(String message) {
    System.out.println(message);
  }
  
  private static RequestExecutor checkClass(String className) throws IncorrectConfigurationValueException {
    try {
      @SuppressWarnings("unchecked")
      Class<? extends RequestExecutor> executorImpl = (Class<? extends RequestExecutor>) ClassLoader.getSystemClassLoader().loadClass(className);
      Constructor<? extends RequestExecutor> constructor = executorImpl.getConstructor();
      return constructor.newInstance();
    } catch (Exception e) {
      throw new IncorrectConfigurationValueException("Unable to load '" + className + "' executor");
    }
  }
}
