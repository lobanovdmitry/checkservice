package com.wbem.checker.kernel.configure;

import java.util.concurrent.TimeUnit;

import com.wbem.checker.kernel.configure.ServerConfiguration.ThreadPoolConfiguration;

import junit.framework.TestCase;

public class ThreadPoolConfigurationTest extends TestCase {
    
    public void testEqualsAndHashCode() throws Exception {
        ThreadPoolConfiguration eq0 = new ThreadPoolConfiguration();
        ThreadPoolConfiguration eq1 = new ThreadPoolConfiguration();
        ThreadPoolConfiguration nonEq = new ThreadPoolConfiguration(-1, -1, -1, TimeUnit.DAYS);
        ThreadPoolConfiguration withNull = new ThreadPoolConfiguration();
        withNull.setTimeUnit(null);
        
        assertTrue(eq0.equals(eq0));
        assertTrue(eq0.equals(eq1));
        assertFalse(eq0.equals(nonEq));
        assertFalse(eq0.equals(withNull));
        assertFalse(withNull.equals(eq0));
        assertTrue(withNull.equals(withNull));
        
        ThreadPoolConfiguration withNull2 = new ThreadPoolConfiguration();
        withNull2.setTimeUnit(null);
        assertTrue(withNull.equals(withNull2));
        
        assertFalse(eq0.equals(null));
        assertFalse(eq0.equals(""));
        
        assertTrue(eq0.hashCode() == eq1.hashCode());
        assertTrue(nonEq.hashCode() != eq0.hashCode());
        assertTrue(nonEq.hashCode() != withNull.hashCode());
        
        eq0.setCorePoolSize(1);
        eq1.setCorePoolSize(1);
        nonEq.setCorePoolSize(-1);
        
        assertTrue(eq0.equals(eq1));
        assertFalse(eq0.equals(nonEq));
        
        eq0.setMaxPoolSize(1);
        eq1.setMaxPoolSize(1);
        nonEq.setCorePoolSize(1);
        nonEq.setMaxPoolSize(-1);
        assertTrue(eq0.equals(eq1));
        assertFalse(eq0.equals(nonEq));
        
        eq0.setKeepAliveTime(1);
        eq1.setKeepAliveTime(1);
        nonEq.setMaxPoolSize(1);
        nonEq.setKeepAliveTime(-1);
        assertTrue(eq0.equals(eq1));
        assertFalse(eq0.equals(nonEq));
        
        eq0.setTimeUnit(TimeUnit.DAYS);
        eq1.setTimeUnit(TimeUnit.DAYS);
        nonEq.setKeepAliveTime(1);
        nonEq.setTimeUnit(TimeUnit.MINUTES);
        assertTrue(eq0.equals(eq1));
        assertFalse(eq0.equals(nonEq));
    }

}
