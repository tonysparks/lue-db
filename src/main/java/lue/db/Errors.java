/*
 * see license.txt
 */
package lue.db;

/**
 * @author Tony
 *
 */
public class Errors {

    public static LueException invalidRecordSize(int size, int max) {
        return new LueException(String.format("Invalid record size '%d' exceends max '%d'", size, max));
    }
    
    public static LueException invalidBlockId(long blockId) {
        return new LueException(String.format("Invalid block id '%d'", blockId));
    }
}
