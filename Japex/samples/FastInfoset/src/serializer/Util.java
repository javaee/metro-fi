/*
 * Util.java
 *
 * Created on January 27, 2005, 4:35 PM
 */

package serializer;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.fastinfoset.stax.StAXInputFactory;
import com.sun.xml.fastinfoset.stax.SAX2StAXWriter;

/**
 *
 * @author hw123265
 */
public class Util {
    DocumentBuilder _docBuilder;
            
    public static final int STAX_SERIALIZER_RI = 1;
    public static final int STAX_SERIALIZER_FI = 2;
    public static final int STAX_SERIALIZER_SJSXP = 3;
    
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    /** Creates a new instance of Util */
    public Util() {
        init();
    }
    
    public Util(int outputFactory) {
        if (outputFactory==STAX_SERIALIZER_FI) {
            System.setProperty("javax.xml.stream.XMLOutputFactory", 
                       "com.sun.xml.fastinfoset.stax.StAXOutputFactory");        
        } else if (outputFactory==STAX_SERIALIZER_SJSXP) {
            System.setProperty("javax.xml.stream.XMLOutputFactory", 
                       "com.sun.xml.stream.ZephyrWriterFactory");                    
        }
        init();
    }
    
    void init() {
        try {
            factory = XMLOutputFactory.newInstance(); 
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            _docBuilder = dbf.newDocumentBuilder();             
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    public DOMSource getDOMSource(File input) {
        try {
            FileInputStream fis = new FileInputStream(input);
            Document document = _docBuilder.parse(fis);
            fis.close();
            return new DOMSource(document);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    

    public SAXResult getSAXResult(OutputStream output) {
        SAXResult _result = null;
        try {
            XMLStreamWriter serializer = factory.createXMLStreamWriter(output);
            SAX2StAXWriter saxTostax = new SAX2StAXWriter(serializer);
            
            _result = new SAXResult();
            _result.setHandler(saxTostax);
            _result.setLexicalHandler(saxTostax);                
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }   
        return _result;
    }
    public StreamResult getStreamResult(OutputStream output) {
        StreamResult result = null;
        try {
            result = new StreamResult(output);            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }    
}
