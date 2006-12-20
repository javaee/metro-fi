package stax;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.tools.SAXEventSerializer;
import com.sun.xml.fastinfoset.tools.StAX2SAXReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import junit.framework.TestCase;

/**
 *
 * @author Alexey Stashok
 */
public class NamespaceTest extends TestCase {
    
    private static final String NS1 = "http://namespace1";
    private static final String NS2 = "http://namespace2";

    public void testDefaultNamespace() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StAXDocumentSerializer writer = new StAXDocumentSerializer();
        writer.setOutputStream(baos);
        write(writer);
        byte[] data = baos.toByteArray();
        baos.reset();
        InputStream in = new ByteArrayInputStream(data);
        
        StAXDocumentParser reader = new StAXDocumentParser();
        reader.setInputStream(in);
        data = read(reader);
    }
    
    public void write(XMLStreamWriter writer) throws Exception {
        writer.writeStartDocument();
        
        writer.setDefaultNamespace(NS1);
        writer.writeStartElement("S", "Action1", NS1);
        writer.writeNamespace("S", NS1);
        writer.writeDefaultNamespace(NS1);
        writer.writeStartElement("Action2");
        
        writer.writeStartElement("Action3");
        writer.writeDefaultNamespace(NS2);
        writer.writeStartElement("Action4");
        writer.writeEndElement(); // Action4
        writer.writeEndElement(); // Action3

        writer.writeStartElement("Action5");
        writer.writeEndElement(); //Action5

        writer.writeEndElement(); //Action2
        writer.writeEndElement(); //Action1
        writer.writeEndDocument();
        writer.flush();
    }

    public byte[] read(XMLStreamReader reader) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SAXEventSerializer ses = new SAXEventSerializer(baos);
        StAX2SAXReader saxreader = new StAX2SAXReader(reader, ses);
        saxreader.setLexicalHandler(ses);
        saxreader.adapt();
        return baos.toByteArray();
    }

}
