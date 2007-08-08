package dom;

import com.sun.xml.fastinfoset.dom.DOMDocumentParser;
import com.sun.xml.fastinfoset.tools.XML_SAX_FI;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NormalizedTextNodeTest extends TestCase {
    
    public void testNormalizedTextNode() throws Exception {
        String attrName = "attr";
        String attrValue = "&lt;escapedAttr&gt;";
        String attrValueExpected = "<escapedAttr>";
        
        String textNodeValue = "&lt;ACCOUNTS&gt;&lt;ACCOUNT&gt;";
        String textNodeValueExpected = "<ACCOUNTS><ACCOUNT>";
        
        String testString = "<RAW_DATA_STRING " + attrName + "=\"" + attrValue +
                "\">" + textNodeValue + "</RAW_DATA_STRING>";
        
        XML_SAX_FI fi = new XML_SAX_FI();
        byte b[] = testString.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fi.parse(is, os);
        
        DOMDocumentParser documentParser = new DOMDocumentParser();
        Document document =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        
        ByteArrayInputStream inputFI = new ByteArrayInputStream(os.toByteArray());
        documentParser.parse(document, inputFI);
        
        Element element = (Element) document.getDocumentElement();
        
        assertEquals(element.getAttribute(attrName), attrValueExpected);
        assertEquals(element.getFirstChild().getNodeValue(), textNodeValueExpected);
    }
}
