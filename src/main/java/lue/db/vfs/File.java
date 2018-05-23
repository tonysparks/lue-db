/*
 * see license.txt
 */
package lue.db.vfs;

/**
 * Virtual File representation
 * 
 * @author Tony
 *
 */
public interface File {

    /**
     * The path file name
     * 
     * @return The file name
     */
    Path path();
    
    /**
     * The number of bytes this file contains
     * 
     * @return the number of bytes of this file
     */
    long length();
    
    /**
     * Extends or contracts the file size
     * 
     * @param newLength
     */
    void length(long newLength);
    
    /**
     * Seek to a position in the file
     * 
     * @param position
     */
    void seek(long position);
    
    /**
     * Get the current position in the file
     * 
     * @return Get the current position in the file
     */
    long position();
    
    byte readByte();
    short readShort();
    int readInt();
    long readLong();
    float readFloat();
    double readDouble();
    
    void writeByte(byte value);
    void writeShort(short value);
    void writeInt(int value);
    void writeLong(long value);
    void writeFloat(float value);
    void writeDouble(double value);
    
    /**
     * Read bytes from this file
     * 
     * @param buffer
     * @param offset
     * @param length
     * @return the number of bytes read
     */
    int readBytes(byte[] buffer, int offset, int length);
    
    /**
     * Write bytes to this file
     * 
     * @param buffer
     * @param offset
     * @param length
     */    
    void writeBytes(byte[] buffer, int offset, int length);
    
    
    /**
     * Flush to disk
     */
    void flush();      
    
    /**
     * Closes the file
     */
    void close();
}
