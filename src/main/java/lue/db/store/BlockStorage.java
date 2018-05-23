/*
 * see license.txt
 */
package lue.db.store;

/**
 * Block Storage
 * 
 * @author Tony
 *
 */
public interface BlockStorage {

    /**
     * The block header size in number of bytes
     * 
     * @return The block header size in number of bytes
     */
    int blockHeaderSize();
    
    /**
     * The block content size in number of bytes
     * 
     * @return The block content size in number of bytes
     */
    int blockContentSize();
    
    
    /**
     * The total block size in number of bytes
     * 
     * @return The total block size in number of bytes
     */
    default int blockSize() {
        return blockHeaderSize() + blockContentSize();
    }
    
    /**
     * Returns the number of blocks in storage
     * 
     * @return the number of blocks in storage
     */
    long numberOfBlocks();
    
    /**
     * Finds the {@link Block} by id
     * 
     * @param blockId
     * @return the {@link Block} is found, otherwise null
     */
    Block find(long blockId);
    
    /**
     * Creates a new {@link Block}
     * 
     * @return the new {@link Block}
     */
    Block createNew();
}
