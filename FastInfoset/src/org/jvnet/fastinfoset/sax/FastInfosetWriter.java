package org.jvnet.fastinfoset.sax;

import java.util.Map;
import org.jvnet.fastinfoset.FastInfosetSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetWriter extends ContentHandler, LexicalHandler, 
        EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler,
        RestrictedAlphabetContentHandler, FastInfosetSerializer {
}
