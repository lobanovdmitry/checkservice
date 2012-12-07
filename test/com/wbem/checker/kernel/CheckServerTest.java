package com.wbem.checker.kernel;

import junit.framework.TestCase;

import com.wbem.checker.kernel.configure.ServerConfiguration;

public class CheckServerTest extends TestCase {

    public void testStartCheckServerWithBedArgs() throws Exception {
        CheckServer.main();
        CheckServer.main("FAKE", "FAKE");
        CheckServer.main("-c");
        CheckServer.main("-c", "file.xml");
        CheckServer.main("--config");
        CheckServer.main("--config", "file.xml");
        CheckServer.main("--config", "resources/empty.xml");
    }

    public void testStartCheckServer() throws Exception {
        ServerConfiguration configuration = new ServerConfiguration();
        new CheckServer(configuration);
    }

}
