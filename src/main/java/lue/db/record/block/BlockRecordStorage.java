/*
* see license.txt
 */
package lue.db.record.block;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lue.db.Errors;
import lue.db.record.RecordStorage;
import lue.db.store.Block;
import lue.db.store.BlockStorage;

/**
 * @author Tony
 *
 */
public class BlockRecordStorage implements RecordStorage {

    private static final int  NextBlockId         = 0,
                              RecordLength        = 1,
                              BlockContentLength  = 2,
                              PreviousBlockId     = 3,
                              IsDeleted           = 4;
    
    /**
     * The size of the record header size
     */
    public static final int   RECORD_HEADER_SIZE  = 5 * Long.BYTES; 
    
    private static final long FreeListBlockId     = 1;
    
    private final int maxRecordLength;
    private BlockStorage storage;
    private List<Block> freeList;
    private Block freeListTail;
    
    private ByteBuffer allocIdBuf;
    private ByteBuffer freeIdBuf;
    /**
     * 
     */
    public BlockRecordStorage(BlockStorage storage, int maxRecordLength) {
        this.storage = storage;
        this.maxRecordLength = maxRecordLength;
        
        this.freeList = new ArrayList<Block>();
        
        this.allocIdBuf = ByteBuffer.allocate(Long.BYTES);
        this.freeIdBuf = ByteBuffer.allocate(Long.BYTES);
        
        Block freeListHead = storage.find(FreeListBlockId);
        if(freeListHead == null) {
            freeListHead = storage.createNew();
            this.freeList.add(freeListHead);
        }
        else {            
            forEachBlock(freeListHead, nextBlock -> this.freeList.add(nextBlock));
        }
        
        this.freeListTail = this.freeList.get(this.freeList.size() - 1);
        
    }

    private void forEachBlock(Block head, Consumer<Block> c) {
        long nextBlockId = head.header(NextBlockId);
        while(nextBlockId > 0) {
            Block nextBlock = this.storage.find(nextBlockId);
            if(nextBlock == null) {
                break;
            }
            
            nextBlockId = nextBlock.header(NextBlockId);                                   
            c.accept(nextBlock);
        }
    }
    
    private Block allocBlock() {
        Block freeBlock = null;
        
        int len = (int)this.freeListTail.header(BlockContentLength);
        if(len > 0) {
            this.allocIdBuf.clear();
            this.freeListTail.read(this.allocIdBuf.array(), 0, this.storage.blockHeaderSize() + (len * Long.BYTES), this.allocIdBuf.capacity());
            
            long freeBlockId = this.allocIdBuf.getLong();
            freeBlock = this.storage.find(freeBlockId);
            if(freeBlock == null || freeBlock.header(IsDeleted) < 1) {
                throw Errors.invalidBlockId(freeBlockId);
            }
            
            int newLen = len - (1 * Long.BYTES);
            this.freeListTail.header(BlockContentLength, Math.max(0, newLen));
        }
        else {
            /* the current tail of the free list is empty, and therefore we can
             * assign it to be a free block
             */
            if(this.freeListTail.id() > 1) {
                long prevBlockId = this.freeListTail.header(PreviousBlockId);
                Block newTail = this.storage.find(prevBlockId);
                if(newTail == null || newTail.header(IsDeleted) < 1) {
                    throw Errors.invalidBlockId(prevBlockId);
                }
                
                freeBlock = this.freeListTail;
                this.freeListTail = newTail;
            }
            else {
                /* no free blocks are available, so we must alloc a brand new one */
                freeBlock = this.storage.createNew();
            }
        }
        
        if(freeBlock == null) {
            throw Errors.blockAllocFailed();
        }
        
        freeBlock.header(IsDeleted, 0);
        freeBlock.header(BlockContentLength, 0);
        freeBlock.header(RecordLength, 0);
        freeBlock.header(NextBlockId, 0);
        freeBlock.header(PreviousBlockId, 0);
        
        return freeBlock;
    }
    
    private void freeBlock(Block block) {
        block.header(IsDeleted, 1);
        block.header(BlockContentLength, 0);
        block.header(RecordLength, 0);
        block.header(NextBlockId, 0);
        block.header(PreviousBlockId, 0);
                
        int len = (int)this.freeListTail.header(BlockContentLength);
        if((len + Long.BYTES) > this.storage.blockContentSize()) {
            Block newBlock = allocBlock();
            this.freeListTail.header(NextBlockId, newBlock.id());
            newBlock.header(PreviousBlockId, this.freeListTail.id());
            
            this.freeList.add(newBlock);
            this.freeListTail = newBlock;
            
            len = 0;
        }
        
        this.freeIdBuf.clear();
        this.freeIdBuf.putLong(block.id());
        this.freeListTail.write(this.freeIdBuf.array(), 0, len, this.freeIdBuf.capacity());
        this.freeListTail.header(BlockContentLength, len + 1 * Long.BYTES);
    }
    
    @Override
    public byte[] find(long recordId) {
        Block block = this.storage.find(recordId);
        if(block != null) {
            if(block.header(IsDeleted) == 0) {
                long recordLen = block.header(RecordLength);
                if(recordLen > this.maxRecordLength) {
                    throw Errors.invalidRecordSize((int)recordLen, this.maxRecordLength);
                }
                
                int recordSize = (int) recordLen;
                
                byte[] record = new byte[recordSize];
                int bytesRead = 0;
                
                do {                    
                    int bytesRemaining = recordSize - bytesRead;
                    bytesRead += block.read(record, bytesRead, this.storage.blockHeaderSize(), Math.min(bytesRemaining, this.storage.blockContentSize()));
                    
                    if(bytesRead < recordSize) {
                        long nextBlockId = block.header(NextBlockId);
                        if(nextBlockId < 1) {
                            throw Errors.invalidBlockId(nextBlockId);
                        }
                        
                        Block nextBlock = this.storage.find(nextBlockId);
                        
                        if(nextBlock == null || nextBlock.header(IsDeleted) > 0) {
                            throw Errors.invalidBlockId(nextBlockId);
                        }
                        
                        block = nextBlock;
                    }                                        
                }
                while(bytesRead < recordSize);
                
                return record;
            }
        }
        
        return null;
    }

    @Override
    public long store(byte[] record, int offset, int length) {
        Block block = allocBlock();
        long recordId = block.id();
        
        int recordLen = length;
        block.header(RecordLength, recordLen);
        int bytesWritten = 0;
        
        do {
            bytesWritten += block.write(record, bytesWritten, this.storage.blockHeaderSize(), Math.min(this.storage.blockContentSize(), recordLen - bytesWritten));
            if(bytesWritten < recordLen) {
                Block nextBlock = allocBlock();
                block.header(NextBlockId, nextBlock.id());
                block = nextBlock;
            }
        }
        while(bytesWritten < recordLen);
        
        return recordId;
    }

    @Override
    public boolean update(long recordId, byte[] record, int offset, int length) {
        Block block = this.storage.find(recordId);
        if(block == null || block.header(IsDeleted) > 0) {
            throw Errors.invalidBlockId(recordId);
        }
                
        int recordLen = length;
        block.header(RecordLength, recordLen);
        int bytesWritten = 0;
        
        do {
            bytesWritten += block.write(record, bytesWritten, this.storage.blockHeaderSize(), Math.min(this.storage.blockContentSize(), recordLen - bytesWritten));
            
            /* determine if we need another block, if so check and 
             * see if there is already one allocated to this record (if so, use it; 
             * otherwise allocate a new one)
             */
            if(bytesWritten < recordLen) {
                
                Block nextBlock = null;
                long nextBlockId = block.header(NextBlockId);
                
                if(nextBlockId > 0) {
                    nextBlock = this.storage.find(nextBlockId);
                }
                
                if(nextBlock == null) {
                    nextBlock = allocBlock();    
                }
                
                block.header(NextBlockId, nextBlock.id());
                block = nextBlock;
            }            
            else {
                /* Now delete any remaining blocks allocated to the previous record
                 * that are no longer needed
                 */                                
                forEachBlock(block, this::freeBlock);
            }
        }
        while(bytesWritten < recordLen);
        
        return bytesWritten > 0;
    }

    @Override
    public boolean delete(long recordId) {
        Block block = this.storage.find(recordId);
        if(block == null || block.header(IsDeleted) > 0) {
            return false;
        }
        
        freeBlock(block);                
        forEachBlock(block, this::freeBlock);
        
        return true;
    }

}

