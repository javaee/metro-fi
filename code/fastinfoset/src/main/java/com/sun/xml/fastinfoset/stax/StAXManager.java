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

package com.sun.xml.fastinfoset.stax;

import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class StAXManager {
    protected static final String STAX_NOTATIONS = "javax.xml.stream.notations";
    protected static final String STAX_ENTITIES = "javax.xml.stream.entities";
    
    HashMap features = new HashMap();
    
    public static final int CONTEXT_READER = 1;
    public static final int CONTEXT_WRITER = 2;
    
    
    /** Creates a new instance of StAXManager */
    public StAXManager() {
    }
    
    public StAXManager(int context) {
        switch(context){
            case CONTEXT_READER:{
                initConfigurableReaderProperties();
                break;
            }
            case CONTEXT_WRITER:{
                initWriterProps();
                break;
            }
        }
    }
    
    public StAXManager(StAXManager manager){
        
        HashMap properties = manager.getProperties();
        features.putAll(properties);
    }
    
    private HashMap getProperties(){
        return features ;
    }
    
    private void initConfigurableReaderProperties(){
        //spec v1.0 default values
        features.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        features.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        features.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        features.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        features.put(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        features.put(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        features.put(XMLInputFactory.REPORTER, null);
        features.put(XMLInputFactory.RESOLVER, null);
        features.put(XMLInputFactory.ALLOCATOR, null);
        features.put(STAX_NOTATIONS,null );
    }
    
    private void initWriterProps(){
        features.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES , Boolean.FALSE);
    }
    
    /**
     * public void reset(){
     * features.clear() ;
     * }
     */
    public boolean containsProperty(String property){
        return features.containsKey(property) ;
    }
    
    public Object getProperty(String name){
        checkProperty(name);
        return features.get(name);
    }
    
    public void setProperty(String name, Object value){
        checkProperty(name);
        if (name.equals(XMLInputFactory.IS_VALIDATING) &&
                Boolean.TRUE.equals(value)){
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.validationNotSupported") +
                    CommonResourceBundle.getInstance().getString("support_validation"));
        } else if (name.equals(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES) &&
                Boolean.TRUE.equals(value)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.externalEntities") +
                    CommonResourceBundle.getInstance().getString("resolve_external_entities_"));
        }
        features.put(name,value);

    }
    
    public void checkProperty(String name) {
        if (!features.containsKey(name))
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{name}));
    }

    public String toString(){
        return features.toString();
    }
        
}
