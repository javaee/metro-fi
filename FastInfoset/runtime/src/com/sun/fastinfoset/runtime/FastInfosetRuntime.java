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
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Fast Infoset runtime class to instansiate instances of, and call methods on,
 * parsers/serializer without a compile-time dependency.
 * <p>
 * 
 * @author Santiago.PericasGeertsen@sun.com
 * @author Paul.Sandoz@Sun.Com
 */
public final class FastInfosetRuntime {
    
    // StAXDocumentParser runtime fields
    
    /**
     * FI StAXDocumentParser constructor using reflection.
     */
    public static Constructor fiStAXDocumentParser_new;
    
    /**
     * FI <code>StAXDocumentParser.setInputStream()</code> method via reflection.
     */
    public static Method fiStAXDocumentParser_setInputStream;
    
    /**
     * FI <code>StAXDocumentParser.setStringInterning()</code> method via reflection.
     */
    public static Method fiStAXDocumentParser_setStringInterning;

    
    // StAXDocumentSerializer runtime fields
    
    /**
     * FI StAXDocumentSerializer constructor using reflection.
     */
    public static Constructor fiStAXDocumentSerializer_new;
    
    /**
     * FI <code>StAXDocumentSerializer.setOutputStream()</code> method via reflection.
     */
    public static Method fiStAXDocumentSerializer_setOutputStream;
    
    /**
     * FI <code>StAXDocumentSerializer.setEncoding()</code> method via reflection.
     */
    public static Method fiStAXDocumentSerializer_setEncoding;
    
    
    // DOMDocumentParser runtime fields

    /**
     * FI DOMDocumentParser constructor using reflection.
     */
    private static Constructor fiDOMDocumentParser_new;
    
    /**
     * FI <code>DOMDocumentParser.parse()</code> method via reflection.
     */
    private static Method fiDOMDocumentParser_parse;
    
    
    // DOMDocumentSerializer runtime fields
    
    /**
     * FI DOMDocumentSerializer constructor using reflection.
     */
    private static Constructor fiDOMDocumentSerializer_new;
    
    /**
     * FI <code>FastInfosetSource.serialize(Document)</code> method via reflection.
     */
    private static Method fiDOMDocumentSerializer_serialize;
    
    /**
     * FI <code>FastInfosetSource.setOutputStream(OutputStream)</code> method via reflection.
     */
    private static Method fiDOMDocumentSerializer_setOutputStream;

    
    // FastInfosetSource runtime fields
    
    /**
     * FI FastInfosetSource class.
     */
    private static Class fiFastInfosetSource;

    /**
     * FI FastInfosetSource constructor using reflection.
     */
    private static Constructor fiFastInfosetSource_new;
    
    /**
     * FI <code>FastInfosetSource.getInputStream()</code> method via reflection.
     */
    private static Method fiFastInfosetSource_getInputStream;
       
    /**
     * FI <code>FastInfosetSource.setInputSTream()</code> method via reflection.
     */
    private static Method fiFastInfosetSource_setInputStream;

    
    // FastInfosetResult runtime fields
    
    /**
     * FI FastInfosetResult class using reflection.
     */
    private static Class fiFastInfosetResult;
    
    /**
     * FI FastInfosetResult constructor using reflection.
     */
    private static Constructor fiFastInfosetResult_new;
    
    /**
     * FI <code>FastInfosetResult.getOutputSTream()</code> method via reflection.
     */
    private static Method fiFastInfosetResult_getOutputStream;
    
    /**
     * Get the class loader using the classloader of {@link FastInfoset}
     * that loads the Fast Infoset implementation classes.
     */
    private static ClassLoader getClassLoader() {
        return getClassLoader(
                FastInfosetRuntime.class.getClassLoader());
    }
    
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
     * Get the class loader that loads the Fast Infoset implementation
     * classes.
     * <p>
     * This method will utilize the {@link MaskingClassLoader} and the 
     * {@link ParallelWorldClassLoader} iff there is a 
     * "com.sun.fastinfoset.runtime.runtime.properties" file present that 
     * contains the property "source", otherwise the parent class loader
     * is returned.
     * <p>
     * The value of the property "source" will be the side directory from 
     * which the {@link ParallelWorldClassLoader} will load the Fast Infoset
     * implementation classes. The {@link MaskingClassLoader} will ensure that
     * the side loaded Fast Infoset implementation and a non-side loaded
     * implementation can co-exist within the same JVM.
     *
     * @param parent the parent class loader of the child class loader return.
     *        The child returned may be the same instance as the parent.
     */
    public static synchronized ClassLoader getClassLoader(ClassLoader parent) {
        // Check to see if this class loader has been used before
        ClassLoader child = (ClassLoader)loaders.get(parent);
        if (child != null) return child;

        // Obtain the properties file and source property
        String source = null;
        Properties p = new Properties();
        InputStream is = parent.getResourceAsStream(
                "com/sun/fastinfoset/runtime/runtime.properties");
        if (is != null) {
            try {
                p.load(is);
                source = p.getProperty("source");
            } catch (Exception e) {
            }
        }
        
        if (source != null && source.length() > 0) {
            // If the source property is present is the masking class loader
            // and parallel world class loader to load the Fast Infoset
            // implementation
            child = new ParallelWorldClassLoader(new MaskingClassLoader(
                parent, maskedPackages), source);
        } else {
            // If there is no source property use the parent class loader
            child = parent;
        }

        loaders.put(parent, child);
        return child;
    }
    
    /**
     * Initialize the static variables to runtime information.
     */
    static {
        // Get the class loader to use
        ClassLoader cl = getClassLoader();
        try {            
            Class clazz = null;
            
            
            // Initialize runtime fields for StAXDocumentParser
            
            clazz = Class.forName("com.sun.xml.fastinfoset.stax.StAXDocumentParser", 
                    true, cl);
            fiStAXDocumentParser_new = clazz.getConstructor(null);
            fiStAXDocumentParser_setInputStream =
                clazz.getMethod("setInputStream", 
                    new Class[] { java.io.InputStream.class });
            fiStAXDocumentParser_setStringInterning =
                clazz.getMethod("setStringInterning", 
                    new Class[] { boolean.class });

            
            // Initialize runtime fields for StAXDocumentSerializer
            
            clazz =
                Class.forName("com.sun.xml.fastinfoset.stax.StAXDocumentSerializer",
                    true, cl);
            fiStAXDocumentSerializer_new = clazz.getConstructor(null);
            fiStAXDocumentSerializer_setOutputStream =
                clazz.getMethod("setOutputStream",
                    new Class[] { java.io.OutputStream.class });
            fiStAXDocumentSerializer_setEncoding =
                clazz.getMethod("setEncoding", 
                    new Class[] { String.class });
            
            
            // Initialize runtime fields for DOMDocumentParser
            
            clazz =
                Class.forName("com.sun.xml.fastinfoset.dom.DOMDocumentParser",
                    true, cl);
            fiDOMDocumentParser_new = clazz.getConstructor(null);
            fiDOMDocumentParser_parse = clazz.getMethod("parse", 
                new Class[] { org.w3c.dom.Document.class, java.io.InputStream.class });
            
            
            // Initialize runtime fields for DOMDocumentSerializer
            
            clazz = Class.forName("com.sun.xml.fastinfoset.dom.DOMDocumentSerializer",
                    true, cl);
            fiDOMDocumentSerializer_new = clazz.getConstructor(null);
            fiDOMDocumentSerializer_serialize = clazz.getMethod("serialize", 
                new Class[] { org.w3c.dom.Node.class });
            fiDOMDocumentSerializer_setOutputStream = clazz.getMethod("setOutputStream",
                new Class[] { java.io.OutputStream.class });
            
            
            // Initialize runtime fields for FastInfosetSource
            
            clazz = Class.forName("org.jvnet.fastinfoset.FastInfosetSource",
                    true, cl);
            fiFastInfosetSource_new = clazz.getConstructor(
                new Class[] { java.io.InputStream.class });
            fiFastInfosetSource_getInputStream = clazz.getMethod("getInputStream", null);          
            fiFastInfosetSource_setInputStream = clazz.getMethod("setInputStream", 
                new Class[] { java.io.InputStream.class });          

            
            // Initialize runtime fields for FastInfosetResult
            
            clazz = Class.forName("org.jvnet.fastinfoset.FastInfosetResult",
                    true, cl);
            fiFastInfosetResult_new = clazz.getConstructor(
                new Class[] { java.io.OutputStream.class });
            fiFastInfosetResult_getOutputStream = clazz.getMethod("getOutputStream", null);           
            
        } catch (Exception e) {
            System.out.println(e);
            // falls through
        }
    }
    
    /**
     * Throw a RuntimeException.
     */
    private static void error() {
        throw new RuntimeException("Unable to locate Fast Infoset implementation");
    }
        
    
    // DOMDocumentParser

    /**
     * Instantiate a new instance of a DOMDocumentParser.
     *
     * @return an instance of a DOMDocumentParser.
     */
    public static Object DOMDocumentParser_new() throws Exception {
        if (fiDOMDocumentParser_new == null) error();
        
        return fiDOMDocumentParser_new.newInstance(null);
    }
    
    /**
     * Invoke by reflection the DOMDocumentParser.parse(Document, InputStream) 
     * method.
     * 
     * @param parser the DOMDocumentParser instance.
     * @param d the {@link Document} to append {@link Nodes} to.
     * @param s the {@link InputStream} to read the fast infoset document from.
     */
    public static void DOMDocumentParser_parse(Object parser, 
        Document d, InputStream s) throws Exception 
    {
        if (fiDOMDocumentParser_parse == null) {
            throw new RuntimeException("Unable to locate Fast Infoset implementation");
        }
        fiDOMDocumentParser_parse.invoke(parser, new Object[] { d, s });
    }
    
    
    // DOMDocumentSerializer
    
    /**
     * Instantiate a new instance of a DOMDocumentSerializer.
     *
     * @return an instance of a DOMDocumentSerializer.
     */
    public static Object DOMDocumentSerializer_new() throws Exception {
        if (fiDOMDocumentSerializer_new == null)  error();
        
        return fiDOMDocumentSerializer_new.newInstance(null);
    }
    
    /**
     * Invoke by reflection the DOMDocumentSerializer.serialize(Node) 
     * method.
     * 
     * @param serializer the DOMDocumentSerializer instance.
     * @param node the {@link Node} to serialize.
     */
    public static void DOMDocumentSerializer_serialize(Object serializer, Node node)
        throws Exception
    {
        if (fiDOMDocumentSerializer_serialize == null) error();
        
        fiDOMDocumentSerializer_serialize.invoke(serializer, new Object[] { node });
    }
    
    /**
     * Invoke by reflection the DOMDocumentSerializer.setOutputStream(OutputStream) 
     * method.
     * 
     * @param serializer the DOMDocumentSerializer instance.
     * @param os the {@link OutputStream} to write the fast infoset document to.
     */
    public static void DOMDocumentSerializer_setOutputStream(Object serializer,
        OutputStream os) throws Exception
    {
        if (fiDOMDocumentSerializer_setOutputStream == null) error();
        
        fiDOMDocumentSerializer_setOutputStream.invoke(serializer, new Object[] { os });
    }
    
    
    // FastInfosetSource
    
    /**
     * Check if a source is an instance of ""org.jvnet.fastinfoset.FastInfosetSource".
     *
     * @param source the source
     * @return true if source is an instance of "org.jvnet.fastinfoset.FastInfosetSource".
     */
    public static boolean isFastInfosetSource(Source source) {
        return source.getClass().getName().equals(
            "org.jvnet.fastinfoset.FastInfosetSource");
    }
    
    /**
     * Instantiate a new instance of a FastInfosetSource.
     *
     * @return an instance of a FastInfosetSource.
     */
    public static Source FastInfosetSource_new(InputStream is) 
        throws Exception 
    {
        if (fiFastInfosetSource_new == null) error();
        
        return (Source) fiFastInfosetSource_new.newInstance(new Object[] { is });        
    }
    
    /**
     * Invoke by reflection the FastInfosetSource.getInputStream() 
     * method.
     * 
     * @param source the FastInfosetSource instance.
     * @return the {@link InputStream} instance.
     */
    public static InputStream FastInfosetSource_getInputStream(Source source) 
        throws Exception 
    {
        if (fiFastInfosetSource_getInputStream == null) error();
        
        return (InputStream) fiFastInfosetSource_getInputStream.invoke(source, null);
    }
    
    /**
     * Invoke by reflection the FastInfosetSource.setInputStream() 
     * method.
     * 
     * @param source the FastInfosetSource instance.
     * @param is the {@link InputStream} instance to set.
     */
    public static void FastInfosetSource_setInputStream(Source source,
        InputStream is) throws Exception
    {
        if (fiFastInfosetSource_setInputStream == null) error();
        
        fiFastInfosetSource_setInputStream.invoke(source, new Object[] { is });
    }
    
    
    // FastInfosetResult
    
    /**
     * Check if a result is an instance of ""org.jvnet.fastinfoset.FastInfosetResult".
     *
     * @param result the result
     * @return true if result is an instance of "org.jvnet.fastinfoset.FastInfosetResult".
     */
    public static boolean isFastInfosetResult(Result result) {
        return result.getClass().getName().equals(
            "org.jvnet.fastinfoset.FastInfosetResult");        
    }
    
    /**
     * Instantiate a new instance of a FastInfosetResult.
     *
     * @return an instance of a FastInfosetResult.
     */
    public static Result FastInfosetResult_new(OutputStream os) 
        throws Exception 
    {
        if (fiFastInfosetResult_new == null) error();
        
        return (Result) fiFastInfosetResult_new.newInstance(new Object[] { os });                
    }
    
    /**
     * Invoke by reflection the FastInfosetResult.getOutputStream() 
     * method.
     * 
     * @param result the FastInfosetResult instance.
     * @return the {@link OutputStream} instance.
     */
    public static OutputStream FastInfosetResult_getOutputStream(Result result) 
        throws Exception 
    {
        if (fiFastInfosetResult_getOutputStream == null) error();
        
        return (OutputStream) fiFastInfosetResult_getOutputStream.invoke(result, null);
    }
    
    /**
     * This class is cannot be instantiated
     */
    private FastInfosetRuntime() {}
}
