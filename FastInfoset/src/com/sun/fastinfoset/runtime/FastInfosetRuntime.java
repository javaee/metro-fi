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

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Fast Infoset runtime class to obtain a class loader that hides the location
 * of the Fast Infoset implementation.
 * <p>
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public final class FastInfosetRuntime {
    /**
     * Specify the packages that will be masked (or hidden) when loading 
     * classes from a 'side' directory.
     */
    private static List maskedPackages = Arrays.asList(new String[]{
        "com.sun.xml.fastinfoset.",
        "org.jvnet.fastinsfoset.",
    });
    
    /**
     * Map containing parent->child class loaders
     */
    private static Map loaders = new HashMap();
    
    /**
     * The side directory where the Fast Infoset implementation resides.
     */
    private static String sideDirectory;
            
    /**
     * Get the class loader that loads the Fast Infoset implementation
     * classes, using the class load of {@link FastInfosetRuntime} as the
     * parent class loader.
     */
    public static ClassLoader getClassLoader() {
        return getClassLoader(
                FastInfosetRuntime.class.getClassLoader());
    }
    
    /**
     * Get the class loader that loads the Fast Infoset implementation
     * classes.
     * <p>
     * If there is a properties file "runtime.properties" in the location
     * "com.sun.fastinfoset.runtime" that contains the property "sideDirectory"
     * then a class loader will be returned that will load the classes from
     * the side directory that is specified by value of the "sideDirectory"
     * property.
     * <p>
     * If a Fast Infoset implementation is under a side directory then the
     * implementation is essentially hidden from the parent class loader,
     * which is passed as an argument to this method.
     *
     * @param parent the parent class loader of the child class loader return.
     *        The child returned may be the same instance as the parent.
     */
    public static synchronized ClassLoader getClassLoader(ClassLoader parent) {
        // Check to see if a class loader that is a child of this
        // parent has previously been created
        ClassLoader child = (ClassLoader)loaders.get(parent);
        if (child != null) return child;

        // Obtain the sideDirectory property value
        if (sideDirectory == null) {
            Properties p = new Properties();
            InputStream is = parent.getResourceAsStream(
                    "com/sun/fastinfoset/runtime/runtime.properties");
            if (is != null) {
                try {
                    p.load(is);
                    sideDirectory = p.getProperty("sideDirectory");
                } catch (Exception e) {
                }
            }
            if (sideDirectory == null) sideDirectory = "";
        }

        if (sideDirectory.length() > 0) {
            // If the sideDirectory property is present is the masking class loader
            // and parallel world class loader to load the Fast Infoset
            // implementation
            child = new ParallelWorldClassLoader(new MaskingClassLoader(
                parent, maskedPackages), sideDirectory);
        } else {
            // If there is no source property use the parent class loader
            child = parent;
        }

        loaders.put(parent, child);
        return child;
    }
        
    /**
     * This class is cannot be instantiated
     */
    private FastInfosetRuntime() {}
}