/*
 * Types.java
 *
 * Created on June 16, 2006, 5:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.analysis.types;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public enum XSDataType {
    _UNSPECIFIED_,
    
    ANYTYPE,
    ANYSIMPLETYPE,
    DURATION,
    DATETIME,
    TIME,
    DATE,
    GYEARMONTH,
    GYEAR,
    GMONTHDAY,
    GDAY,
    GMONTH,
    STRING,
    NORMALIZEDSTRING,
    TOKEN,
    LANGUAGE,
    NAME,
    NCNAME,
    ID,
    IDREF,
    IDREFS,
    ENTITY,
    ENTITIES,
    NMTOKEN,
    NMTOKENS,
    BOOLEAN,
    BASE64BINARY,
    HEXBINARY,
    FLOAT,
    DECIMAL,
    INTEGER,
    NONPOSITIVEINTEGER,
    NEGATIVEINTEGER,
    LONG,
    INT,
    SHORT,
    BYTE,
    NONNEGATIVEINTEGER,
    UNSIGNEDLONG,
    UNSIGNEDINT,
    UNSIGNEDSHORT,
    UNSIGNEDBYTE,
    POSITIVEINTEGER,
    DOUBLE,
    ANYURI,
    QNAME,
    NOTATION;
    
    public static XSDataType create(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return _UNSPECIFIED_;
        }
    }
}
