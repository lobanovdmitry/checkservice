package com.wbem.checker.kernel.configure;

import com.wbem.checker.kernel.configure.ServerConfiguration.Modules;

import junit.framework.TestCase;

public class ModulesTest extends TestCase {

    public void testEqualsAndHashCode() {

        Modules modulesEq1 = new Modules();
        Modules modulesEq2 = new Modules();
        Modules nonEq = new Modules();
        nonEq.addModule(null);

        assertFalse(modulesEq1.equals(null));
        assertFalse(modulesEq1.equals(""));
        
        assertFalse(modulesEq1.equals(nonEq));
        assertTrue(modulesEq1.equals(modulesEq1));
        assertTrue(modulesEq1.equals(modulesEq2));
        
    }
}
