package com.sun.xml.analysis.frequency;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A Schema processor that collects the namespaces, local names, elements
 * and attributes declared in a set of schema.
 *
 * TODO: add default values for attribute/element simple content.
 *     : enums used for attribute/element simple content
 * @author Paul.Sandoz@Sun.Com
 */
public class SchemaProcessor implements XSVisitor, XSSimpleTypeVisitor {
    
    private class StringComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            String s1 = (String)o1;
            String s2 = (String)o2;
            return s1.compareTo(s2);
        }
    };
    private StringComparator _stringComparator = new StringComparator();
    
    private class QNameComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            QName q1 = (QName)o1;
            QName q2 = (QName)o2;
            
            if (q1.getNamespaceURI() == null 
                    && q2.getNamespaceURI() == null) {
                return q1.getLocalPart().compareTo(q2.getLocalPart());
            } else if (q1.getNamespaceURI() == null) {
                return 1;
            } else if (q2.getNamespaceURI() == null) {
                return -1;
            } else {
                int c = q1.getNamespaceURI().compareTo(q2.getNamespaceURI());
                if (c != 0) return c;
                return q1.getLocalPart().compareTo(q2.getLocalPart());                
            }
        }
    };
    private QNameComparator _qNameComparator = new QNameComparator();
    
    /**
     * The set of elements declared in the schema
     */
    Set<QName> elements = new java.util.TreeSet(_qNameComparator);
    /**
     * The set of attributes declared in the schema
     */
    Set<QName> attributes = new java.util.TreeSet(_qNameComparator);
    /**
     * The set of local names declared in the schema
     */
    Set<String> localNames = new java.util.TreeSet(_stringComparator);
    /**
     * The set of namespaces declared in the schema
     */
    Set<String> namespaces = new java.util.TreeSet(_stringComparator);
    /**
     * The set of default values and enum values for attributes
     * declared in the schema
     */
    Set<String> attributeValues = new java.util.TreeSet(_stringComparator);
    /**
     * The set of default values and enums values for text content 
     * declared in the schema
     */
    Set<String> textContentValues = new java.util.TreeSet(_stringComparator);
    
    // The list of URLs to schema
    private List<URL> _schema;

    // True if if default values and enums of elements 
    // and attributes should be collected
    private boolean _collectValues;
    
    /*
     * Construct a schema processor given a URL to a schema.
     * 
     * @param schema the URL to the schema.
     * @param collectValues true if default values and enums of elements
     *        and attributes should be collected.
     */
    public SchemaProcessor(URL schema, boolean collectValues) {
        _schema = new ArrayList();
        _schema.add(schema);
        _collectValues = collectValues;
    }
    
    /*
     * Construct a schema processor given a list URLs to schema.
     * 
     * @param schema the list URLs to schema.
     * @param collectValues true if default values and enums of elements
     *        and attributes should be collected.
     */
    public SchemaProcessor(List<URL> schema, boolean collectValues) {
        _schema = schema;
        _collectValues = collectValues;
    }
    
    // XSVisitor, XSSimpleTypeVisitor
    
    public void annotation(XSAnnotation xSAnnotation) {
    }

    public void attGroupDecl(XSAttGroupDecl decl) {
        Iterator itr = decl.iterateAttGroups();
        while (itr.hasNext()) {
            addAttribute((XSAttGroupDecl) itr.next());
        }

        itr = decl.iterateDeclaredAttributeUses();
        while (itr.hasNext()) {
            attributeUse((XSAttributeUse) itr.next());
        }
    }

    public void attributeDecl(XSAttributeDecl xSAttributeDecl) {
        if (xSAttributeDecl.getDefaultValue() != null) {
            addAttributeValue(xSAttributeDecl.getDefaultValue().value);
        }
        
        addAttribute(xSAttributeDecl);

        if (xSAttributeDecl.getType().isRestriction()) {
            for (XSFacet f : xSAttributeDecl.getType().asRestriction().getDeclaredFacets(XSFacet.FACET_ENUMERATION)) {
                addAttributeValue(f.getValue().value);
            }
        }                
    }

    public void attributeUse(XSAttributeUse use) {
        XSAttributeDecl decl = use.getDecl();
        
        attributeDecl(decl);
    }

    public void complexType(XSComplexType type) {
        
        if (type.getContentType().asSimpleType() != null) {
            XSType baseType = type.getBaseType();
            
            if (type.getDerivationMethod() != XSType.RESTRICTION) {
                // check if have redefine tag
                if ((type.getTargetNamespace().compareTo(
                        baseType.getTargetNamespace()) ==
                        0)
                        && (type.getName().compareTo(baseType.getName()) == 0)) {
                    baseType.visit(this);
                }
            }
        } else {
            XSComplexType baseType = type.getBaseType().asComplexType();

            if (type.getDerivationMethod() != XSType.RESTRICTION) {
                // check if have redefine tag
                if ((type.getTargetNamespace().compareTo(
                        baseType.getTargetNamespace()) ==
                        0)
                        && (type.getName().compareTo(baseType.getName()) == 0)) {
                    baseType.visit(this);
                }
                type.getExplicitContent().visit(this);
            }
            type.getContentType().visit(this);
        }
        
        Iterator itr = type.iterateAttGroups();
        while (itr.hasNext()) {
            addAttribute((XSAttGroupDecl) itr.next());
        }
        
        itr = type.iterateDeclaredAttributeUses();
        while (itr.hasNext()) {
            attributeUse((XSAttributeUse) itr.next());
        }
    }

    public void schema(XSSchema s) {
        for (XSAttGroupDecl groupDecl : s.getAttGroupDecls().values()) {
            attGroupDecl(groupDecl);
        }

        for (XSAttributeDecl attrDecl : s.getAttributeDecls().values()) {
            attributeDecl(attrDecl);
        }

        for (XSComplexType complexType : s.getComplexTypes().values()) {
            complexType(complexType);
        }

        for (XSElementDecl elementDecl : s.getElementDecls().values()) {
            elementDecl(elementDecl);
        }

        for (XSModelGroupDecl modelGroupDecl : s.getModelGroupDecls().values()) {
            modelGroupDecl(modelGroupDecl);
        }

        for (XSSimpleType simpleType : s.getSimpleTypes().values()) {
            simpleType(simpleType);
        }       
    }

    public void facet(XSFacet facet) {
        /*
         * TODO
         * Need to tell if this facet of the simple type is associated with an
         * attribute value or an text content of an element.
         * For the moment add the enumeration value to both sets.
         */
        if (facet.getName().equals(XSFacet.FACET_ENUMERATION)) {
            addAttributeValue(facet.getValue().value);
            addTextContentValue(facet.getValue().value);
        }        
    }

    public void notation(XSNotation xSNotation) {
    }

    public void identityConstraint(XSIdentityConstraint xSIdentityConstraint) {
    }

    public void xpath(XSXPath xSXPath) {
    }

    public void wildcard(XSWildcard xSWildcard) {
    }

    public void modelGroupDecl(XSModelGroupDecl decl) {
       modelGroup(decl.getModelGroup());
    }

    public void modelGroup(XSModelGroup group) {
        final int len = group.getSize();
        for (int i = 0; i < len; i++) {
            particle(group.getChild(i));
        }
    }

    public void elementDecl(XSElementDecl type) {
        if (type.getDefaultValue() != null) {
            addTextContentValue(type.getDefaultValue().value);
        }
        
        if (type.getType().isSimpleType() && type.getType().asSimpleType().isRestriction()) {
            XSSimpleType s = type.getType().asSimpleType();
            for (XSFacet f : s.asRestriction().getDeclaredFacets(XSFacet.FACET_ENUMERATION)) {
                addTextContentValue(f.getValue().value);
            }
        }
        
        addElement(type);        
    }

    public void particle(XSParticle part) {
        part.getTerm().visit(this);
    }

    public void empty(XSContentType xSContentType) {
    }    
    
    public void simpleType(XSSimpleType type) {
        type.visit((XSSimpleTypeVisitor)this);
    }

    public void listSimpleType(XSListSimpleType xSListSimpleType) {
    }

    public void unionSimpleType(XSUnionSimpleType type) {
        final int len = type.getMemberSize();
        for (int i = 0; i < len; i++) {
            XSSimpleType member = type.getMember(i);
            if (member.isLocal()) {
                simpleType(member);
            }
        }        
    }

    public void restrictionSimpleType(XSRestrictionSimpleType type) {
        XSSimpleType baseType = type.getSimpleBaseType();
        if (baseType == null) {
            return;
        }
        
        if (baseType.isLocal()) {
            simpleType(baseType);
        }
       
        Iterator itr = type.iterateDeclaredFacets();
        while (itr.hasNext()) {
            facet((XSFacet) itr.next());
        }
    }
    
    private class ErrorHandlerImpl implements ErrorHandler {
        public void warning(SAXParseException e) throws SAXException {
            System.out.println("WARNING");
            e.printStackTrace();
        }

        public void error(SAXParseException e) throws SAXException {
            System.out.println("ERROR");
            e.printStackTrace();
        }

        public void fatalError(SAXParseException e) throws SAXException {
            System.out.println("FATAL ERROR");
            e.printStackTrace();
        }
    }
    
    /**
     * Process the schema to produce the set of properties of
     * information items.
     */
    public void process() throws Exception {
        XSOMParser parser = new XSOMParser();
        parser.setErrorHandler(new ErrorHandlerImpl());
        
        for (URL u : _schema) {
            InputSource s = new InputSource(u.openStream());
            s.setSystemId(u.toString());
            parser.parse(s);
        }
        
        XSSchemaSet sset = parser.getResult();
        Iterator<XSSchema> is = sset.iterateSchema();
        while (is.hasNext()) {
            XSSchema s = is.next();
            s.visit(this);
        }
    }
    
    private void addAttribute(XSDeclaration d) {
        QName q = getQName(d);
        attributes.add(q);
        addNamespaceAndLocalName(q);
    }
    
    private void addElement(XSDeclaration d) {
        QName q = getQName(d);
        elements.add(q);
        addNamespaceAndLocalName(q);
    }
    
    private void addNamespaceAndLocalName(QName q) {
        localNames.add(q.getLocalPart());
        if (q.getNamespaceURI() != XMLConstants.NULL_NS_URI &&
                !q.getNamespaceURI().equals("http://www.w3.org/XML/1998/namespace")) {
            // Ignore the XML namespace
            namespaces.add(q.getNamespaceURI());
        }
    }
    
    private void addAttributeValue(String s) {
        if (_collectValues) attributeValues.add(s);
    }
    
    private void addTextContentValue(String s) {
        if (_collectValues) textContentValues.add(s);
    }
    
    private QName getQName(XSDeclaration d) {
        String n = d.getTargetNamespace();
        String l = d.getName();
        return new QName(n, l);
    }
    
    private void print() {
        System.out.println("Namespaces");
        System.out.println("----------");
        int i = 1;
        for (String s : namespaces) {
            System.out.println((i++) + ": " + s);
        }
        System.out.println("LocaNames");
        System.out.println("---------");
        i = 1;
        for (String s : localNames) {
            System.out.println((i++) + ": " + s);
        }
        System.out.println("Elements");
        System.out.println("--------");
        i = 1;
        for (QName q : elements) {
            System.out.println((i++) + ": " + q);
        }
        System.out.println("Attributes");
        System.out.println("----------");
        i = 1;
        for (QName q : attributes) {
            System.out.println((i++) + ": " + q);
        }
    }
    
    public static void main(String[] args) throws Exception {
        SchemaProcessor v = new SchemaProcessor(new File(args[0]).toURL(), true);
        v.process();
        v.print();
    }
}
