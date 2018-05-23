/*
 * see license.txt
 */
package lue.db.store;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import lue.db.store.vfs.VfsBlockStorage;
import lue.db.vfs.File;
import lue.db.vfs.VirtualFileSystem;
import lue.db.vfs.VirtualFileSystem.OpenOperation;
import lue.db.vfs.mem.MemVirtualFileSystem;

/**
 * @author Tony
 *
 */
public class BlockStorageTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        VirtualFileSystem vfs = new MemVirtualFileSystem();
        File storageFile = vfs.open(vfs.path("test.lue"), OpenOperation.WRITE);
        
        int headerSize = 5;
        int contentSize = 1024 - headerSize;
        
        BlockStorage storage = new VfsBlockStorage(storageFile, headerSize, contentSize);
        assertEquals(headerSize + contentSize, storage.blockSize());
        
        Block block = storage.createNew();
        assertNotNull(block);
        assertEquals(1, block.id());
        
        for(int i = 0; i < headerSize; i++) {
            block.header(i, i+1);
            assertEquals(i+1, block.header(i));
        }
        
        ByteBuffer wBuf = ByteBuffer.allocate(contentSize);
        wBuf.putInt(4);
        wBuf.putFloat(4.4f);
        wBuf.putLong(444L);
        
        block.write(wBuf.array(), 0, storage.blockHeaderSize(), wBuf.position());
        
        ByteBuffer rBuf = ByteBuffer.allocate(contentSize);
        block.read(rBuf.array(), 0, storage.blockHeaderSize(), rBuf.capacity());
        
        wBuf.position(0);
        assertEquals(wBuf.getInt(), rBuf.getInt());
        assertEquals(wBuf.getFloat(), rBuf.getFloat(), 0.01);
        assertEquals(wBuf.getLong(), rBuf.getLong());
    }

}
