/*
 * see license.txt
 */
package lue.db.vfs.disk;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import lue.db.vfs.File;
import lue.db.vfs.Path;
import lue.db.vfs.VirtualFileSystem;

/**
 * Ability to read/write files from disk
 * 
 * @author Tony
 *
 */
public class DiskVirtualFileSystem implements VirtualFileSystem {

    @Override
    public Path path(String pathName) {
        return new DiskPath(new java.io.File(pathName));
    }
    
    @Override
    public File open(Path path, OpenOperation op) {
        String mode = "r";
        switch(op) {
            case READ:  mode = "r"; break;
            case WRITE: mode = "w"; break;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(new java.io.File(path.name()), mode);
            return new DiskFile(path, raf);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
