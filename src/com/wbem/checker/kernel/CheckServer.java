package com.wbem.checker.kernel;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import com.wbem.checker.kernel.configure.ConfigParser;
import com.wbem.checker.kernel.configure.ServerConfiguration;
import com.wbem.checker.kernel.configure.exceptions.XmlParsingException;

public class CheckServer {
  private ServerSocket serverSocket;
  private RequestsHolder requestsHolder;
  private ThreadPoolExecutor executor;
  
  public CheckServer(ServerConfiguration cfg) {
    executor = new ThreadPoolExecutor(
        cfg.getThreadPoolConfiguration().getCorePoolSize(),
        cfg.getThreadPoolConfiguration().getMaxPoolSize(),
        cfg.getThreadPoolConfiguration().getKeepAliveTime(),
        cfg.getThreadPoolConfiguration().getTimeUnit(), 
        new ArrayBlockingQueue<Runnable>(100));
    requestsHolder = new RequestsHolder(cfg);
    connect(cfg.getServerPort());
  }

  public void start() {
    while(true){
      try {
        Socket socket = serverSocket.accept();
        executor.execute(new QueryProcessTask(socket.getInputStream(), socket.getOutputStream(), requestsHolder));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String... args) {
    ServerConfiguration cfg = processArgs(args);
    if (cfg == null) {
      printUsage();
      return;
    }
    new CheckServer(cfg).start();
  }
  
  void shutdown() {
      executor.shutdown();
  }
  
  private static void printUsage() {
    System.out.println("Usage: java com.wbem.checker.CheckServer [--config|-c config_file.xml]");
  }
  
  private static ServerConfiguration processArgs(String[] args) {
    if ( args.length != 2 || !( "-c".equals(args[0]) || "--config".equals(args[0]))) {
      return null;
    }
    String configFilePath = args[1];
    try {
      return ConfigParser.parse(new File(configFilePath));
    } catch (XmlParsingException e) {
      System.err.println("File '" + configFilePath + "' can not be parsed");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private void connect(int port) {
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.out.println("Port " + port + " is already in use.");
      System.exit(-1);
    }
  }
}
