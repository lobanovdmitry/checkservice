package com.wbem.checker.kernel;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wbem.checker.api.RequestExecutor;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.exceptions.InvalidParametersException;
import com.wbem.checker.api.exceptions.ParameterNotFoundException;
import com.wbem.checker.api.exceptions.UnknownRequestTypeException;
import com.wbem.checker.kernel.configure.ServerConfiguration;

public class RequestsHolder {

  private Map<String, Class<? extends RequestExecutor>> executors = new HashMap<String, Class<? extends RequestExecutor>>();

  public RequestsHolder(ServerConfiguration cfg) {
    loadExecutors(cfg);
  }

  public RequestExecutor getExecutor(String type, Request request) throws Exception {
    Class<? extends RequestExecutor> executor = executors.get(type);
    if (executor == null) {
      throw new UnknownRequestTypeException(type);
    }
    RequestExecutor requestExecutor;
    try {
      Constructor<? extends RequestExecutor> constructor = executor.getConstructor();
      requestExecutor = constructor.newInstance();
    } catch (Exception e) {
      throw new InvalidParameterException("Unable to load " + executor.getCanonicalName());
    }
    validateRequest(requestExecutor, request);
    return requestExecutor;
  }

  private void validateRequest(RequestExecutor executor, Request request) throws InvalidParametersException {
    if (request.getHostname() == null) {
      throw new ParameterNotFoundException("hostname(-h)");
    }
    if (executor.isAuthentificateNeeded()) {
      if (request.getUsername() == null) {
        throw new ParameterNotFoundException("username(-u)");
      }
      if (request.getPassword() == null) {
        throw new ParameterNotFoundException("password(-p)");
      }
    }
    List<String> params = new ArrayList<String>(0);
    for (String requiredParam : executor.getRequiredParameters()) {
      if (request.getParameterValue(requiredParam) == null) {
        params.add(requiredParam);
      }
    }
    if (params.size() != 0) {
      throw new ParameterNotFoundException(params.toArray(new String[params.size()]));
    }
    executor.validateRequest(request);
  }

  private void loadExecutors(ServerConfiguration cfg) {
    for (RequestExecutor executor : cfg.getModules().getModules()) {
      executors.put(executor.getRequestType().toLowerCase(), executor.getClass());
    }
  }
}
