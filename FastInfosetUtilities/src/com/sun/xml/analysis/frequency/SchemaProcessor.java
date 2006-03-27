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
import com.sun.xml.xsom.visitor.XSTermVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * A Schema processor that collects the namespaces, local names, elements
 * and attributes declared in a set of schema.
 *
 * TODO: add default values for attribute/element simple content.
 * @author Paul.Sandoz@Sun.Com
 */
public class SchemaProcessor implements XSVisitor, XSSimpleTypeVisitor {

    /**
     * The set of elements declared in the schema
     */
    public Set<QName> elements = new java.util.HashSet();
    /**
     * The set of attributes declared in the schema
     */
    public Set<QName> attributes = new java.util.HashSet();
    /**
     * The set of local names declared in the schema
     */
    public Set<String> localNames = new java.util.HashSet();
    /**
     * The set of namespaces declared in the schema
     */
    public Set<String> namespaces = new java.util.HashSet();
    
    // The list of URLs to schema
    private List<URL> _schema;

    /*
     * Construct a schema processor given a URL to a schema.
     * 
     * @param schema the URL to the schema.
     */
    public SchemaProcessor(URL schema) {
        _schema = new ArrayList();
        _schema.add(schema);
    }
    
    /*
     * Construct a schema processor given a list URLs to schema.
     * 
     * @param schema the list URLs to schema.
     */
    public SchemaProcessor(List<URL> schema) {
        _schema = schema;
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
        addAttribute(xSAttributeDecl);
    }

    public void attributeUse(XSAttributeUse use) {
        XSAttributeDecl decl = use.getDecl();
        addAttribute(decl);        
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
        addElement(type);        
    }

    public void particle(XSParticle part) {
        final XSVisitor localThis = this;
        
        part.getTerm().visit(new XSTermVisitor() {
            public void elementDecl(XSElementDecl decl) {
                if (decl.isLocal()) {
                    localThis.elementDecl(decl);
                }
            }

            public void modelGroupDecl(XSModelGroupDecl decl) {
                // reference
            }

            public void modelGroup(XSModelGroup group) {
                localThis.modelGroup(group);
            }

            public void wildcard(XSWildcard wc) {
            }
        });    
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
    
    
    /**
     * Process the schema to produce the set of properties of
     * information items.
     */
    public void process() throws Exception {        
        for (URL u : _schema) {
            XSOMParser parser = new XSOMParser();
            parser.parse(u.openStream());

            XSSchemaSet sset = parser.getResult();

            Iterator<XSSchema> is = sset.iterateSchema();
            while (is.hasNext()) {
                XSSchema s = is.next();
                s.visit(this);
            }
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
        if (q.getNamespaceURI() != XMLConstants.NULL_NS_URI) {
            namespaces.add(q.getNamespaceURI());
        }
    }
    
    private QName getQName(XSDeclaration d) {
        String n = d.getTargetNamespace();
        String l = d.getName();
        return new QName(n, l);
    }
    
    private void print() {
        for (String s : namespaces) {
            System.out.println(s);
        }
        for (String s : localNames) {
            System.out.println(s);
        }
        for (QName q : elements) {
            System.out.println(q);
        }
        for (QName q : attributes) {
            System.out.println(q);
        }
    }
    
    public static void main(String[] args) throws Exception {
        SchemaProcessor v = new SchemaProcessor(new File(args[0]).toURL());
        v.process();
        v.print();
    }

}
