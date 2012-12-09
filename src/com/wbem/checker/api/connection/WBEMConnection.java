package com.wbem.checker.api.connection;

import javax.cim.CIMInstance;
import javax.wbem.WBEMException;

public interface WBEMConnection {

  public Object[] getPropertyValuesOfAllInstances(String path, String property) throws WBEMException;

  public boolean isInstanceExist(String path, String property, Object value) throws WBEMException;

  public CIMInstance[] getAllInstances(String path) throws WBEMException;
}
