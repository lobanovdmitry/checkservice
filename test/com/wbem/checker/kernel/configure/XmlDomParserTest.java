package com.wbem.checker.kernel.configure;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.w3c.dom.Element;

import com.wbem.checker.kernel.configure.XmlDomParser.ParserUtils;
import com.wbem.checker.kernel.configure.XmlDomParser.XmlMessages;
import com.wbem.checker.kernel.configure.exceptions.SingleChildExpectedException;
import com.wbem.checker.kernel.configure.exceptions.XmlParsingException;

public class XmlDomParserTest extends TestCase {

    public static final File resourcesDir = new File("resources");

    public void testFileNotFoundException() throws Exception {
        new XmlDomParser();
        new XmlDomParser.XmlMessages();
        new XmlDomParser.ParserUtils();

        File file = new File("fake.xml");
        try {
            XmlDomParser.parse(file, "root");
            fail();
        } catch (IOException e) {
            assertTrue(e.getMessage().contains(XmlMessages.fileNotFound(file.getPath())));
        }
    }

    public void testFileIsNotInUTF8() throws Exception {
        new XmlDomParser();
        File file = new File(resourcesDir, "nonutf.xml");
        try {
            XmlDomParser.parse(file, "root");
            fail();
        } catch (XmlParsingException e) {
            // OK
        }
    }

    public void testParseFile() throws Exception {
        File file = new File(resourcesDir, "server_cfg.xml");
        XmlDomParser.parse(file, "checkServiceConfig");
    }

    public void testEmptyDocument() throws Exception {
        try {
            File configFile = new File(resourcesDir, "empty.xml");
            XmlDomParser.parse(configFile, "root");
            fail();
        } catch (XmlParsingException e) {
            assertTrue(e.getMessage().contains(XmlDomParser.XmlMessages.emptyDocumentFound()));
        }
    }

    public void testBadRootTag() throws Exception {
        String configXml = "<fake/>";
        StringReader stringReader = new StringReader(configXml);
        try {
            XmlDomParser.parse(stringReader, "root");
            fail();
        } catch (XmlParsingException e) {
            assertTrue(e.getMessage().contains(XmlDomParser.XmlMessages.invalidRootTag("root", "fake")));
        }
    }

    public void testBadSingleChild() throws Exception {
        String configXml = "<root>" + "  <child/>" + "  <child/>" + "</root>";
        StringReader stringReader = new StringReader(configXml);
        try {
            Element root = XmlDomParser.parse(stringReader, "root");
            XmlDomParser.getSingleChild(root, "child");
            fail();
        } catch (SingleChildExpectedException e) {
            // OK
        }
    }

    public void testGetMandatoryAttribute() throws Exception {
        String configXml = "<root>" + "  <child mandatory1='' mandatory2='nonempty'/>" + "</root>";
        StringReader stringReader = new StringReader(configXml);
        Element root = XmlDomParser.parse(stringReader, "root");
        Element child = XmlDomParser.getSingleChild(root, "child");
        try {
            XmlDomParser.getMandatoryAttribute(child, "mandatory0");
            fail();
        } catch (XmlParsingException e) {
            assertTrue(e.getMessage().contains(XmlDomParser.XmlMessages.attributeNotSet("mandatory0", "child")));
        }

        try {
            XmlDomParser.getMandatoryAttribute(child, "mandatory1");
            fail();
        } catch (XmlParsingException e) {
            assertTrue(e.getMessage().contains(XmlDomParser.XmlMessages.attributeNotSet("mandatory1", "child")));
        }

        assertEquals("nonempty", XmlDomParser.getMandatoryAttribute(child, "mandatory2"));
        assertEquals("nonempty", XmlDomParser.getAttribute(child, "mandatory2", "default"));
        assertEquals("default", XmlDomParser.getAttribute(child, "mandatory0", "default"));
        assertEquals("default", XmlDomParser.getAttribute(child, "mandatory1", "default"));
    }

    public void testGetOptionalAttribute() throws Exception {
        String configXml = "<root>" + "  <child optional1='' optional2='nonempty'/>" + "</root>";
        StringReader stringReader = new StringReader(configXml);
        Element root = XmlDomParser.parse(stringReader, "root");
        Element child = XmlDomParser.getSingleChild(root, "child");

        assertEquals("", XmlDomParser.getOptionalAttribute(child, "optional0"));
        assertEquals("", XmlDomParser.getOptionalAttribute(child, "optional1"));
        assertEquals("nonempty", XmlDomParser.getOptionalAttribute(child, "optional2"));

        assertEquals("default", XmlDomParser.getOptionalStringAttribute(child, "optional0", "default"));
        assertEquals("", XmlDomParser.getOptionalStringAttribute(child, "optional1", "default"));
        assertEquals("nonempty", XmlDomParser.getOptionalStringAttribute(child, "optional2", "default"));
    }

    public void testGetOptionalIntAttribute() throws Exception {
        String configXml = "<root>" + "  <child optional1='' optional2='12'/>" + "</root>";
        StringReader stringReader = new StringReader(configXml);
        Element root = XmlDomParser.parse(stringReader, "root");
        Element child = XmlDomParser.getSingleChild(root, "child");

        assertEquals(-1, XmlDomParser.getOptionalIntAttribute(child, "optional0", -1));
        assertEquals(-1, XmlDomParser.getOptionalIntAttribute(child, "optional1", -1));
        assertEquals(12, XmlDomParser.getOptionalIntAttribute(child, "optional2", -1));
    }

    public void testGetOptionalBooleanAttribute() throws Exception {
        String configXml = "<root>" + "  <child optional1='' optional2='true'/>" + "</root>";
        StringReader stringReader = new StringReader(configXml);
        Element root = XmlDomParser.parse(stringReader, "root");
        Element child = XmlDomParser.getSingleChild(root, "child");

        assertFalse(XmlDomParser.getOptionalBooleanAttribute(child, "optional0", false));
        assertFalse(XmlDomParser.getOptionalBooleanAttribute(child, "optional1", false));
        assertTrue(XmlDomParser.getOptionalBooleanAttribute(child, "optional2", false));
    }

    public void testGetNodeValueAsObject() throws Exception {
        String configXml = "<root></root>";
        StringReader stringReader = new StringReader(configXml);
        Element root = XmlDomParser.parse(stringReader, "root");
        assertEquals("", XmlDomParser.getNodeValue(root));
        assertEquals("", XmlDomParser.getNodeValueAsObject(root));
    }

    public void testGetNodeValueAsObjectWithChildren() throws Exception {
        String configXml = "<root>sds" + "  <child>value</child>" + "</root>";
        StringReader stringReader = new StringReader(configXml);
        Element root = XmlDomParser.parse(stringReader, "root");
        Element child = XmlDomParser.getSingleChild(root, "child");
        assertEquals("value", XmlDomParser.getNodeValue(child));
        assertEquals(root, XmlDomParser.getNodeValueAsObject(root));
    }

    public void testGetNodeValueAsObjectNonEmpty() throws Exception {
        String configXml = "<root>" + "  ..." + "</root>";
        StringReader stringReader = new StringReader(configXml);
        Element root = XmlDomParser.parse(stringReader, "root");
        assertEquals("  ...", XmlDomParser.getNodeValue(root));
        assertEquals("  ...", XmlDomParser.getNodeValueAsObject(root));
    }

    public void testParseUtils() throws Exception {
        ParserUtils.close(new Reader() {

            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        });
    }

}
