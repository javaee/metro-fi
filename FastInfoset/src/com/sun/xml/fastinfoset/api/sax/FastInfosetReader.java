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

package com.sun.xml.fastinfoset.api.sax;

/**
 * Interface for reading an Fast Infoset document using callbacks.
 *
 * <p>FastInfosetReader is the interface that an Fast Infoset parser's 
 * SAX2 driver must implement. This interface allows an application to 
 * to register Fast Infoset specific event handlers for encoding algorithms.</p>
 *
 * <p>The support for encoding algorithms is categorised into four categories:
 *  1) None ; 2) Generic; 3) Primitive; and 4) Primitive and application. The 
 *  category of support is enabled by the registration (or not) of 
 *  EncodingAlgorithmContentHandler and PrimitiveTypeContentHandler as follows.<p>
 *
 * <p>"None": If the EncodingAlgorithmContentHandler and PrimitiveTypeContentHandler are 
 * not registered then encoding algorithm data for the built-in encoding
 * algorithms shall be notified as character data using the 
 * {@link org.xml.sax.ContentHandler#characters characters } method. The occurence
 * of encoding algorithm data for application defined encoding algorithms 
 * shall result in the throwing of a java.io.IOException.<p>
 *
 * <p>"Generic": If the EncodingAlgorithmContentHandler is registered and the 
 * PrimitiveTypeContentHandler is not registered then encoding algorithm data
 * for the built-in and application defined encoding algorithms shall be notified
 * through the callback methods of the EncodingAlgorithmContentHandler.<p>
 *
 * <p>"Primitive": If the EncodingAlgorithmContentHandler is not registered and the 
 * PrimitiveTypeContentHandler is registered then encoding algorithm data
 * for the built-in encoding algorithms shall be notified through the callback 
 * methods of the PrimitiveTypeContentHandler. The occurence of encoding 
 * algorithm data for application defined encoding algorithms shall
 * result in the throwing of a java.io.IOException.<p>
 *
 * <p>"Primitive and application": If the EncodingAlgorithmContentHandler 
 * and PrimitiveTypeContentHandler are registered then then encoding algorithm 
 * data for the built-in encoding algorithms shall be notified through the 
 * callback methods of the PrimitiveTypeContentHandler, and the encoding 
 * algorithm data for the application defined encoding algorithms shall be 
 * notified through the callback methods of the EncodingAlgorithmContentHandler.<p>
 *
 * @version 0.1
 * @see com.sun.xml.fastinfoset.api.sax.PrimitiveTypeContentHandler
 * @see com.sun.xml.fastinfoset.api.sax.EncodingAlgorithmContentHandler
 * @see org.xml.sax.XMLReader
 * @see org.xml.sax.ContentHandler
 */
public interface FastInfosetReader {
    /**
     * Allow an application to register an encoding algorithm handler.
     *
     * <p>If the application does not register an encoding algorithm handler, 
     * TODO</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The encoding algorithm handler.
     * @see #getEncodingAlgorithmContentHandler
     */
    public void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler handler);


    /**
     * Return the current encoding algorithm handler.
     *
     * @return The current encoding algorithm handler, or null if none
     *         has been registered.
     * @see #setEncodingAlgorithmContentHandler
     */
    public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler();

    /**
     * Allow an application to register a primitive type handler.
     *
     * <p>If the application does not register a primitive type handler, 
     * TODO</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The primitive type handler.
     * @see #gettPrimitiveTypeContentHandler
     */
    public void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler handler);


    /**
     * Return the current primitive type handler.
     *
     * @return The current primitive type handler, or null if none
     *         has been registered.
     * @see #setPrimitiveTypeContentHandler
     */
    public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler();
    
}
