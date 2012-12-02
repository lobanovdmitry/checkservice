package com.wbem.checker.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.data.ResponseConstants;
import com.wbem.checker.kernel.impls.RequestImpl;
import com.wbem.checker.kernel.protobuf.proxy.ProxyUtils;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckRequest;
import com.wbem.checker.kernel.protobuf.proxy.WbemCheckProxy.CheckResponse;

public class QueryProcessTask implements Runnable {

  private Socket socket;
  private RequestsHolder requestsHolder;
  
  public QueryProcessTask(Socket socket, RequestsHolder requestsHolder) {
    this.socket = socket;
    this.requestsHolder = requestsHolder;
  }
  
  @Override
  public void run() {
    try {
      InputStream inputStream = socket.getInputStream();
      CheckRequest request = CheckRequest.parseDelimitedFrom(inputStream);
      CheckResponse checkResponse = ProxyUtils.createCheckResponse(processRequest(request));
      checkResponse.writeDelimitedTo(socket.getOutputStream());
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Response processRequest(CheckRequest request) {
    try {
      WBEMConnection connection = ConnectionPool.findOrCreateConnection(ProxyUtils.getHostAndCredentianls(request));
      return requestsHolder.getExecutor(request.getType(), new RequestImpl(request)).execute(connection);
    } catch (Exception e) {
      return createErrorResponse(e); 
    }
  }

  private Response createErrorResponse(Exception e) {
    Response response = new Response();
    response.setCheckSeverity(ResponseConstants.SEVERITY_CRITICAL);
    String message = e.getLocalizedMessage();
    response.setMessage( message == null ? e.getCause().getMessage() : message);
    response.setRequestCorrect(ResponseConstants.REQUEST_STATUS_KO);
    return response;
  }
}
