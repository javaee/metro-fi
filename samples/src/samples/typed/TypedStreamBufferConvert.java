package samples.typed;

import com.sun.xml.analysis.types.SchemaProcessor;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.streambuffer.FastInfosetReaderSAXBufferCreator;
import com.sun.xml.fastinfoset.streambuffer.FastInfosetWriterSAXBufferProcessor;
import com.sun.xml.fastinfoset.streambuffer.TypedSAXBufferCreator;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParserFactory;
import org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.jvnet.fastinfoset.sax.FastInfosetWriter;
import org.xml.sax.XMLReader;

/**
 * Example of converting from lexical space to value space 
 * using typed stream buffer and encoding the result as a fast infoset document.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class TypedStreamBufferConvert {
    
    /**
     * Parse an XML document and convert to a typed stream buffer 
     * using a schema to convert content from lexical space to value space.
     * Serialize the typed stream buffer to a fast infoset document.
     * Parse the fast infoset document to a typed stream buffer.
     * <p>
     * Arg 0 is the path to the schema (XSD or RNG).
     * <p>
     * Arg 1 is the path to the XML document to be converted.
     * <p>
     */
    public static void main(String[] args) throws Exception {

        /**
         * Process the schema to obtain a mapping of attribute/element
         * local name to a set of XS data types.
         */
        SchemaProcessor sp = new SchemaProcessor(new File(args[0]).toURL());
        sp.process();

        /**
         * Create the typed stream buffer
         */
        XMLStreamBuffer typedSource = TypedSAXBufferCreator.create(
                sp.getElementToXSDataTypeMap(),
                sp.getAttributeToXSDataTypeMap(),
                new BufferedInputStream(new FileInputStream(args[1])));

        /**
         * Serialize the typed stream buffer to a fast infoset document
         */
        ByteArrayOutputStream fiDocumentOut = new ByteArrayOutputStream();
        FastInfosetWriter fiWriter = new SAXDocumentSerializer();
        fiWriter.setOutputStream(fiDocumentOut);
        FastInfosetWriterSAXBufferProcessor p = 
                new FastInfosetWriterSAXBufferProcessor(typedSource);
        p.process(fiWriter);
        
        /**
         * Parse the fast infoset document to a typed stream buffer
         */
        ByteArrayInputStream fiDocumentIn = new ByteArrayInputStream(fiDocumentOut.toByteArray());
        FastInfosetReader fiReader = new SAXDocumentParser();
        FastInfosetReaderSAXBufferCreator c = new FastInfosetReaderSAXBufferCreator();
        MutableXMLStreamBuffer typedSink = c.create(fiReader, fiDocumentIn);
    }    
}
