/*
* see license.txt
 */
package lue.db.record.block;

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
    
    private static final long FreeListBlockId     = 1;
    
    private final int maxRecordLength;
    private BlockStorage storage;
    private List<Block> freeList;
    private Block freeListTail;
    /**
     * 
     */
    public BlockRecordStorage(BlockStorage storage, int maxRecordLength) {
        this.storage = storage;
        this.maxRecordLength = maxRecordLength;
        
        this.freeList = new ArrayList<Block>();
        
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
        
        return null;
    }
    
    private void freeBlock(Block block) {
        block.header(IsDeleted, 1);
        block.header(BlockContentLength, 0);
        block.header(RecordLength, 0);
        block.header(NextBlockId, 0);
        
        long len = this.freeListTail.header(BlockContentLength);
        if(len >= this.storage.blockContentSize()) {
            
        }
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
                int bytesRead = block.read(record, 0, this.storage.blockHeaderSize(), Math.min(recordSize, this.storage.blockContentSize()));
                
                while(bytesRead > recordSize) {
                    long nextBlockId = block.header(NextBlockId);
                    if(nextBlockId < 1) {
                        throw Errors.invalidBlockId(nextBlockId);
                    }
                    
                    Block nextBlock = this.storage.find(nextBlockId);
                    
                    if(nextBlock == null || nextBlock.header(IsDeleted) > 0) {
                        throw Errors.invalidBlockId(nextBlockId);
                    }
                    
                    int bytesRemaining = recordSize - bytesRead;
                    bytesRead += block.read(record, bytesRead, this.storage.blockHeaderSize(), Math.min(bytesRemaining, this.storage.blockContentSize()));
                }
                
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
            bytesWritten += block.write(record, bytesWritten, this.storage.blockHeaderSize(), Math.min(this.storage.blockContentSize(), bytesWritten));
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
        int bytesWritten = 0;
        
        do {
            bytesWritten += block.write(record, bytesWritten, this.storage.blockHeaderSize(), Math.min(this.storage.blockContentSize(), bytesWritten));
            
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
                block.header(IsDeleted, 0);                
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
