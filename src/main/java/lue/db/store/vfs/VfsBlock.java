/*
 * see license.txt
 */
package lue.db.store.vfs;

import lue.db.store.Block;
import lue.db.vfs.File;

/**
 * Uses the virtual file system to read/write a block
 * 
 * @author Tony
 *
 */
public class VfsBlock implements Block {

    private File file;
    private long blockId;
    private long blockPosition;
    
    /**
     * @param file
     * @param blockId
     * @param blockSize
     */
    public VfsBlock(File file, long blockId, long blockPosition) {
        this.file = file;
        this.blockId = blockId;
        
        this.blockPosition = blockPosition; 
        this.file.seek(blockPosition);
    }

    @Override
    public long id() {
        return this.blockId;
    }

    @Override
    public long header(int field) {
        this.file.seek(this.blockPosition + field*Long.BYTES);
        return this.file.readLong();        
    }

    @Override
    public void header(int field, long value) {
        this.file.seek(this.blockPosition + field*Long.BYTES);
        this.file.writeLong(value);
    }

    @Override
    public int read(byte[] buffer, int bufferOffset, int srcOffset, int length) {
        this.file.seek(this.blockPosition + srcOffset);
        return this.file.readBytes(buffer, bufferOffset, length);
    }

    @Override
    public int write(byte[] buffer, int bufferOffset, int dstOffset, int length) {
        this.file.seek(this.blockPosition + dstOffset);
        this.file.writeBytes(buffer, bufferOffset, length);
        return length;
    }

}
