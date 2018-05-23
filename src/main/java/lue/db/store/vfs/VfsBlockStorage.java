/*
 * see license.txt
 */
package lue.db.store.vfs;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lue.db.store.Block;
import lue.db.store.BlockStorage;
import lue.db.vfs.File;

/**
 * @author Tony
 *
 */
public class VfsBlockStorage implements BlockStorage {

    private final static int FORMAT_VERSION1 = 0x01;
    private final static int CURRENT_FORMAT_VERSION = FORMAT_VERSION1;
    
    private final static int V1_HEADER_SIZE = 
            Integer.BYTES   + // format version
            Integer.BYTES   + // header size
            Integer.BYTES   + // content size
            Integer.BYTES*5   // reserved for future use
            ;
    
    private int formatVersion;
    private int headerSize, contentSize;
    private File blockStorageFile;
    
    private Map<Long, Block> blocks;
    
    public VfsBlockStorage(File blockStorageFile) {
        this(blockStorageFile, 1, 1);
    }
    
    /**
     * @param headerSize
     * @param contentSize
     */    
    public VfsBlockStorage(File blockStorageFile, int headerSize, int contentSize) {
        this.blockStorageFile = blockStorageFile;
        this.headerSize = headerSize;
        this.contentSize = contentSize;
        
        if(this.headerSize < 1) {
            throw new IllegalArgumentException("Invalid header size: " + this.headerSize);
        }
        
        if(this.contentSize < 1) {
            throw new IllegalArgumentException("Invalid content size: " + this.contentSize);
        }
        
        this.blocks = new ConcurrentHashMap<>();
        
        if(isNew()) {
            writeHeader();
        }
        else {
            readHeader();
        }
    }
    
    private boolean isNew() {
        int version = 0;
        if(this.blockStorageFile.length() > 0) {
            this.blockStorageFile.seek(0);
            version = this.blockStorageFile.readInt();
        }
        
        return version == 0;
    }
    
    private void readHeader() {
        ByteBuffer buf = ByteBuffer.allocate(V1_HEADER_SIZE);
                
        this.blockStorageFile.seek(0);
        this.blockStorageFile.readBytes(buf.array(), 0, buf.capacity());
        
        this.formatVersion = buf.getInt();
        this.headerSize = buf.getInt();
        this.contentSize = buf.getInt();
    }
    
    private void writeHeader() {
        this.formatVersion = CURRENT_FORMAT_VERSION;
        
        ByteBuffer buf = ByteBuffer.allocate(getFormatHeaderSize());
        buf.putInt(this.formatVersion);
        buf.putInt(this.headerSize);
        buf.putInt(this.contentSize);
        
        this.blockStorageFile.length(getFormatHeaderSize()); 
        this.blockStorageFile.seek(0);
        this.blockStorageFile.writeBytes(buf.array(), 0, buf.position());
    }
    
    private int getFormatHeaderSize() {
        if(this.formatVersion == CURRENT_FORMAT_VERSION) {
            return V1_HEADER_SIZE;
        }
        
        throw new IllegalStateException();
    }
    
    private long getBlockPosition(long blockId) {
        long filePos = getFormatHeaderSize() + blockId * blockSize();
        return filePos;
    }
    
    @Override
    public int blockHeaderSize() {
        return this.headerSize;
    }

    @Override
    public int blockContentSize() {
        return this.contentSize;
    }

    @Override
    public long numberOfBlocks() {
        if(this.blockStorageFile.length() > getFormatHeaderSize()) {
            return (this.blockStorageFile.length()-getFormatHeaderSize()) / blockSize();
        }
        
        return 0;
    }
    
    @Override
    public Block find(long blockId) {
        Long id = blockId;
        
        /* has this block already been loaded and cached, if
         * it has, return it
         */
        if(this.blocks.containsKey(id)) {
            return this.blocks.get(id);
        }
        
        
        /* Determine if this block is stored on disk, if 
         * it is, calculate the location and return it
         */
        long filePos = getBlockPosition(blockId);
        if(this.blockStorageFile.length() > filePos) {
            Block block = new VfsBlock(this.blockStorageFile, blockId, filePos);
            this.blocks.put(id, block);
            return block;
        }
        
        /* the requested block does not exist */
        return null;
    }

    @Override
    public Block createNew() {
        long lastBlockId = numberOfBlocks();
        long newBlockId = lastBlockId + 1;
        
        long filePos = getBlockPosition(newBlockId);
        this.blockStorageFile.length(filePos + blockSize());
        
        Block block = new VfsBlock(this.blockStorageFile, newBlockId, filePos);
        this.blocks.put(newBlockId, block);
        
        return block;
    }

    
}
