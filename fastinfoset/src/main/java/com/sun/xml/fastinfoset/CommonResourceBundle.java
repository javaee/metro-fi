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
package com.sun.xml.fastinfoset;

import java.util.Locale;
import java.util.ResourceBundle;

/** Resource bundle implementation for localized messages.
 */
public class CommonResourceBundle extends AbstractResourceBundle {

    public static final String BASE_NAME = "com.sun.xml.fastinfoset.resources.ResourceBundle";
    private static CommonResourceBundle instance = null;
    private static Locale locale = null;
    private ResourceBundle bundle = null;
    
    protected CommonResourceBundle() {
        // Load the resource bundle of default locale
        bundle = ResourceBundle.getBundle(BASE_NAME);
    }

    protected CommonResourceBundle(Locale locale) {
        // Load the resource bundle of specified locale
        bundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }

    public static CommonResourceBundle getInstance() {
        if (instance == null) {
            synchronized (CommonResourceBundle.class) {
                instance = new CommonResourceBundle();
                //**need to know where to get the locale
                //String localeString = CommonProperties.getInstance()
                //                  .getProperty("omar.common.locale");
                String localeString = null;
                locale = parseLocale(localeString);
            }
        }

        return instance;
    }
    
    public static CommonResourceBundle getInstance(Locale locale) {
        if (instance == null) {
            synchronized (CommonResourceBundle.class) {
                instance = new CommonResourceBundle(locale);
            }
        } else {
            synchronized (CommonResourceBundle.class) {
                if (CommonResourceBundle.locale != locale) {
                    instance = new CommonResourceBundle(locale);
                }
            }
	}
        return instance;
    }


    public ResourceBundle getBundle() {
        return bundle;
    }
    public ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BASE_NAME, locale);
    }
    
}
