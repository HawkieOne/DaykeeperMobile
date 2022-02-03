package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Class which handles the communication between the application and the database
 */

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class MemoryRepository {

    private MemoryDAO memoryDAO;
    private LiveData<List<Memory>> allMemories;
    private LiveData<List<Memory>> allMemoriesDesc;
    private LiveData<List<Memory>> memoriesSearch;
    private List<Memory>allMemoriesList;

    /**
     * Constructor for the MemoryRepository. Connects the database and the dao.
     * @param application for the repo
     */
    MemoryRepository(Application application){
        MemoryRoomDataBase db = MemoryRoomDataBase.getDatabase(application);
        memoryDAO = db.memoryDAO();
        allMemories = memoryDAO.getMemories();
        allMemoriesDesc = memoryDAO.getMemoriesDesc();
    }

    /**
     * @return all memories
     */
    LiveData<List<Memory>> getAllMemories(){
        return allMemories;
    }

    /**
     * @return all memories in descending order
     */
    LiveData<List<Memory>> getAllMemoriesDesc(){
        return allMemoriesDesc;
    }

    /**
     * @param searchWord to search for
     * @return all found memories
     */
    LiveData<List<Memory>> getMemoriesSearch(String searchWord){
        memoriesSearch = memoryDAO.getMemoriesSearch(searchWord);
        return memoriesSearch;
    }

    /**
     * @param searchWord to search for
     * @return all found memories
     */
    LiveData<List<Memory>> getMemoriesSearchDesc(String searchWord){
        memoriesSearch = memoryDAO.getMemoriesSearchDesc(searchWord);
        return memoriesSearch;
    }

    /**
     * Inserts a memory into the database
     * @param memory to insert
     */
    void insert(Memory memory){
        MemoryRoomDataBase.databaseWriteExecutor.execute(() -> {
            memoryDAO.insert(memory);
        });
    }

    /**
     * Updates a memory in the database
     * @param memory to update
     */
    void updateMemory(Memory memory){
        MemoryRoomDataBase.databaseWriteExecutor.execute(() -> {
            memoryDAO.updateMemory(memory);
        });
    }

    /**
     * Deletes a memory into the database
     * @param memory to delete
     */
    void delete(Memory memory){
        MemoryRoomDataBase.databaseWriteExecutor.execute(() -> {
            memoryDAO.delete(memory);
        });
    }
}
