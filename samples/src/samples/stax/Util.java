/*
 * Util.java
 *
 * Created on January 14, 2005, 12:35 PM
 */

package samples.stax;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;

/**
 *
 * @author hw123265
 */
public class Util {
    
    /** Creates a new instance of Util */
    public Util() {
    }
    
    /**
     * @param eventType
     * @return
     */    
    public final static String getEventTypeString(int eventType) {
        switch (eventType){
            case XMLStreamConstants.START_ELEMENT:
                return "START_ELEMENT";
            case XMLStreamConstants.END_ELEMENT:
                return "END_ELEMENT";
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLStreamConstants.CHARACTERS:
                return "CHARACTERS";
            case XMLStreamConstants.COMMENT:
                return "COMMENT";
            case XMLStreamConstants.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLStreamConstants.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLStreamConstants.ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case XMLStreamConstants.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLStreamConstants.DTD:
                return "DTD";
            case XMLStreamConstants.CDATA:
                return "CDATA";
        }
        return "UNKNOWN_EVENT_TYPE";
    }
    
    public static void printEventType(int eventType) {
        System.out.print("EVENT TYPE("+eventType+"):");
        System.out.println(getEventTypeString(eventType));
    }
    
    public static void printName(XMLStreamReader xmlr,int eventType){
        if(xmlr.hasName()){
            System.out.println("HAS NAME: " + xmlr.getLocalName());
        } else {
            System.out.println("HAS NO NAME");
        }
    }
    
    public static void printText(XMLStreamReader xmlr){
        if(xmlr.hasText()){
            System.out.println("HAS TEXT: " + xmlr.getText());
        } else {
            System.out.println("HAS NO TEXT");
        }
    }
    
    public static void printPIData(XMLStreamReader xmlr){
        if (xmlr.getEventType() == XMLStreamConstants.PROCESSING_INSTRUCTION){
            System.out.println(" PI target = " + xmlr.getPITarget() ) ;
            System.out.println(" PI Data = " + xmlr.getPIData() ) ;
        }
    }
    
    public static void printAttributes(XMLStreamReader xmlr){
        if(xmlr.getAttributeCount() > 0){
            System.out.println("\nHAS ATTRIBUTES: ");
            int count = xmlr.getAttributeCount() ;
            for(int i = 0 ; i < count ; i++) {
                
                QName name = xmlr.getAttributeName(i) ;
                String namespace = xmlr.getAttributeNamespace(i) ;
                String  type = xmlr.getAttributeType(i) ;
                String prefix = xmlr.getAttributePrefix(i) ;
                String value = xmlr.getAttributeValue(i) ;
                
                System.out.println("ATTRIBUTE-PREFIX: " + prefix );
                System.out.println("ATTRIBUTE-NAMESP: " + namespace );
                System.out.println("ATTRIBUTE-NAME:   " + name.toString() );
                System.out.println("ATTRIBUTE-VALUE:  " + value );
                System.out.println("ATTRIBUTE-TYPE:  " + type );
                System.out.println();
                
            }
            
        } else {
            System.out.println("HAS NO ATTRIBUTES");
        }
    }
        
}
