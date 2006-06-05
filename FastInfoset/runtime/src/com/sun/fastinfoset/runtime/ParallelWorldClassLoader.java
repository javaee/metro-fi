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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Load classes/resources from a side folder, so that
 * classes of the same package can live in a single jar file.
 *
 * <p>
 * For example, with the following jar file:
 * <pre>
 *  /
 *  +- foo
 *     +- X.class
 *  +- bar
 *     +- X.class
 * </pre>
 * <p>
 * {@link ParallelWorldClassLoader}("foo/") would load <tt>X.class<tt> from
 * <tt>/foo/X.class</tt> (note that X is defined in the root package, not
 * <tt>foo.X</tt>.
 *
 * <p>
 * This can be combined with  {@link MaskingClassLoader} to mask classes which
 * are loaded by the parent
 * class loader so that the child class loader
 * classes living in different folders are loaded
 * before the parent class loader loads classes living the jar file publicly
 * visible
 * For example, with the following jar file:
 * <pre>
 *  /
 *  +- foo
 *     +- X.class
 *  +- bar
 *     +-foo
 *        +- X.class
 * </pre>
 * <p>
 * {@link ParallelWorldClassLoader}(MaskingClassLoader.class.getClassLoader())
 * would load <tt>foo.X.class<tt> from <tt>/bar/foo.X.class</tt> not the
 * <tt>foo.X.class<tt> in the publicly visible place in the jar file, thus
 * masking the parent classLoader from loading the class from
 * <tt>foo.X.class<tt> (note that X is defined in the  package foo, not
 * <tt>bar.foo.X</tt>.
 *
 * <p>
 * Don't use any JDK 5 classes in this class!
 *
 * @author Kohsuke Kawaguchi
 */
final class ParallelWorldClassLoader extends ClassLoader {
    
    /**
     * Strings like "prefix/", "abc/"...
     */
    private final String prefix;
    
    protected ParallelWorldClassLoader(ClassLoader parent,String prefix) {
        super(parent);
        this.prefix = prefix;
    }
    
    protected Class findClass(String name) throws ClassNotFoundException {
        StringBuffer sb = new StringBuffer(name.length()+prefix.length()+6);
        if (prefix != null && prefix.length() > 0)
            sb.append(prefix).append('/').append(name.replace('.','/')).append(".class");
        else
            //2.0 classes reside normally in the jar file without a prefix
            sb.append(name.replace('.','/')).append(".class");
        
        InputStream is = getParent().getResourceAsStream(sb.toString());
        if (is==null)
            throw new ClassNotFoundException(name);
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while((len=is.read(buf))>=0)
                baos.write(buf,0,len);
            
            buf = baos.toByteArray();
            int packIndex = name.lastIndexOf('.');
            if (packIndex != -1) {
                String pkgname = name.substring(0, packIndex);
                // Check if package already loaded.
                Package pkg = getPackage(pkgname);
                if (pkg == null) {
                    definePackage(pkgname, null, null, null, null, null, null, null);
                }
            }
            return defineClass(name,buf,0,buf.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name,e);
        }
    }
    
    protected URL findResource(String name) {
        return getParent().getResource(prefix+'/'+name);
    }
    
    protected Enumeration findResources(String name) throws IOException {
        return getParent().getResources(prefix+'.'+name);
    }
}