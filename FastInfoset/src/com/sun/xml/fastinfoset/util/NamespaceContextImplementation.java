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

package com.sun.xml.fastinfoset.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
final public class NamespaceContextImplementation implements NamespaceContext {
    private static int DEFAULT_SIZE = 8;
    
    private String[] prefixes = new String[DEFAULT_SIZE];
    private String[] namespaceURIs = new String[DEFAULT_SIZE];
    private int namespacePosition;
    
    private int[] contexts = new int[DEFAULT_SIZE];
    private int contextPosition;
    
    private int currentContext;
    
    public NamespaceContextImplementation() {
        prefixes[0] = "xml";
        namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
        prefixes[1] = "xmlns";
        namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
        
        currentContext = namespacePosition = 2;
    }
    
    
    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new IllegalArgumentException();
        
        prefix = prefix.intern();
        
        for (int i = namespacePosition - 1; i >= 0; i--) {
            final String declaredPrefix = prefixes[i];
            if (declaredPrefix == prefix) {
                return namespaceURIs[i];
            }
        }
        
        return "";
    }
    
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException();
        
        // namespaceURI = namespaceURI.intern();
        
        for (int i = namespacePosition - 1; i >= 0; i--) {
            final String declaredNamespaceURI = namespaceURIs[i];
            if (declaredNamespaceURI == namespaceURI || declaredNamespaceURI.equals(namespaceURI)) {
                final String declaredPrefix = prefixes[i];
                
                // Check if prefix is out of scope
                for (++i; i < namespacePosition; i++)
                    if (declaredPrefix == prefixes[i])
                        return null;
                
                return declaredPrefix;
            }
        }
        
        return null;
    }
    
    public String getNonDefaultPrefix(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException();
        
        // namespaceURI = namespaceURI.intern();
        
        for (int i = namespacePosition - 1; i >= 0; i--) {
            final String declaredNamespaceURI = namespaceURIs[i];
            if ((declaredNamespaceURI == namespaceURI || declaredNamespaceURI.equals(namespaceURI)) &&
                prefixes[i].length() > 0){
                final String declaredPrefix = prefixes[i];
                
                // Check if prefix is out of scope
                for (++i; i < namespacePosition; i++)
                    if (declaredPrefix == prefixes[i])
                        return null;
                
                return declaredPrefix;
            }
        }
        
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException();
        
        // namespaceURI = namespaceURI.intern();
        
        List l = new ArrayList();
        
        NAMESPACE_LOOP: for (int i = namespacePosition - 1; i >= 0; i--) {
            final String declaredNamespaceURI = namespaceURIs[i];
            if (declaredNamespaceURI == namespaceURI || declaredNamespaceURI.equals(namespaceURI)) {
                final String declaredPrefix = prefixes[i];
                
                // Check if prefix is out of scope
                for (int j = i + 1; j < namespacePosition; j++)
                    if (declaredPrefix == prefixes[j])
                        continue NAMESPACE_LOOP;
                
                l.add(declaredPrefix);
            }
        }
        
        return l.iterator();
    }
    
    
    public String getPrefix(int index) {
        return prefixes[index];
    }
    
    public String getNamespaceURI(int index) {
        return namespaceURIs[index];
    }
    
    public int getCurrentContextStartIndex() {
        return currentContext;
    }
    
    public int getCurrentContextEndIndex() {
        return namespacePosition;
    }
    
    public boolean isCurrentContextEmpty() {
        return currentContext == namespacePosition;
    }
    
    public void declarePrefix(String prefix, String namespaceURI) {
        prefix = prefix.intern();
        namespaceURI = namespaceURI.intern();
        
        // Ignore the "xml" or "xmlns" declarations
        if (prefix == "xml" || prefix == "xmlns")
            return;
        
        // Replace any previous declaration
        for (int i = currentContext; i < namespacePosition; i++) {
            final String declaredPrefix = prefixes[i];
            if (declaredPrefix == prefix) {
                prefixes[i] = prefix;
                namespaceURIs[i] = namespaceURI;
                return;
            }
        }
        
        if (namespacePosition == namespaceURIs.length)
            resizeNamespaces();
        
        // Add new declaration
        prefixes[namespacePosition] = prefix;
        namespaceURIs[namespacePosition++] = namespaceURI;
    }
    
    private void resizeNamespaces() {
        final int newLength = namespaceURIs.length * 3 / 2 + 1;
        
        String[] newPrefixes = new String[newLength];
        System.arraycopy(prefixes, 0, newPrefixes, 0, prefixes.length);
        prefixes = newPrefixes;
        
        String[] newNamespaceURIs = new String[newLength];
        System.arraycopy(namespaceURIs, 0, newNamespaceURIs, 0, namespaceURIs.length);
        namespaceURIs = newNamespaceURIs;
    }
    
    public void pushContext() {
        if (contextPosition == contexts.length)
            resizeContexts();
        
        contexts[contextPosition++] = currentContext = namespacePosition;
    }
    
    private void resizeContexts() {
        int[] newContexts = new int[contexts.length * 3 / 2 + 1];
        System.arraycopy(contexts, 0, newContexts, 0, contexts.length);
        contexts = newContexts;
    }
    
    public void popContext() {
        if (contextPosition > 0) {
            namespacePosition = currentContext = contexts[--contextPosition];
        }
    }
    
    public void reset() {
        currentContext = namespacePosition = 2;
    }
}