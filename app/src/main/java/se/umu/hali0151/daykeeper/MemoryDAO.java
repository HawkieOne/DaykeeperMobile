package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Data Access Object for the database
 */

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MemoryDAO {

    /**
     * Inserts a memory in the database
     * @param memory to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Memory memory);

    /**
     * Updates a memory in the database
     * @param memory to update
     */
    @Update
    void updateMemory(Memory memory);

    /**
     * Deletes all memories in the database
     */
    @Query("DELETE FROM memory_table")
    void deleteAll();

    /**
     * Deletes a memory from the database
     * @param memory to delete
     */
    @Delete
    void delete(Memory memory);

    /**
     * Gets all memories in ascending dates
     * @return memories
     */
    @Query("SELECT * from memory_table order by DATE ASC")
    LiveData<List<Memory>> getMemories();

    /**
     * Gets all memories in descending dates
     * @return memories
     */
    @Query("SELECT * from memory_table order by DATE DESC")
    LiveData<List<Memory>> getMemoriesDesc();

    /**
     * Gets the memories that match the searched word
     * @param searchWord
     * @return memories that matches the searchWord
     */
    @Query("SELECT * from memory_table WHERE dateDay LIKE :searchWord OR dateMonth " +
            "LIKE :searchWord OR dateYear LIKE :searchWord")
    LiveData<List<Memory>> getMemoriesSearch(String searchWord);

    /**
     * Gets the memories that match the searched word
     * @param searchWord
     * @return memories that matches the searchWord
     */
    @Query("SELECT * from memory_table WHERE dateDay LIKE :searchWord OR dateMonth " +
            "LIKE :searchWord OR dateYear LIKE :searchWord order by DATE DESC")
    LiveData<List<Memory>> getMemoriesSearchDesc(String searchWord);

    /**
     * Get all memories
     * @return memories
     */
    @Query("SELECT * FROM memory_table")
    List<Memory> getMemoriesList();
}
