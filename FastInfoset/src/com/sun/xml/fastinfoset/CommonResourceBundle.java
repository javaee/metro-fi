
package com.sun.xml.fastinfoset;

import java.util.Locale;
import java.util.ResourceBundle;


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
                if (instance == null) {
                    instance = new CommonResourceBundle();
                    //**need to know where to get the locale
                    //String localeString = CommonProperties.getInstance()
                    //                  .getProperty("omar.common.locale");
                    String localeString = null;
                    locale = parseLocale(localeString);
                }
            }
        }

        return instance;
    }
    
    public static CommonResourceBundle getInstance(Locale locale) {
        if (instance == null) {
            synchronized (CommonResourceBundle.class) {
                if (instance == null) {
                    instance = new CommonResourceBundle(locale);
                }
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
