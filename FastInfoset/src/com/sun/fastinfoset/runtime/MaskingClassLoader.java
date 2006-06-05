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
package com.sun.fastinfoset.runtime;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * {@link ClassLoader} that masks some packages available in
 * the parent class loader.
 *
 * @author Kohsuke Kawaguchi
 */
final class MaskingClassLoader extends ClassLoader {
    
    private final String[] prefixList;
    
    public MaskingClassLoader(ClassLoader parent, List prefixList) {
        super(parent);
        this.prefixList = (String[]) prefixList.toArray(
                new String[prefixList.size()]);
    }
    
    protected synchronized Class loadClass(String name, boolean resolve) 
            throws ClassNotFoundException {
        if(matchesPrefix(name))
            // we are hiding these packages from its parent.
            throw new ClassNotFoundException();
        return super.loadClass(name,resolve);
    }
    
    public URL getResource(String name) {
        if(matchesPrefix(name))
            return null;
        return super.getResource(name);
    }
    
    public Enumeration getResources(String name) throws IOException {
        if(matchesPrefix(name))
            return new Vector().elements();
        return super.getResources(name);
    }
    
    private boolean matchesPrefix(String name) {
        for (int i = 0; i < prefixList.length; i++ ) {
            String packprefix = prefixList[i];
            if (name.startsWith(packprefix) )
                return true;
        }
        return false;
    }
}