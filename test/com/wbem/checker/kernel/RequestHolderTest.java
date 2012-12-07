package com.wbem.checker.kernel;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.wbem.checker.api.DummyRequestExecutor;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.exceptions.ParameterNotFoundException;
import com.wbem.checker.api.exceptions.UnknownRequestTypeException;
import com.wbem.checker.kernel.configure.ServerConfiguration;
import com.wbem.checker.kernel.configure.ServerConfiguration.Modules;

public class RequestHolderTest extends TestCase {

    private ServerConfiguration configuration = new ServerConfiguration();

    @Override
    protected void setUp() throws Exception {
        Modules modules = new Modules();
        modules.addModule(new DummyExecutor());
        configuration.setModules(modules);
    }

    public void testUnknownRequestTypeException() throws Exception {
        RequestsHolder requestsHolder = new RequestsHolder(new ServerConfiguration());
        try {
            requestsHolder.getExecutor("unknown", null);
            fail();
        } catch (UnknownRequestTypeException e) {
            // OK
        }
    }

    public void testGetRequestExecutor() throws Exception {
        RequestsHolder requestsHolder = new RequestsHolder(configuration);
        requestsHolder.getExecutor("dummy", createRequest("username", "pass", "host", null));
    }

    public void testUnableToLoadExecutor() throws Exception {
        configuration.getModules().addModule(new InvalidExecutor("invalid"));
        try {
            RequestsHolder requestsHolder = new RequestsHolder(configuration);
            requestsHolder.getExecutor("invalid", null);
            fail();
        } catch (InvalidParameterException e) {
            // OK
        }
    }

    public void testUsernameIsNotFound() throws Exception {
        try {
            RequestsHolder requestsHolder = new RequestsHolder(configuration);
            requestsHolder.getExecutor("dummy", createRequest(null, "pass", "host", null));
            fail();
        } catch (ParameterNotFoundException e) {
            assertEquals("Required parameters are not found: 'username(-u)'", e.getMessage());
        }
    }

    public void testPasswordIsNotFound() throws Exception {
        try {
            RequestsHolder requestsHolder = new RequestsHolder(configuration);
            requestsHolder.getExecutor("dummy", createRequest("user", null, "host", null));
            fail();
        } catch (ParameterNotFoundException e) {
            assertEquals("Required parameters are not found: 'password(-p)'", e.getMessage());
        }
    }

    public void testHostIsNotFound() throws Exception {
        try {
            RequestsHolder requestsHolder = new RequestsHolder(configuration);
            requestsHolder.getExecutor("dummy", createRequest("user", "pass", null, null));
            fail();
        } catch (ParameterNotFoundException e) {
            assertEquals("Required parameters are not found: 'hostname(-h)'", e.getMessage());
        }
    }

    public void testIsnotAuthentificatedExecutor() throws Exception {
        configuration.getModules().addModule(new NonAuthExecutor());
        RequestsHolder requestsHolder = new RequestsHolder(configuration);
        Map<String, String> params = new HashMap<String, String>();
        params.put("a", "a");
        params.put("b", "b");
        params.put("c", "c");
        requestsHolder.getExecutor("nonauth", createRequest(null, null, "host", params));
    }
    
    public void testRequiredParametersAreNotFound() throws Exception {
        configuration.getModules().addModule(new NonAuthExecutor());
        RequestsHolder requestsHolder = new RequestsHolder(configuration);
        Map<String, String> params = new HashMap<String, String>();
        params.put("a", "a");
        try {
            requestsHolder.getExecutor("nonauth", createRequest(null, null, "host", params));
            fail();
        } catch (ParameterNotFoundException e) {
            assertEquals("Required parameters are not found: 'b', 'c'", e.getMessage());
        }
    }

    private Request createRequest(final String username, final String pass, final String host, final Map<String, String> params) {
        return new Request() {

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return pass;
            }

            @Override
            public String getParameterValue(String parameterName) {
                return params != null ? params.get(parameterName) : null;
            }

            @Override
            public String getHostname() {
                return host;
            }
        };
    }

    private static class InvalidExecutor extends DummyRequestExecutor {

        public InvalidExecutor(String parameter) {
            // non default constructor
        }

        @Override
        public String getRequestType() {
            return "invalid";
        }
    }

    private static class DummyExecutor extends DummyRequestExecutor {
        public DummyExecutor() {
            // nothing to do
        }

        @Override
        public String getRequestType() {
            return "dummy";
        }

        @Override
        public String[] getRequiredParameters() {
            return new String[0];
        }
    }

    private static class NonAuthExecutor extends DummyRequestExecutor {

        public NonAuthExecutor() {
            // nothing to do
        }

        @Override
        public String getRequestType() {
            return "nonauth";
        }

        public boolean isAuthentificateNeeded() {
            return false;
        };

        public String[] getRequiredParameters() {
            return new String[] { "a", "b", "c" };
        };
    };

}
