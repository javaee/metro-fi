package samples.typed;

import com.sun.xml.analysis.types.SchemaProcessor;
import com.sun.xml.analysis.types.XSDataType;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.FastInfosetSource;
import org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.jvnet.fastinfoset.sax.helpers.EncodingAlgorithmAttributesImpl;
import org.jvnet.fastinfoset.sax.helpers.FastInfosetDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class ConvertLexicalValues {
        
    static class LexicalFilter extends XMLFilterImpl {
        private Map<String, Set<XSDataType>> _elements;
        private Map<String, Set<XSDataType>> _attributes;
        private Set<XSDataType> _textContent;
        
        public LexicalFilter(Map<String, Set<XSDataType>> elements, 
                Map<String, Set<XSDataType>> attributes) {
            _elements = elements;
            _attributes = attributes;
        }
        
        private void replaceAttributeValue(Set<XSDataType> attributeTypes, 
                int index, EncodingAlgorithmAttributesImpl atts) {
            if (attributeTypes == null) return;
            
            char[] ch = atts.getValue(index).toCharArray();
            for (XSDataType dt : attributeTypes) {
                try {
                    switch(dt) {
                        case BASE64BINARY:
                            byte[] b = (byte[])BuiltInEncodingAlgorithmFactory.
                                    base64EncodingAlgorithm.
                                    convertFromCharacters(ch, 0, ch.length);
                            atts.replaceAttributeAlgorithmData(index, 
                                    null, EncodingAlgorithmIndexes.BASE64, b);
                            return;
                        case FLOAT:
                            float[] f = (float[])BuiltInEncodingAlgorithmFactory.
                                    floatEncodingAlgorithm.
                                    convertFromCharacters(ch, 0, ch.length);
                            atts.replaceAttributeAlgorithmData(index, 
                                    null, EncodingAlgorithmIndexes.FLOAT, f);
                            return;
                        default:
                    }
                } catch (Exception e) {
                }
            }
            
        }
        
        public void startElement(String uri, String localName, String qName, 
                Attributes atts) throws SAXException {
            _textContent = _elements.get(localName);
            
            for (int i = 0; i < atts.getLength(); i++) {
                if (_attributes.containsKey(atts.getLocalName(i))) {
                    // Copy attributes
                    final EncodingAlgorithmAttributesImpl eatts = new EncodingAlgorithmAttributesImpl(atts);
                    
                    for (i = 0; i < atts.getLength(); i++) {
                        replaceAttributeValue(_attributes.get(atts.getLocalName(i)),
                                i, eatts);
                    }
                    atts = eatts;
                    break;
                }
            }
            
            super.startElement(uri, localName, qName, atts);
        }
        
        public void characters (char ch[], int start, int length)
            throws SAXException {
            if (_textContent == null) {
                super.characters(ch, start, length);
                return;
            }

            for (XSDataType dt : _textContent) {
                try {
                    switch(dt) {
                        case BASE64BINARY:
                            byte[] b = (byte[])BuiltInEncodingAlgorithmFactory.
                                    base64EncodingAlgorithm.
                                    convertFromCharacters(ch, start, length);
                            ((PrimitiveTypeContentHandler)getContentHandler()).
                                    bytes(b, 0, b.length);
                            _textContent = null;
                            return;
                        case FLOAT:
                            float[] f = (float[])BuiltInEncodingAlgorithmFactory.
                                    floatEncodingAlgorithm.
                                    convertFromCharacters(ch, start, length);
                            ((PrimitiveTypeContentHandler)getContentHandler()).
                                    floats(f, 0, f.length);
                            _textContent = null;
                            return;
                        default:
                            super.characters(ch, start, length);
                            _textContent = null;
                            return;
                    }
                } catch (Exception e) {
                }
            }
        }
    }
        
    public static void main(String[] args) throws Exception {
        SchemaProcessor sp = new SchemaProcessor(new File(args[0]).toURL());
        sp.process();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SAXDocumentSerializer s = new SAXDocumentSerializer();
        s.setOutputStream(baos);
        
        LexicalFilter lf = new LexicalFilter(sp.getElementToXSDataTypeMap(), 
                sp.getAttributeToXSDataTypeMap());
        lf.setContentHandler(s);
        lf.setParent(getXMLReader());
        lf.parse(new InputSource(new FileInputStream(args[1])));
        
        FastInfosetSource source = new FastInfosetSource(
                new ByteArrayInputStream(baos.toByteArray()));
        StreamResult result = new StreamResult(System.out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(source, result);
        System.out.println();
        
        SAXDocumentParser p = new SAXDocumentParser();
        FastInfosetDefaultHandler h = new FastInfosetDefaultHandler() {
            public void bytes(byte[] b, int start, int length) 
            throws SAXException {
                System.out.println("Byte: " + b[start]);
            }

            public void floats(float[] f, int start, int length) 
            throws SAXException {
                System.out.println("Float: " + f[start]);
            }
        };
        p.setContentHandler(h);
        p.setPrimitiveTypeContentHandler(h);
        p.parse(new ByteArrayInputStream(baos.toByteArray()));
    }
    
    public static XMLReader getXMLReader() throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        return spf.newSAXParser().getXMLReader();
    }
}
