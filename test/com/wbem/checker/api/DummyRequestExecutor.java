package com.wbem.checker.api;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.exceptions.InvalidParametersException;

public class DummyRequestExecutor implements RequestExecutor {

	private String requestType;
	private String[] requiredParameters;
	private boolean isAuthNeeded = true;
	private RequestValidator requestValidator;
	private Response response;
	
	public DummyRequestExecutor() {
	    requestType = "dummy";
    }
	
	public DummyRequestExecutor(String requestType, String[] requiredParameters, boolean isAuthNeeded, RequestValidator requestValidator, Response expectedResponse) {
		this.requestType = requestType;
		this.requiredParameters = requiredParameters;
		this.isAuthNeeded = isAuthNeeded;
		this.requestValidator = requestValidator;
		response = expectedResponse;
	}
	
	@Override
	public void validateRequest(Request request) throws InvalidParametersException {
		if ( requestValidator != null ) {
		    requestValidator.validate(request);
		}
	}

	@Override
	public Response execute(WBEMConnection connection) throws Exception {
		return response;
	}

	@Override
	public String[] getRequiredParameters() {
		return requiredParameters;
	}

	@Override
	public String getRequestType() {
		return requestType;
	}

	@Override
	public boolean isAuthentificateNeeded() {
		return isAuthNeeded;
	}

	public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
	
	public void setRequestValidator(RequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }
	
	public void setRequiredParameters(String[] requiredParameters) {
        this.requiredParameters = requiredParameters;
    }
	
	public void setResponse(Response response) {
        this.response = response;
    }
	
	public void setAuthNeeded(boolean isAuthNeeded) {
        this.isAuthNeeded = isAuthNeeded;
    }

    public interface RequestValidator {
		
		void validate(Request request) throws InvalidParametersException;
	}
}
