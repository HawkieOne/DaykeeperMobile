package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * ViewModel class for data
 */

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MemoryViewModel extends AndroidViewModel {

    private MemoryRepository repository;

    private LiveData<List<Memory>> allMemories;
    private LiveData<List<Memory>> allMemoriesDesc;
    private LiveData<List<Memory>> memoriesSearch;
    private List<Memory> allMemoriesList;
    private Memory savedMemory;

    /**
     * Constructor for the ViewModel
     * @param application
     */
    public MemoryViewModel(Application application){
        super(application);
        repository = new MemoryRepository(application);
        allMemories = repository.getAllMemories();
        allMemoriesDesc = repository.getAllMemoriesDesc();
    }

    /**
     * @return all memories
     */
    LiveData<List<Memory>> getAllMemories() {
        return allMemories;
    }
    /**
     * @return all memories in descending order
     */
    LiveData<List<Memory>> getAllMemoriesDesc() {
        return allMemoriesDesc;
    }

    /**
     * @param searchWord to search for
     * @return the found memories
     */
    LiveData<List<Memory>> getMemoriesSearch(String searchWord) {
        memoriesSearch = repository.getMemoriesSearch(searchWord);
        return memoriesSearch;
    }

    /**
     * @param searchWord to search for
     * @return the found memories
     */
    LiveData<List<Memory>> getMemoriesSearchDesc(String searchWord) {
        memoriesSearch = repository.getMemoriesSearchDesc(searchWord);
        return memoriesSearch;
    }

    /**
     * Inserts a memory in the database
     * @param memory to insert
     */
    public void insert(Memory memory){
        repository.insert(memory);
    }

    /**
     * Updates a memory in the database
     * @param memory to update
     */
    public void update(Memory memory){
        repository.updateMemory(memory);
    }

    /**
     * Deletes a memory in the database
     * @param memory to delete
     */
    public void delete(Memory memory){
        repository.delete(memory);
    }

    public Memory restoreSavedMemory(){
        if(savedMemory != null){
            return savedMemory;
        }
        else{
            return null;
        }
    }

    public void setSavedMemory(Memory memory){
        savedMemory = memory;
    }
}
