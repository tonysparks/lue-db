/*
 * see license.txt
 */
package lue.db.store;

/**
 * Storage block contains a fixed size set of data
 * 
 * @author Tony
 *
 */
public interface Block {

    long id();
    
    long header(int field);
    void header(int field, long value);
    
    int read(byte[] buffer, int bufferOffset, int srcOffset, int length);
    int write(byte[] buffer, int bufferOffset, int dstOffset, int length);
}
