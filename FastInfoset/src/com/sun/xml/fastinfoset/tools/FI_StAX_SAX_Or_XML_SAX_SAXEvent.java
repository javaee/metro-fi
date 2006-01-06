/*
 * Fast Infoset ver. 0.1 software ("Software")
 * 
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved. 
 * 
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 * 
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 * 
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 * 
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 */ 


package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.sax.Properties;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import com.sun.xml.fastinfoset.tools.StAX2SAXReader;

public class FI_StAX_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput {
    
    public FI_StAX_SAX_Or_XML_SAX_SAXEvent() {
    }

    public void parse(InputStream document, OutputStream events) throws Exception {
        if (!document.markSupported()) {
            document = new BufferedInputStream(document);
        }
        
        document.mark(4);
        boolean isFastInfosetDocument = Decoder.isFastInfosetDocument(document);
        document.reset();
        
        if (isFastInfosetDocument) {
            StAXDocumentParser parser = new StAXDocumentParser();
            parser.setInputStream(document);
            SAXEventSerializer ses = new SAXEventSerializer(events);
            StAX2SAXReader reader = new StAX2SAXReader(parser, ses);
            reader.setLexicalHandler(ses);
            reader.adapt();
        } else {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            SAXParser parser = parserFactory.newSAXParser();
            SAXEventSerializer ses = new SAXEventSerializer(events);
            parser.setProperty(Properties.LEXICAL_HANDLER_PROPERTY, ses);
            parser.parse(document, ses);
        }
    }
    
    public static void main(String[] args) throws Exception {
        FI_StAX_SAX_Or_XML_SAX_SAXEvent p = new FI_StAX_SAX_Or_XML_SAX_SAXEvent();
        p.parse(args);
    }    
    
}
