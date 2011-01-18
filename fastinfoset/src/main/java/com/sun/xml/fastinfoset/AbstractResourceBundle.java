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
/*
 * ====================================================================
 *
 * This code is subject to the freebxml License, Version 1.1
 *
 * Copyright (c) 2001 - 2005 freebxml.org.  All rights reserved.
 *
 * $Header: /zpool01/javanet/scm/svn/tmp/cvs2svn/fi/FastInfoset/src/com/sun/xml/fastinfoset/AbstractResourceBundle.java,v 1.4 2006-09-15 12:02:53 sandoz Exp $
 * ====================================================================
 */
package com.sun.xml.fastinfoset;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * This class contains methods common to all *ResourceBundle classes
 *
 * @author  Paul Sterk / Sun Microsystems
 */
public abstract class AbstractResourceBundle extends ResourceBundle {
        
    public static final String LOCALE = "com.sun.xml.fastinfoset.locale";
    static String _bundleName = null;
    
    public static String getBundleName() {
        return _bundleName;
    }
    public static void setBundleName(String name) {
        _bundleName = name;
    }
    /**
     * Gets 'key' from ResourceBundle and format mesage using 'args'.
     *
     * @param key String key for message.
     * @param args Array of arguments for message.
     * @return String formatted message.
     */
    public String getString(String key, Object args[]) {
        String pattern = getBundle().getString(key);
        return MessageFormat.format(pattern, args);
    }
    
    /**
     * Gets 'key' from ResourceBundle and format mesage using 'args'.
     *
     * @param key String key for message.
     * @param args Array of arguments for message.
     * @param locale Locale in which to perform key lookup.
     * @return String formatted message.
     */
    public String getString(String key, Object args[], Locale locale) {
        String pattern = null;
        if (locale == null) {
            pattern = getBundle().getString(key);
        } else {
            pattern = getBundle(_bundleName, locale).getString(key);
        }
        return MessageFormat.format(pattern, args);
    }

    /**
     * Parse a locale string, return corresponding Locale instance.
     *
     * @param localeString
     * Name for the locale of interest.  If null, use VM default locale.
     * @return New Locale instance.
     */
    public static Locale parseLocale(String localeString) {        
        Locale locale = null;
        if (localeString == null) {
            locale = Locale.getDefault();
        } else {
            try {
                String[] args = localeString.split("_");
                if (args.length == 1) {
                    locale = new Locale(args[0]);
                } else if (args.length == 2) {
                    locale = new Locale(args[0], args[1]);
                } else if (args.length == 3) {
                    locale = new Locale(args[0], args[1], args[2]);
                }
            } catch (Throwable t) {
                locale = Locale.getDefault();
            }
        }
        return locale;
    }
    
    /**
     * Subclasses of this class must implement this method so that the 
     * correct resource bundle is passed to methods in this class
     *
     * @return
     *  A java.util.ResourceBundle from the subsclass. Methods in this class
     *  will use this reference.
     */
    public abstract ResourceBundle getBundle();
    

    /**
     * Since we are changing the ResourceBundle extension point, must
     * implement handleGetObject() using delegate getBundle().  Uses
     * getObject() call to work around protected access to
     * ResourceBundle.handleGetObject().  Happily, this means parent tree
     * of delegate bundle is searched for a match.
     *
     * Implements java.util.ResourceBundle.handleGetObject; inherits that
     * javadoc information.
     *
     * @see java.util.ResourceBundle#handleGetObject(String)
     */
    protected Object handleGetObject(String key) {
       return getBundle().getObject(key);
    }

    /**
     * Since we are changing the ResourceBundle extension point, must
     * implement getKeys() using delegate getBundle().
     *
     * Implements java.util.ResourceBundle.getKeys; inherits that javadoc
     * information.
     *
     * @see java.util.ResourceBundle#getKeys()
     */
    public final Enumeration getKeys() {
       return getBundle().getKeys();
    }
}
