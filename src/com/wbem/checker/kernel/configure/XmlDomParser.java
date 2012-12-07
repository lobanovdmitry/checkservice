package com.wbem.checker.kernel.configure;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.wbem.checker.kernel.configure.exceptions.IncorrectRootTagException;
import com.wbem.checker.kernel.configure.exceptions.SingleChildExpectedException;
import com.wbem.checker.kernel.configure.exceptions.XmlParsingException;

public class XmlDomParser {

  private static DOMParser domParser = new DOMParser();

  public static Element parse(File file, String expectedRootTag) throws XmlParsingException, IOException {
    if ( !file.exists() ) {
      throw new IOException(XmlMessages.fileNotFound(file.getPath()));
    }

    Reader reader = null;
    try {
      if ( ParserUtils.loadTextFileToString(file).length() == 0 ) {
        throw new XmlParsingException(XmlMessages.emptyDocumentFound());
      }
      reader = ParserUtils.getBufferedFileUTF8Reader(file);
      return parse(reader, expectedRootTag);
    } catch ( Exception e ) {
      throw new XmlParsingException(e);
    } finally {
    	if ( reader != null ) {
    		ParserUtils.close(reader);
    	}
    }
  }

  public static Element parse(Reader reader, String expectedRootTag) throws XmlParsingException {
    Document document = parse(reader);
    Element root = document.getDocumentElement();
    if ( !root.getTagName().equals(expectedRootTag) ) {
      throw new IncorrectRootTagException(XmlMessages.invalidRootTag(expectedRootTag, root.getTagName()));
    }
    return root;
  }

  public static Document parse(Reader reader) throws XmlParsingException {
    synchronized ( domParser ) {
      try {
        domParser.parse(new InputSource(reader));
        ParserUtils.close(reader);
        return domParser.getDocument();
      } catch ( IOException e ) {
        throw new XmlParsingException(e);
      } catch ( SAXException e ) {
        throw new XmlParsingException(e);
      } finally {
        domParser.reset();
      }
    }
  }

  public static NodeList getChildrenWithName(Node node, String name) {
    return new FilteredNodeList(node, name);
  }
  
  public static Element getSingleChild( Node node, String name) throws SingleChildExpectedException {
    NodeList list = getChildrenWithName(node, name);
    if ( list.getLength() > 1) {
      throw new SingleChildExpectedException(node, name);
    }
    return list.getLength() == 0 ? null : (Element)list.item(0);
  }

  public static String getMandatoryAttribute(Element node, String attributeName) throws XmlParsingException {
    String name = node.getAttribute(attributeName);
    if ( (name == null) || (name.length() == 0) ) {
      throw new XmlParsingException(XmlMessages.attributeNotSet(attributeName, node.getNodeName()));
    }
    return name;
  }

  public static String getOptionalAttribute(Element node, String attributeName) {
    String result = "";
    if ( node.hasAttribute(attributeName) ) {
      result = node.getAttribute(attributeName);
    }
    return result;
  }

  public static String getAttribute(Element node, String attributeName, String defaultValue) {
    try {
      return getMandatoryAttribute(node, attributeName);
    } catch ( XmlParsingException e ) {
      return defaultValue;
    }
  }

  public static String getOptionalStringAttribute(Element node, String attributeName, String defaultValue) {
    if ( node.hasAttribute(attributeName) ) {
      return node.getAttribute(attributeName);
    }
    return defaultValue;
  }

  public static int getOptionalIntAttribute(Element node, String attributeName, int defaultValue) {
    int result = defaultValue;
    if ( node.hasAttribute(attributeName) ) {
      String resultAsString = node.getAttribute(attributeName);
      try {
        result = Integer.parseInt(resultAsString);
      } catch ( Exception e ) {
        result = defaultValue;
      }
    }
    return result;
  }

  public static boolean getOptionalBooleanAttribute(Element node, String attributeName, boolean defaultValue) {
    boolean result = defaultValue;
    if ( node.hasAttribute(attributeName) ) {
      String resultAsString = node.getAttribute(attributeName);
      result = Boolean.valueOf(resultAsString);
    }
    return result;
  }

  public static Object getNodeValueAsObject(Node node) {
    if ( !node.hasChildNodes() ) {
      return "";
    }
    if ( hasChildNodes(node) ) {
      return node;
    }
    return node.getFirstChild().getNodeValue();
  }

  public static String getNodeValue(Node node) {
    if ( !node.hasChildNodes() ) {
      return "";
    }
    return node.getFirstChild().getNodeValue();
  }

  private static boolean hasChildNodes(Node node) {
    NodeList nodeList = node.getChildNodes();
    for ( int j = 0; j < nodeList.getLength(); j++ ) {
      Node childNode = nodeList.item(j);
      if ( childNode.getNodeType() == Node.ELEMENT_NODE ) {
        return true;
      }
    }
    return false;
  }

  private static class FilteredNodeList implements NodeList {
    List<Element> nodesList = new ArrayList<Element>();

    public FilteredNodeList(Node node, String childName) {
      NodeList allChildren = node.getChildNodes();
      for ( int i = 0, size = allChildren.getLength(); i < size; i++ ) {
        Node child = allChildren.item(i);
        if ( child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childName) ) {
          nodesList.add((Element)child);
        }
      }
    }

    public int getLength() {
      return nodesList.size();
    }

    public Node item(int i) {
      return nodesList.get(i);
    }
  }

  public static class XmlMessages {

    public static String fileNotFound(String path) {
      return "File is not found at " + path;
    }

    public static String invalidRootTag(String expectedRootTag, String tagName) {
      return "Invalid root tag: expected '" + expectedRootTag + "', but found '" + tagName + "'";
    }

    public static String attributeNotSet(String attributeName, String nodeName) {
      return "Attribute is not set: tag '" + nodeName + "'" + ", attribute '" + attributeName + "'";
    }

    public static String emptyDocumentFound() {
      return "Empty document found";
    }
  }

  public static class ParserUtils {

    public static void close(Reader reader) {
      try {
        reader.close();
      } catch ( IOException e ) {
        e.printStackTrace();
      }
    }

    public static String loadTextFileToString(File file) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      StringBuffer buffer = new StringBuffer();
      String line = null;
      while ( (line = reader.readLine()) != null ) {
        buffer.append(line);
      }
      reader.close();
      return buffer.toString();
    }

    public static Reader getBufferedFileUTF8Reader(File file) throws IOException {
      InputStream bufferedInputStream = getBufferedInputStream(file);
      try {
        return new InputStreamReader(bufferedInputStream, "UTF8");
      } catch ( UnsupportedEncodingException e ) {
        bufferedInputStream.close();
        throw e;
      }
    }

    public static InputStream getBufferedInputStream(File sourceFile) throws IOException {
      FileInputStream fileInputStream = new FileInputStream(sourceFile);
      return new BufferedInputStream(fileInputStream);
    }
  }
}
