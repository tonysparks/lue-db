/*
 * see license.txt
 */
package lue.db.record;

/**
 * Stores database row records
 * 
 * @author Tony
 *
 */
public interface RecordStorage {

    /**
     * Find the record
     * 
     * @param recordId
     * @return the bytes representing the record, or null if not found
     */
    byte[] find(long recordId);
    
    /**
     * Store the record
     * 
     * @param record
     * @return the record id
     */
    long store(byte[] record, int offset, int length);
    
    /**
     * Updates an existing record
     * 
     * @param recordId
     * @param record
     * @return true if the record was updated
     */
    boolean update(long recordId, byte[] record, int offset, int length);
    
    /**
     * Deletes an existing record
     * 
     * @param recordId
     * @return true if the record was deleted
     */
    boolean delete(long recordId);
}
