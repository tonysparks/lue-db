/*
 * see license.txt
 */
package lue.db.vfs;

import java.util.List;

/**
 * Path to a {@link File}
 * 
 * @author Tony
 *
 */
public interface Path {

    String name();
    String fullName();
    
    boolean isDirectory();
    boolean isFile();
    boolean exists();
    
    List<Path> contents();
}
