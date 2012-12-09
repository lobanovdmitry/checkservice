package com.wbem.checker.modules.executors.dummy;

import javax.cim.CIMInstance;
import javax.wbem.WBEMException;

import com.wbem.checker.api.connection.WBEMConnection;

public class FakeWBEMConnection implements WBEMConnection {

    @Override
    public Object[] getPropertyValuesOfAllInstances(String path, String property) throws WBEMException {
        return null;
    }

    @Override
    public boolean isInstanceExist(String path, String property, Object value) throws WBEMException {
        return false;
    }

    @Override
    public CIMInstance[] getAllInstances(String path) throws WBEMException {
        return null;
    }

}
