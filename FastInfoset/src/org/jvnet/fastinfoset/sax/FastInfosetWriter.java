package org.jvnet.fastinfoset.sax;

import java.util.Map;
import org.jvnet.fastinfoset.VocabularyWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetWriter extends ContentHandler, LexicalHandler, 
        EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler, 
        VocabularyWriter {

    public void setRegisteredEncodingAlgorithms(Map algorithms);
    
    public Map getRegisteredEncodingAlgorithms();
}
