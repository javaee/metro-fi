package stax;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import junit.framework.TestCase;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class StringTest extends TestCase {
    
    public void testIndexedStrings() throws Exception {
        
        XMLStreamReader r = new StAXDocumentParser(createDocumentWithIndexedStrings());
        while (r.hasNext()) {
            switch (r.next()) {
                case XMLStreamReader.CHARACTERS:
                    String s1 = new String(
                            r.getTextCharacters(),
                            r.getTextStart(),
                            r.getTextLength());
                    String s2 = r.getText();
                    assertEquals(s1, s2);
            }
        }
    }
    
    public void testNonIndexedStrings() throws Exception {
        
        XMLStreamReader r = new StAXDocumentParser(createDocumentWithoutIndexedStrings());
        while (r.hasNext()) {
            switch (r.next()) {
                case XMLStreamReader.CHARACTERS:
                    String s1 = new String(
                            r.getTextCharacters(),
                            r.getTextStart(),
                            r.getTextLength());
                    String s2 = r.getText();
                    assertEquals(s1, s2);
            }
        }
    }
    
    InputStream createDocumentWithIndexedStrings() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StAXDocumentSerializer s = new StAXDocumentSerializer(baos);
        XMLStreamWriter w = s;
        
        w.writeStartDocument();
            w.writeStartElement("root");
                for (int i = 0; i < 8192; i++) {
                    w.writeStartElement("content");
                        w.writeCharacters(Integer.toString(i));
                    w.writeEndElement();
                    w.writeStartElement("content");
                        w.writeCharacters(Integer.toString(i));
                    w.writeEndElement();
                }
            w.writeEndElement();
        w.writeEndDocument();
        w.flush();
        
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    InputStream createDocumentWithoutIndexedStrings() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StAXDocumentSerializer s = new StAXDocumentSerializer(baos);
        XMLStreamWriter w = s;
        
        w.writeStartDocument();
            w.writeStartElement("root");
                for (int i = 0; i < 2048; i++) {
                    w.writeStartElement("content");
                        w.writeCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + Integer.toString(i));
                    w.writeEndElement();
                    w.writeStartElement("content");
                        w.writeCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + Integer.toString(i));
                    w.writeEndElement();
                }
            w.writeEndElement();
        w.writeEndDocument();
        w.flush();
        
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
