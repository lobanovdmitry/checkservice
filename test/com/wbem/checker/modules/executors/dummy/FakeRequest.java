package com.wbem.checker.modules.executors.dummy;

import com.wbem.checker.api.data.Request;

public class FakeRequest implements Request {

    @Override
    public String getHostname() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getParameterValue(String parameterName) {
        return null;
    }
}
