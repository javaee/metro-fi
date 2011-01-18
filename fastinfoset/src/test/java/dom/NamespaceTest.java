package dom;

import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Alexey Stashok
 */
public class NamespaceTest extends TestCase {
    public void testWithoutNamespace() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element e = doc.createElement("ABC");
        root.appendChild(e);
        e = doc.createElement("ABC");
        root.appendChild(e);
        
        DOMDocumentSerializer ds = new DOMDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ds.setOutputStream(baos);
        
        ds.serialize(doc);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SAXDocumentParser sp = new SAXDocumentParser();
        
        sp.parse(bais);
        
        assertTrue(true);
    }
    
    public void testNamespace() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElementNS("http://www.xxx-root.org", "ABC:root");
        doc.appendChild(root);
        Element e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        
        DOMDocumentSerializer ds = new DOMDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ds.setOutputStream(baos);
        
        ds.serialize(doc);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SAXDocumentParser sp = new SAXDocumentParser();
        
        sp.parse(bais);
        
        assertTrue(true);
    }
    
    public void testNestedNamespace() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElementNS("http://www.xxx.org", "ABC:root");
        doc.appendChild(root);
        Element e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        e = doc.createElementNS("http://www.xxx.org", "ABC:e");
        root.appendChild(e);
        
        DOMDocumentSerializer ds = new DOMDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ds.setOutputStream(baos);
        
        ds.serialize(doc);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SAXDocumentParser sp = new SAXDocumentParser();
        
        sp.parse(bais);
        
        assertTrue(true);
    }
}
