/*
 * Japex ver. 0.1 software ("Software")
 * 
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This Software is distributed under the following terms:
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc., 'Java', 'Java'-based names,
 * nor the names of contributors may be used to endorse or promote products
 * derived from this Software without specific prior written permission.
 * 
 * The Software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE
 * AS A RESULT OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE
 * LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED
 * AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package com.sun.japex;

import java.net.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

class JapexClassLoader extends URLClassLoader {
    
    /**
     * Set the parent class loader to null in order to force the use of 
     * the bootstrap classloader. The bootstrap class loader does not 
     * have access to the system's class path.
     */ 
    public JapexClassLoader(String classPath) {
        super(new URL[0], null);
        initialize(classPath);
    }
    
    public Class findClass(String name) throws ClassNotFoundException {
        // Delegate when loading Japex classes, excluding JDSL drivers
        if (name.startsWith("com.sun.japex.") && !name.startsWith("com.sun.japex.jdsl.")) {
            return DriverImpl.class.getClassLoader().loadClass(name);
        }

        // Otherwise, use class loader based on japex.classPath only
        return super.findClass(name);
    }
    
    public void addURL(URL url) {
        super.addURL(url);
    }
    
    public JapexDriverBase getJapexDriver(String className) 
        throws ClassNotFoundException 
    {        
        try {
            // Use 'this' class loader here
            Class clazz = Class.forName(className, true, this);
            return (JapexDriverBase) clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (ClassCastException e) {
            throw new RuntimeException("Class '" + className 
                + "' must extend '" + JapexDriverBase.class.getName() + "'");
        }
    }
    
    /**
     * Initializes the Japex class loader. A single class loader will be
     * created for all drivers. Thus, if japex.classPath is defined as
     * a driver's property, it will be ignored.
     */ 
    private void initialize(String classPath) {
        if (classPath == null) {
            return;
        }
        
        String pathSep = System.getProperty("path.separator");
        String fileSep = System.getProperty("file.separator");
        StringTokenizer tokenizer = new StringTokenizer(classPath, pathSep); 
        
        // TODO: Ensure that this code works on Windows too!
	while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();            
            try {
                boolean lookForJars = false;
                
                // Strip off '*.jar' at the end if present
                if (path.endsWith("*.jar")) {
                    int k = path.lastIndexOf('/');
                    path = (k >= 0) ? path.substring(0, k + 1) : "./";
                    lookForJars = true;
                }
                
                // Create a file from the resulting path
                File file = new File(path);
                
                // If a directory, add all '.jar'
                if (file.isDirectory() && lookForJars) {
                    String children[] = file.list(
                        new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".jar");
                            }
                        });
                        
                    for (String c : children) {
                        addURL(new File(path + fileSep + c).toURL());
                    }
                }
                else {
                    addURL(file.toURL());
                }
            }
            catch (MalformedURLException e) {
                // ignore
            }
        }        
    }

}    
       