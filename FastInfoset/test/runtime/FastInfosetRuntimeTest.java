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
package runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import junit.framework.TestCase;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class FastInfosetRuntimeTest extends TestCase {
    
    public FastInfosetRuntimeTest(String testName) {
        super(testName);
    }

    public void testClassloader() {
        Method m = null;
        try {
            Class clazz = Class.forName(
                    "com.sun.fastinfoset.runtime.FastInfosetRuntime");
            m = clazz.getDeclaredMethod("getClassLoader", 
                    new Class[] { ClassLoader.class });
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        
        Object o = null;
        try {
            o = m.invoke(null, new Object[] { FastInfosetRuntimeTest.class.getClassLoader() });
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        
        assertTrue(o instanceof ClassLoader);

        ClassLoader cl = (ClassLoader)o;
        Class clazz = null;
        try {
            clazz = Class.forName("com.sun.xml.fastinfoset.stax.StAXDocumentParser", true, cl);
            Constructor c = clazz.getConstructor(null);
            o = c.newInstance(null);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}