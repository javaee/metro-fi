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

import java.util.*;

import org.apache.tools.ant.taskdefs.Execute;

public class Params {
    
    final static int OUT_EXPR = 0;
    final static int IN_EXPR  = 1;
    final static String DELIMITER = "\uFFFE";      // A Unicode nonchar
    
    /**
     * Mapping between strings and values. Values could be of three 
     * possible types: String, Long or Double.
     */
    Map _mapping = new HashMap();
    
    /**
     * Default mapping used when a parameter is not defined in this
     * mapping.
     */
    Params _defaults = null;
    
    public Params() {
    }
    
    public Params(Properties props) {
        for (Iterator i = props.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            convertAndPut(key, props.getProperty(key));
        }
    }
    
    public Params(Params defaults) {
        _defaults = defaults;
    }

    private void convertAndPut(String key, String value) {
        try {
            long l = Long.parseLong(value);
            _mapping.put(key, new Long(l));
        }
        catch (NumberFormatException e1) {
            try {
                double d = Double.parseDouble(value);
                _mapping.put(key, new Double(d));
            }
            catch (NumberFormatException e2) {
                _mapping.put(key, value);                    
            }
        }        
    }
    
    public Object clone() {
        try {
            // Start with a shallow copy of the object
            Params clone = (Params) super.clone();

            // Make a deep copy of _mapping
            clone._mapping = new HashMap();
            for (Iterator i = _mapping.keySet().iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                clone._mapping.put(key, _mapping.get(key));
            }
            
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getParamOrDefault(String name) {
        Object value = _mapping.get(name);
        if (value == null && _defaults != null) {
            value = _defaults.getParamOrDefault(name);
        }
        return value;
    }
    
    public synchronized boolean hasParam(String name) {
        return getParamOrDefault(name) != null;
    }

    // -- String params --------------------------------------------------
    
    public synchronized void setParam(String name, String value) {
        convertAndPut(name, evaluate(name, value));
    }
    
    public synchronized String getParam(String name) {
        Object value = getParamOrDefault(name);
        if (value instanceof Long) {
            value = ((Long) value).toString();
        }
        else if (value instanceof Double) {
            value = Util.formatDouble(((Double) value).doubleValue());
        }
        return (String) value;
    }
    
    // -- Int params -----------------------------------------------------
    
    public synchronized void setIntParam(String name, int value) {
        _mapping.put(name, new Long(value));
    }
    
    public synchronized int getIntParam(String name) {
        Object value = getParamOrDefault(name);
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        else if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        else {
            try {
                return Integer.parseInt((String) value);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    // -- Long params ----------------------------------------------------
    
    public synchronized void setLongParam(String name, long value) {
        _mapping.put(name, new Long(value));
    }
    
    public synchronized long getLongParam(String name) {
        Object value = getParamOrDefault(name);
        if (value instanceof Long) {
            return ((Long) value).longValue();
        }
        else if (value instanceof Double) {
            return ((Double) value).longValue();
        }
        else {
            try {
                return Long.parseLong((String) value);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    // -- Double params --------------------------------------------------
    
    public synchronized void setDoubleParam(String name, double value) {
        _mapping.put(name, new Double(value));
    }
    
    public synchronized double getDoubleParam(String name) {
        Object value = getParamOrDefault(name);
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        else if (value instanceof Double) {
            return ((Double) value).doubleValue();
        }
        else {
            try {
                return Double.parseDouble((String) value);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // -- Other methods --------------------------------------------------
    
    public void serialize(StringBuffer buffer, int indent) {        
        // Serialize built-in params first (TODO sort)
        Iterator names = _mapping.keySet().iterator();
        
        while (names.hasNext()) {
            String name = (String) names.next();
            
            if (name.startsWith("japex.")) {
                String xmlName = name.substring(name.indexOf('.') + 1);                
                // Replace path.separator by a single space
                if (name.equals(Constants.CLASS_PATH)) {
                    buffer.append(Util.getSpaces(indent) 
                        + "<" + xmlName + ">" 
                        + getParam(name).replaceAll(
                              System.getProperty("path.separator"), "\n")
                        + "</" + xmlName + ">\n");                    
                }
                else {
                    buffer.append(Util.getSpaces(indent) 
                        + "<" + xmlName + ">" 
                        + getParam(name)
                        + "</" + xmlName + ">\n");
                }
            }
        }
        
        // Serialize driver-defined params (TODO sort)
        names = _mapping.keySet().iterator();
        
        while (names.hasNext()) {
            String name = (String) names.next();
            
            if (!name.startsWith("japex.")) {
                buffer.append(Util.getSpaces(indent) 
                    + "<" + name + " xmlns=\"\">" 
                    + getParam(name) 
                    + "</" + name + ">\n");                
            }
        }
    }

    /**
     * Expand expression of the form ${paramname}
     */
    private String evaluate(String name, String value) {
        StringTokenizer tokenizer = 
            new StringTokenizer(value, "${}", true);
        
        String t = null;
        StringBuffer buffer = new StringBuffer();
        int state = OUT_EXPR;
        
        while (tokenizer.hasMoreTokens()) {            
            t = tokenizer.nextToken();
            
            if (t.length() == 1) {
                switch (t.charAt(0)) {
                    case '$':
                        switch (state) {
                            case OUT_EXPR:
                                t = tokenizer.nextToken();
                                if (t.equals("{")) {
                                    buffer.append(DELIMITER);
                                    state = IN_EXPR;                                    
                                }
                                else {
                                    buffer.append("$" + t);
                                }
                                break;
                            case IN_EXPR:
                                buffer.append('$');
                                break;
                        }                                                
                        break;
                    case '}':
                        switch (state) {
                            case OUT_EXPR:
                                buffer.append('}');
                                break;
                            case IN_EXPR:
                                buffer.append(DELIMITER);
                                state = OUT_EXPR;
                                break;
                        }
                        break;
                    default:
                        buffer.append(t);
                        break;
                }
            }
            else {
                buffer.append(t);
            }
        }

        // Must be in OUT_EXPR at the end of parsing
        if (state != OUT_EXPR) {
            throw new RuntimeException("Error evaluating parameter '"
                + name + "' of value '" + value + "'");
        }
        
        /*
          * Second pass: split up buffer into literal and non-literal expressions.
          */
        tokenizer = new StringTokenizer(buffer.toString(), DELIMITER, true);
        StringBuffer result = new StringBuffer();
        
        while (tokenizer.hasMoreTokens()) {
            t = tokenizer.nextToken();
            
            if (t.equals(DELIMITER)) {
                String paramName = tokenizer.nextToken();
                String paramValue = getParam(paramName);
                if (paramValue != null) {
                    result.append(paramValue);
                }
                else {
                    // If not defined, check OS environment
                    paramValue = getEnvVariable(paramName);
                    if (paramValue != null) {
                        result.append(paramValue);                            
                    }
                    else {
                        throw new RuntimeException("Undefined parameter '"
                          + paramName + "'");        
                    }
                }                    
                
                tokenizer.nextToken();      // consume other delimiter
            }
            else {
                result.append(t);
            }
        }        
        
        return result.toString();
    }

    // Use Ant class to get environment
    private static Vector ENV = Execute.getProcEnvironment();
    
    private String getEnvVariable(String name) {
        for (int i = 0; i < ENV.size(); i++) {
            String def = (String) ENV.get(i);
            int k = def.indexOf('=');
            if (k > 0) {
                if (name.equals(def.substring(0, k))) {
                    return def.substring(k + 1);
                }
            }
        }
        return null;
    }

}
