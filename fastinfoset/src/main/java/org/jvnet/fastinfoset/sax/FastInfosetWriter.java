package org.jvnet.fastinfoset.sax;

import org.jvnet.fastinfoset.FastInfosetSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetWriter extends ContentHandler, LexicalHandler, 
        EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler,
        RestrictedAlphabetContentHandler, ExtendedContentHandler,
        FastInfosetSerializer {
}
