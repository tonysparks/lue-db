/*
 * see license.txt
 */
package lue.db.vfs.disk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lue.db.vfs.Path;

/**
 * @author Tony
 *
 */
public class DiskPath implements Path {

    private File file;
    
    /**
     * @param file
     */
    public DiskPath(File file) {
        this.file = file;
    }

    @Override
    public String name() {
        return this.file.getName();
    }
    
    @Override
    public String fullName() {    
        return this.file.getAbsolutePath();
    }

    @Override
    public boolean isDirectory() {
        return this.file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return this.file.isFile();
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public List<Path> contents() {
        File[] contents = this.file.listFiles();
        List<Path> result = new ArrayList<>();
        for(File f : contents) {
            result.add(new DiskPath(f));
        }
        return result;
    }

}
