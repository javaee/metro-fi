package stax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

/**
 * @author Alexey Stashok
 */
public class EncodingTest extends TestCase implements
        XMLStreamConstants {
    
    public void testRoundTripInMemory() throws Exception {
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = new StAXDocumentSerializer(out);
        writer.writeStartDocument();
        
        writer.writeStartElement("Envelope");
        writer.writeStartElement("Body");
        
        writer.writeStartElement("findPerson");
        writer.writeStartElement("person");
        
        writer.writeStartElement("name");
        writer.writeStartElement("first");
        writer.writeCharacters("j");
        writer.writeEndElement();
        writer.writeStartElement("last");
        writer.writeCharacters("smith");
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeStartElement("ssn");
        writer.writeCharacters("123");
        writer.writeEndElement();
        
        writer.writeStartElement("requestor");
        writer.writeCharacters("foo");
        writer.writeEndElement();
        
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeEndDocument();
        writer.flush();
        out.flush();
        out.close();
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader reader = new StAXDocumentParser(in);
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Envelope", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("Body", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("findPerson", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("person", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("name", reader.getLocalName());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("first", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("j", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("last", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("smith", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(END_ELEMENT, reader.next()); // </name>
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("ssn", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("123", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(START_ELEMENT, reader.next());
        assertEquals("requestor", reader.getLocalName());
        assertEquals(CHARACTERS, reader.next());
        assertEquals("foo", reader.getText());
        assertEquals(END_ELEMENT, reader.next());
        
        assertEquals(END_ELEMENT, reader.next()); // </person>
        assertEquals(END_ELEMENT, reader.next()); // </findPerson>
        assertEquals(END_ELEMENT, reader.next()); // </Body>
        assertEquals(END_ELEMENT, reader.next()); // </Envelope>
        
        reader.close();
        in.close();
    }
}


