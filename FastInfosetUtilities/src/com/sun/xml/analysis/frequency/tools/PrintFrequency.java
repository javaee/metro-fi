package com.sun.xml.analysis.frequency.tools;

import com.sun.xml.analysis.frequency.*;
import java.io.File;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * A simple class that prints out in the order of descreasing frequency
 * information items declared in a schema and occuring 0 or more times in 
 * a set of infosets.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class PrintFrequency {
    
    /**
     * @param args the command line arguments. arg[0] is the path to a schema,
     * args[1] to args[n] are the paths to XML documents.
     */
    public static void main(String[] args) throws Exception {
        SchemaProcessor sp = new SchemaProcessor(new File(args[0]).toURL());
        sp.process();
        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser p = spf.newSAXParser();
        
        FrequenceHandler fh = new FrequenceHandler(sp);
        for (int i = 1; i < args.length; i++) {
            p.parse(new File(args[i]), fh);
        }

        FrequencyBasedLists l = fh.getLists();
        System.out.println("Elements");
        for (QName q : l.elements) {
            System.out.println(q.getPrefix() + " " + q);
        }
        
        System.out.println("Attributes");
        for (QName q : l.attributes) {
            System.out.println(q.getPrefix() + " " + q);
        }
    }
}
