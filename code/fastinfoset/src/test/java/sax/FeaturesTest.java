package sax;

import com.sun.xml.fastinfoset.sax.Features;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import junit.framework.TestCase;
import org.xml.sax.SAXNotSupportedException;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class FeaturesTest extends TestCase {
    
    public void testNamespaceFeatureTrue() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean exceptionThrown = false;
        try {
            sp.setFeature(Features.NAMESPACES_FEATURE, true);
        } catch (SAXNotSupportedException e) {
            exceptionThrown = true;
        }
        
        assertFalse(exceptionThrown);
    }
    
    public void testNamespaceFeatureFalse() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean exceptionThrown = false;
        try {
            sp.setFeature(Features.NAMESPACES_FEATURE, false);
        } catch (SAXNotSupportedException e) {
            exceptionThrown = true;
        }
        
        assertTrue(exceptionThrown);
    }
    
    public void testStringInternFeature() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean stringIntern = false;
        sp.setFeature(Features.STRING_INTERNING_FEATURE, stringIntern);        
        boolean v = sp.getFeature(Features.STRING_INTERNING_FEATURE);
        assertEquals(stringIntern, v);
        
        stringIntern = true;
        sp.setFeature(Features.STRING_INTERNING_FEATURE, stringIntern);        
        v = sp.getFeature(Features.STRING_INTERNING_FEATURE);
        assertEquals(stringIntern, v);
    }
    
    public void testNamespacePrefixesFeature() throws Exception {
        SAXDocumentParser sp = new SAXDocumentParser();
        
        boolean namespacePrefixes = false;
        sp.setFeature(Features.NAMESPACE_PREFIXES_FEATURE, namespacePrefixes);        
        boolean v = sp.getFeature(Features.NAMESPACE_PREFIXES_FEATURE);
        assertEquals(namespacePrefixes, v);
        
        namespacePrefixes = true;
        sp.setFeature(Features.NAMESPACE_PREFIXES_FEATURE, namespacePrefixes);        
        v = sp.getFeature(Features.NAMESPACE_PREFIXES_FEATURE);
        assertEquals(namespacePrefixes, v);
    }
}
