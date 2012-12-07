package com.wbem.checker.kernel.configure;

import java.util.concurrent.TimeUnit;

import com.wbem.checker.api.DummyRequestExecutor;
import com.wbem.checker.kernel.configure.ServerConfiguration.Modules;
import com.wbem.checker.kernel.configure.ServerConfiguration.ThreadPoolConfiguration;

import junit.framework.TestCase;

public class ServerConfigurationTest extends TestCase {

    public void testEqualsAndHashCode() throws Exception {
        ServerConfiguration cfgEq0 = new ServerConfiguration();
        ServerConfiguration cfgEq1 = new ServerConfiguration();
        ServerConfiguration noneq = new ServerConfiguration();
        ServerConfiguration withNullEq0 = new ServerConfiguration();
        withNullEq0.setModules(null);
        withNullEq0.setThreadPoolConfiguration(null);
        ServerConfiguration withNullEq1 = new ServerConfiguration();
        withNullEq1.setModules(null);
        withNullEq1.setThreadPoolConfiguration(null);
        
        
        assertTrue(cfgEq0.equals(cfgEq0));
        assertTrue(cfgEq0.equals(cfgEq1));
        assertFalse(cfgEq0.equals(null));
        assertFalse(cfgEq0.equals(new Object()));
        
        cfgEq0.setServerPort(1000);
        cfgEq1.setServerPort(1000);
        noneq.setServerPort(1001);
        checkEqualAndHashCode(cfgEq0, cfgEq1, noneq, withNullEq1, withNullEq0);
        
        cfgEq0.setThreadPoolConfiguration(new ThreadPoolConfiguration());
        cfgEq1.setThreadPoolConfiguration(new ThreadPoolConfiguration());
        noneq.setThreadPoolConfiguration(new ThreadPoolConfiguration(1, 10, 1, TimeUnit.HOURS));
        withNullEq0.setServerPort(1000);
        withNullEq1.setServerPort(1000);
        checkEqualAndHashCode(cfgEq0, cfgEq1, noneq, withNullEq1, withNullEq0);
        
        cfgEq0.setModules(new Modules());
        cfgEq1.setModules(new Modules());
        Modules modules = new Modules();
        modules.addModule(new DummyRequestExecutor());
        noneq.setModules(modules);
        withNullEq0.setThreadPoolConfiguration(new ThreadPoolConfiguration());
        withNullEq1.setThreadPoolConfiguration(new ThreadPoolConfiguration());
        checkEqualAndHashCode(cfgEq0, cfgEq1, noneq, withNullEq1, withNullEq0);
    }

    private void checkEqualAndHashCode(ServerConfiguration cfgEq0, ServerConfiguration cfgEq1, ServerConfiguration noneq, ServerConfiguration withNullEq0, ServerConfiguration withNullEq1) {
        assertTrue(cfgEq0.equals(cfgEq1));
        assertTrue(withNullEq0.equals(withNullEq1));
        assertFalse(cfgEq0.equals(noneq));
        assertFalse(noneq.equals(cfgEq0));
        assertFalse(withNullEq0.equals(cfgEq0));
        assertFalse(cfgEq0.equals(noneq));
        assertFalse(cfgEq0.equals(withNullEq0));

        assertTrue(cfgEq0.hashCode() == cfgEq1.hashCode());
        assertTrue(cfgEq0.hashCode() != noneq.hashCode());
        assertTrue(withNullEq1.hashCode() == withNullEq0.hashCode());
    }

}
