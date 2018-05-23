/*
 * see license.txt
 */
package lue.db.vfs;

/**
 * @author Tony
 *
 */
public interface VirtualFileSystem {

    enum OpenOperation {
        READ,
        WRITE,
    }
    
    /**
     * Resolves the {@link Path} from the supplied path name
     * 
     * @param pathName
     * @return the {@link Path} representing the supplied pathName
     */
    Path path(String pathName);
    
    /**
     * Open a File
     * 
     * @param path
     * @param op
     * @return the {@link File}
     */
    File open(Path path, OpenOperation op);
}
