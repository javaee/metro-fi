package encoding;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import junit.framework.TestCase;
import org.jvnet.fastinfoset.FastInfosetException;
import org.xml.sax.helpers.AttributesImpl;

public class DuplicateAttributesTest extends TestCase {
    
    public DuplicateAttributesTest() {
    }

    public void testDuplicateAttributes() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(createDocumentWithDuplicateAttributes());        
        SAXDocumentParser sdp = new SAXDocumentParser();
        
        sdp.setInputStream(bais);
        
        boolean exceptionThrown = false;
        try {
            parse(sdp, bais);
        } catch (FastInfosetException e) {
            exceptionThrown = true;
        }
        
        assertEquals(true, exceptionThrown);
    }

    public void testDuplicateNamespaceAttributes() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(createDocumentWithDuplicateNamespaceAttributes());        
        SAXDocumentParser sdp = new SAXDocumentParser();
        
        boolean exceptionThrown = false;
        try {
            parse(sdp, bais);
        } catch (FastInfosetException e) {
            exceptionThrown = true;
        }
        
        assertEquals(true, exceptionThrown);
    }
    
    public void testReparseAfterDuplicateAttributes() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(createDocumentWithDuplicateAttributes());        
        SAXDocumentParser sdp = new SAXDocumentParser();
        
        boolean exceptionThrown = false;
        try {
            parse(sdp, bais);
        } catch (FastInfosetException e) {
            exceptionThrown = true;
        }
        
        assertEquals(true, exceptionThrown);
        
        bais = new ByteArrayInputStream(createWellFormedDocument());        
        exceptionThrown = false;
        try {
            parse(sdp, bais);
        } catch (FastInfosetException e) {
            e.printStackTrace();
            exceptionThrown = true;
        }
        assertEquals(false, exceptionThrown);
    }
    
    public void testReparseAfterDuplicateNamespaceAttributes() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(createDocumentWithDuplicateNamespaceAttributes());        
        SAXDocumentParser sdp = new SAXDocumentParser();
        
        boolean exceptionThrown = false;
        try {
            parse(sdp, bais);
        } catch (FastInfosetException e) {
            exceptionThrown = true;
        }
        
        assertEquals(true, exceptionThrown);
        
        bais = new ByteArrayInputStream(createWellFormedDocument());        
        exceptionThrown = false;
        try {
            parse(sdp, bais);
        } catch (FastInfosetException e) {
            e.printStackTrace();
            exceptionThrown = true;
        }
        assertEquals(false, exceptionThrown);
    }
    
    void parse(SAXDocumentParser sdp, InputStream in) throws Exception {
        sdp.setInputStream(in);
        sdp.parse();
    }
    
    byte[] createDocumentWithDuplicateAttributes() throws Exception {
        SAXDocumentSerializer sds = new SAXDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AttributesImpl attributes = new AttributesImpl();
        
        sds.setOutputStream(baos);
        
        String namespace = "http://namespace";
        String prefix = "ns";
        
        attributes.addAttribute(namespace, "a", prefix + ":a", "PCDATA", "value1");
        attributes.addAttribute(namespace, "a", prefix + ":a", "PCDATA", "value2");
        
        sds.startDocument();
            sds.startPrefixMapping(prefix, namespace);
            sds.startElement(namespace, "e", prefix + ":e", attributes);
            sds.endElement(namespace, "e", prefix + ":e");
            sds.endPrefixMapping(prefix);
        sds.endDocument();
        
        return baos.toByteArray();
    }
    
    byte[] createDocumentWithDuplicateNamespaceAttributes() throws Exception {
        SAXDocumentSerializer sds = new SAXDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AttributesImpl attributes = new AttributesImpl();
        
        sds.setOutputStream(baos);
        
        String namespace = "http://namespace";
        String prefix = "ns";
                
        sds.startDocument();
            sds.startPrefixMapping(prefix, namespace);
            sds.startPrefixMapping(prefix, namespace);
            sds.startElement(namespace, "e", prefix + ":e", attributes);
            sds.endElement(namespace, "e", prefix + ":e");
            sds.endPrefixMapping(prefix);
            sds.endPrefixMapping(prefix);
        sds.endDocument();
        
        return baos.toByteArray();
    }
    
    byte[] createWellFormedDocument() throws Exception {
        SAXDocumentSerializer sds = new SAXDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AttributesImpl attributes = new AttributesImpl();
        
        sds.setOutputStream(baos);
        
        String namespace = "http://namespace";
        String prefix = "ns";
                
        attributes.addAttribute(namespace, "a", prefix + ":a", "PCDATA", "value1");
        attributes.addAttribute(namespace, "b", prefix + ":b", "PCDATA", "value1");
        
        sds.startDocument();
            sds.startPrefixMapping(prefix, namespace);
            sds.startElement(namespace, "e", prefix + ":e", attributes);
            sds.endElement(namespace, "e", prefix + ":e");
            sds.endPrefixMapping(prefix);
        sds.endDocument();
        
        return baos.toByteArray();
    }
}
