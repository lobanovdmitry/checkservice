package com.wbem.checker.api.exceptions;

public class SourceIsNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public SourceIsNotFoundException(String targetType, String parameterName, String purpose) {
        super(buildMessage(targetType, parameterName, purpose));
    }

    private static String buildMessage(String targetType, String parameterName, String purpose) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Impossible to ").append(purpose).append(" because: ");
        if (parameterName != null) {
            buffer.append("Property '").append(parameterName).append("' of ").append(targetType).append("'s instance is not found!");
        } else {
            buffer.append("Instances of '").append(targetType).append("' class are not found!");
        }
        return buffer.toString();
    }
}
