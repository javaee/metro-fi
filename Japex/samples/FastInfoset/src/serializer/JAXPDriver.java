/*
 * JAXPDriver.java
 *
 * Created on January 27, 2005, 5:40 PM
 */

package serializer;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.fastinfoset.stax.StAXInputFactory;
import com.sun.xml.fastinfoset.stax.SAX2StAXWriter;

import com.sun.japex.*;

public class JAXPDriver extends JapexDriverBase {
    String _xmlFile;
    ByteArrayInputStream _inputStream;
    Transformer _transformer;
    DOMSource _source = null;
    StreamResult _result = null;
    ByteArrayOutputStream _baos;
    /** Creates a new instance of StAXRIDriver */
    public JAXPDriver() {
    }
    public void initializeDriver() {
        try {
            _transformer = TransformerFactory.newInstance().newTransformer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
    
    public void prepare(TestCase testCase) {
        _xmlFile = testCase.getParam("xmlfile");
        if (_xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }
        
        Util util = new Util();
        _source = util.getDOMSource(new File(_xmlFile));
        _baos = new ByteArrayOutputStream();
        _result = new StreamResult(_baos);
    }
    
    public void warmup(TestCase testCase) {
        try {
            _baos.reset();
            _transformer.transform(_source, _result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run(TestCase testCase) {
        try {
            _baos.reset();
            _transformer.transform(_source, _result);
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
