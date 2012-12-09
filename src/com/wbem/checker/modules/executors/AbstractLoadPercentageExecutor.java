package com.wbem.checker.modules.executors;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.api.exceptions.InvalidParametersException;

public abstract class AbstractLoadPercentageExecutor implements RequestExecutor {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    private int warningValue;
    private int criticalValue;

    protected abstract String getMessage();

    protected Response createResponse(int loadValue) {
        Response response = new Response();
        response.setRequestCorrect(ResponseConstants.REQUEST_STATUS_OK);
        if (loadValue < warningValue) {
            response.setCheckSeverity(ResponseConstants.SEVERITY_OK);
            response.setMessage(getOkMessage(loadValue));
            return response;
        }
        if (loadValue >= warningValue && loadValue < criticalValue) {
            response.setCheckSeverity(ResponseConstants.SEVERITY_WARNING);
            response.setMessage(getWarningMessage(loadValue));
            return response;
        }
        response.setCheckSeverity(ResponseConstants.SEVERITY_CRITICAL);
        response.setMessage(getAlarmMessage(loadValue));
        return response;
    }

    @Override
    public String[] getRequiredParameters() {
        return new String[] { "-w", "-c" };
    }

    @Override
    public void validateRequest(Request request) throws InvalidParametersException {
        String waringPercentage = request.getParameterValue("-w");
        String criticalPercentage = request.getParameterValue("-c");
        if (waringPercentage.endsWith("%")) {
            waringPercentage = waringPercentage.substring(0, waringPercentage.length() - 1);
        }
        if (criticalPercentage.endsWith("%")) {
            criticalPercentage = criticalPercentage.substring(0, criticalPercentage.length() - 1);
        }
        warningValue = parseInt(waringPercentage);
        criticalValue = parseInt(criticalPercentage);
        if (warningValue < MIN_VALUE || warningValue > MAX_VALUE) {
            throw new InvalidParametersException("Warning percentage value must be integer from " + MIN_VALUE + " to " + MAX_VALUE + "!");
        }
        if (criticalValue < MIN_VALUE || criticalValue > MAX_VALUE || criticalValue <= warningValue) {
            throw new InvalidParametersException("Critical percentage value must be integer from " + MIN_VALUE + " to " + MAX_VALUE + " and bigger than Warning percentage!");
        }
    }
    
    private int parseInt(String value) throws InvalidParametersException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidParametersException("'" + value + "' can not be parsed as Integer!");
        }
        
    }

    private String getOkMessage(int loading) {
        return getMessage() + loading + "%";
    }

    private String getWarningMessage(int loading) {
        return "WARNING: " + getMessage() + loading + "%";
    }

    private String getAlarmMessage(int loading) {
        return "ALARM: " + getMessage() + loading + "%";
    }
}
