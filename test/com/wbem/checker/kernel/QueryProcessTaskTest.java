package com.wbem.checker.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.kernel.configure.ServerConfiguration;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckRequest;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckResponse;
import com.wbem.checker.modules.executors.PingRequestExecutor;

public class QueryProcessTaskTest extends TestCase {

    public void testNominalCase() throws Exception {
        ServerConfiguration cfg = new ServerConfiguration();
        cfg.getModules().addModule(new PingRequestExecutor());
        RequestsHolder requestsHolder = new RequestsHolder(cfg);
        
        CheckRequest.Builder checkRequest = CheckRequest.newBuilder();
        checkRequest.setHostname("localhost");
        checkRequest.setType("ping");
        CheckRequest request = checkRequest.build();
        
        ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
        request.writeDelimitedTo(requestStream);
        
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        QueryProcessTask processTask = new QueryProcessTask(new ByteArrayInputStream(requestStream.toByteArray()), responseStream, requestsHolder);
        processTask.run();
        
        CheckResponse checkResponse = CheckResponse.parseDelimitedFrom(new ByteArrayInputStream(responseStream.toByteArray()));
        assertEquals(true, checkResponse.getIsRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_OK, checkResponse.getCheckSeverity());
        assertEquals("Host 'localhost' is reachable through 'ping'", checkResponse.getMessage());
    }

    
    public void testErrorCase() throws Exception {
        ServerConfiguration cfg = new ServerConfiguration();
        cfg.getModules().addModule(new PingRequestExecutor());
        RequestsHolder requestsHolder = new RequestsHolder(cfg);
        
        CheckRequest.Builder checkRequest = CheckRequest.newBuilder();
        checkRequest.setHostname("unknownserver");
        checkRequest.setType("ping");
        CheckRequest request = checkRequest.build();
        
        ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
        request.writeDelimitedTo(requestStream);
        
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        QueryProcessTask processTask = new QueryProcessTask(new ByteArrayInputStream(requestStream.toByteArray()), responseStream, requestsHolder);
        processTask.run();
        
        CheckResponse checkResponse = CheckResponse.parseDelimitedFrom(new ByteArrayInputStream(responseStream.toByteArray()));
        assertEquals(true, checkResponse.getIsRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_UNKNOWN, checkResponse.getCheckSeverity());
        assertEquals("Unknown host: 'unknownserver'", checkResponse.getMessage());
    }
    
    public void testIncorrectRequest() throws Exception {
        ServerConfiguration cfg = new ServerConfiguration();
        cfg.getModules().addModule(new PingRequestExecutor());
        RequestsHolder requestsHolder = new RequestsHolder(cfg);
        
        CheckRequest.Builder checkRequest = CheckRequest.newBuilder();
        checkRequest.setHostname("localhost");
        checkRequest.setType("fake");
        CheckRequest request = checkRequest.build();
        
        ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
        request.writeDelimitedTo(requestStream);
        
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        QueryProcessTask processTask = new QueryProcessTask(new ByteArrayInputStream(requestStream.toByteArray()), responseStream, requestsHolder);
        processTask.run();
        
        CheckResponse checkResponse = CheckResponse.parseDelimitedFrom(new ByteArrayInputStream(responseStream.toByteArray()));
        assertEquals(false, checkResponse.getIsRequestCorrect());
        assertEquals(ResponseConstants.SEVERITY_CRITICAL, checkResponse.getCheckSeverity());
        assertEquals("Type 'fake' of the request is unknown!", checkResponse.getMessage());
    }
}
