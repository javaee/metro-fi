/*
 * StAXRIDriver.java
 *
 * Created on July 27, 2004, 10:13 AM
 */

package serializers;

import java.io.*;
import javax.xml.stream.*;
import java.util.Properties;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import com.sun.japex.*; 

/**
 *
 * @author  sp106478
 */
public class SerializeDriver extends JapexDriverBase {
    
    String _xmlFile;
    Transformer _identity;
    DocumentBuilder _docBuilder;
    Document _document;
    
    public SerializeDriver() {
    }
    
    public void initializeDriver() {
        try {
            _identity = TransformerFactory.newInstance().newTransformer();
            _docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void prepare(TestCase testCase) {
        _xmlFile = testCase.getParam("xmlfile");
        if (_xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }
        
        // Load file into byte array to factor out IO
        try {
            FileInputStream fis = new FileInputStream(new File(_xmlFile));
            _document = _docBuilder.parse(fis);
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void warmup(TestCase testCase) {
        try {
            // Serialize into a byte array
            _identity.transform(
            new DOMSource(_document),
            new StreamResult(new ByteArrayOutputStream()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run(TestCase testCase) {
        try {
            // Serialize into a byte array
            _identity.transform(
            new DOMSource(_document),
            new StreamResult(new ByteArrayOutputStream()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void finish(TestCase testCase) {
    }
    
    public void terminateDriver() {
    }
}
