/*
 * see license.txt 
 */
package lue.db.record;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import lue.db.record.block.BlockRecordStorage;
import lue.db.store.BlockStorage;
import lue.db.store.vfs.VfsBlockStorage;
import lue.db.vfs.File;
import lue.db.vfs.VirtualFileSystem;
import lue.db.vfs.VirtualFileSystem.OpenOperation;
import lue.db.vfs.mem.MemVirtualFileSystem;

/**
 * @author Tony
 *
 */
public class RecordStorageTest {

    private byte[] makeRecord(int size) {
        byte[] record = new byte[size];
        record[0] = 4;
        record[size-1] = 3;
        return record;
    }
    
    @Test
    public void test() {
        VirtualFileSystem vfs = new MemVirtualFileSystem();
        File storageFile = vfs.open(vfs.path("test.lue"), OpenOperation.WRITE);
        
        int headerSize = BlockRecordStorage.RECORD_HEADER_SIZE;
        int contentSize = 1024 - headerSize;
        
        BlockStorage storage = new VfsBlockStorage(storageFile, headerSize, contentSize);
        RecordStorage records = new BlockRecordStorage(storage, contentSize*3);
        
        byte[] record1 = makeRecord(contentSize);        
        long id1 = records.store(record1, 0, record1.length);
        
        byte[] record2 = makeRecord(contentSize+1);
        long id2 = records.store(record2, 0, record2.length);
        
        byte[] copyRecord1 = records.find(id1);        
        assertTrue(Arrays.equals(record1, copyRecord1));
        
        byte[] copyRecord2 = records.find(id2);
        assertTrue(Arrays.equals(record2, copyRecord2));
        
        byte[] updatedRecord1 = makeRecord(contentSize*2);
        records.update(id1, updatedRecord1, 0, updatedRecord1.length);
        
        copyRecord1 = records.find(id1); 
        assertTrue(Arrays.equals(updatedRecord1, copyRecord1));
        
        
        byte[] updatedRecord2 = makeRecord(contentSize);
        records.update(id2, updatedRecord2, 0, updatedRecord2.length);
        
        copyRecord2 = records.find(id2); 
        assertTrue(Arrays.equals(updatedRecord2, copyRecord2));
    }

}
