package org.jvnet.fastinfoset;

import java.io.OutputStream;

public abstract class AccessibleByteBufferOutputStream extends OutputStream {
    
    /**
     * Get the byte buffer.
     *
     * <p>The application shall modify the byte
     * array within the the range (getStart(), 
     * getStart() + getLength()].<p>
     * 
     * @return The underlying byte array to write to
     */
    public abstract byte[] getBuffer();
    
    /**
     * Get the byte buffer.
     *
     * <p>The application shall modify the byte
     * array within the the range (getStart(), 
     * getStart() + getLength()].<p>
     *
     * @param length The length of bytes of the byte array that need to be modified 
     * @return The byte array. Null is returned if the length
     * requested is too large.
     */
    public abstract byte[] getBuffer(int length);
    
    /** 
     * Commit bytes to the byte buffer.
     *
     * <p>The bytes committed shall be in the range (getStart(), getStart() + length].<p>
     *
     * <p>If the method completed without error then getStart() after the call will be
     * equal to getStart() + length before the call, and getLength() after the call will 
     * be equal to getLength() - length before the call.<p>
     * 
     * <p>The length shall be > 0 and <= getLength().<p>
     *
     * @param length The length of bytes in the byte array to commit. 
     */ 
    public abstract void commitBytes(int length);
    
    /** 
     * Get the start position to modify bytes in the byte buffer.
     *
     * @return  The start position in the byte array. 
     */ 
    public abstract int getStart();
    
    /** 
     * Get the length of bytes that can be modified in the byte buffer.
     *
     * @return  The length of bytes in the byte array. 
     */ 
    public abstract int getLength();
}
