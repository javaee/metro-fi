package com.sun.xml.fastinfoset.api.sax;

import com.sun.xml.fastinfoset.api.VocabularyWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetWriter extends ContentHandler, LexicalHandler, 
        EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler, 
        VocabularyWriter {
    
}
