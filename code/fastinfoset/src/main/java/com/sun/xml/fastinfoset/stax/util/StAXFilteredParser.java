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

package com.sun.xml.fastinfoset.stax.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.StreamFilter;
import com.sun.xml.fastinfoset.CommonResourceBundle;


public class StAXFilteredParser extends StAXParserWrapper {
    private StreamFilter _filter;
    
    /** Creates a new instance of StAXFilteredParser */
    public StAXFilteredParser() {
    }
    public StAXFilteredParser(XMLStreamReader reader, StreamFilter filter) {
        super(reader);
        _filter = filter;
    }
    
    public void setFilter(StreamFilter filter) {
        _filter = filter;
    }

    public int next() throws XMLStreamException
    {
        if (hasNext())
            return super.next();
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.noMoreItems"));
    }

    public boolean hasNext() throws XMLStreamException
    {
        while (super.hasNext()) {
            if (_filter.accept(getReader())) return true;
            super.next();
        }
        return false;
    }
    
}
