/*
 * see license.txt
 */
package lue.db.vfs.mem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lue.db.vfs.File;
import lue.db.vfs.Path;
import lue.db.vfs.VirtualFileSystem;

/**
 * In memory version of the virtual file system
 * 
 * @author Tony
 *
 */
public class MemVirtualFileSystem implements VirtualFileSystem {

    private Map<Path, File> files;
    
    public MemVirtualFileSystem() {
        this.files = new ConcurrentHashMap<>();
    }

    @Override
    public Path path(String pathName) {
        return new MemPath(pathName);
    }

    @Override
    public File open(Path path, OpenOperation op) {
        if(!this.files.containsKey(path)) {
            this.files.put(path, new MemFile(path));
        }
        
        return this.files.get(path);
    }

}
