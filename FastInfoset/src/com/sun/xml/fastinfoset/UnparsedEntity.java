/*
 * UnparsedEntityDeclaration.java
 *
 * Created on January 20, 2005, 3:15 PM
 */

package com.sun.xml.fastinfoset;

/**
 *
 * @author SONY USER
 */
public class UnparsedEntity extends Notation {
    public final String notationName;
    
    /** Creates a new instance of UnparsedEntityDeclaration */
    public UnparsedEntity(String _name, String _systemIdentifier, String _publicIdentifier, String _notationName) {
        super(_name, _systemIdentifier, _publicIdentifier);
        notationName = _notationName;
    }
    
}
