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


package encoding;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.sax.VocabularyGenerator;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jvnet.fastinfoset.FastInfosetParser;

public class DecodingTest extends TestCase {

    public static final String FINF_SPEC_UBL_XML_RESOURCE = "X.finf/UBL-example.xml";
    public static final String FINF_SPEC_UBL_FINF_RESOURCE = "X.finf/UBL-example.finf";
    public static final String FINF_SPEC_UBL_FINF_REFVOCAB_RESOURCE = "X.finf/UBL-example-refvocab.finf";
    
    public static final String EXTERNAL_VOCABULARY_URI_STRING = "urn:oasis:names:tc:ubl:Order:1:0:joinery:example";

    private SAXParserFactory _saxParserFactory;
    private SAXParser _saxParser;
    private URL _xmlDocumentURL;
    private URL _finfDocumentURL;
    private URL _finfRefVocabDocumentURL;
        
    /** Creates a new instance of DecodeTestCase */
    public DecodingTest()  throws Exception {
        _saxParserFactory = SAXParserFactory.newInstance();
        _saxParserFactory.setNamespaceAware(true);
        _saxParser = _saxParserFactory.newSAXParser();
        
        _xmlDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_XML_RESOURCE);
        _finfDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_FINF_RESOURCE);
        _finfRefVocabDocumentURL = this.getClass().getClassLoader().getResource(FINF_SPEC_UBL_FINF_REFVOCAB_RESOURCE);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DecodingTest.class);
        return suite;
    }

    public void testDecodeWithVocabulary() throws Exception {
        SerializerVocabulary serializerExternalVocabulary = new SerializerVocabulary();
        serializerExternalVocabulary.attributeValueSizeConstraint = serializerExternalVocabulary.characterContentChunkSizeContraint = 0;
        ParserVocabulary parserExternalVocabulary = new ParserVocabulary();
        
        VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(serializerExternalVocabulary, parserExternalVocabulary);
        _saxParser.parse(_xmlDocumentURL.openStream(), vocabularyGenerator);

        SerializerVocabulary initialVocabulary = new SerializerVocabulary();
        initialVocabulary.attributeValueSizeConstraint = initialVocabulary.characterContentChunkSizeContraint = 6;
        initialVocabulary.setExternalVocabulary(
                new URI(EXTERNAL_VOCABULARY_URI_STRING),
                serializerExternalVocabulary, false);

        // Map<String, ParserVocabulary> externalVocabularies = new HashMap<String, ParserVocabulary>();
        Map externalVocabularies = new HashMap();
        externalVocabularies.put(EXTERNAL_VOCABULARY_URI_STRING, parserExternalVocabulary);
        
        
        SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setVocabulary(initialVocabulary);        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        documentSerializer.setOutputStream(baos);

        SAXDocumentParser documentParser = new SAXDocumentParser();
        documentParser.setProperty(FastInfosetParser.EXTERNAL_VOCABULARIES_PROPERTY, externalVocabularies);
        documentParser.setContentHandler(documentSerializer);
        documentParser.parse(_finfRefVocabDocumentURL.openStream());        

        
        byte[] finfDocument = baos.toByteArray();
        compare(finfDocument, obtainBytesFromStream(_finfRefVocabDocumentURL.openStream()));
    }

    public void testDecodeWithoutVocabulary() throws Exception {
        SerializerVocabulary initialVocabulary = new SerializerVocabulary();
        initialVocabulary.attributeValueSizeConstraint = initialVocabulary.characterContentChunkSizeContraint = 6;

        SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setVocabulary(initialVocabulary);        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        documentSerializer.setOutputStream(baos);

        SAXDocumentParser documentParser = new SAXDocumentParser();
        documentParser.setContentHandler(documentSerializer);
        documentParser.parse(_finfDocumentURL.openStream());

        
        byte[] finfDocument = baos.toByteArray();
        compare(finfDocument, obtainBytesFromStream(_finfDocumentURL.openStream()));
    }
 
        
    private void compare(byte[] fiDocument, byte[] specFiDocument) throws Exception {
        this.assertTrue("Fast infoset document is not the same length as the X.finf specification", 
            fiDocument.length == specFiDocument.length);
        
        boolean passed = true;
        for (int i = 0; i < fiDocument.length; i++) {
            if (fiDocument[i] != specFiDocument[i]) {
                System.err.println(Integer.toHexString(i) + ": " + 
                        Integer.toHexString(fiDocument[i] & 0xFF) + " " + 
                        Integer.toHexString(specFiDocument[i] & 0xFF));
                passed = false;
            }
        }
        
        assertTrue("Fast infoset document does not have the same content as the X.finf specification",
            passed);

    }
    
    static byte[] obtainBytesFromStream(InputStream s) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        
        int bytesRead = 0;
        while ((bytesRead = s.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}
