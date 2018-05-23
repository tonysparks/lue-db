/*
 * see license.txt
 */
package lue.db.vfs.mem;

import java.util.Collections;
import java.util.List;

import lue.db.vfs.Path;

/**
 * @author Tony
 *
 */
public class MemPath implements Path {

    private String pathName;
    
    /**
     * 
     */
    public MemPath(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public String name() {
        return this.pathName;
    }

    @Override
    public String fullName() {
        return this.pathName;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public List<Path> contents() {
        return Collections.emptyList();
    }

}
