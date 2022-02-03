package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Class which handles the database
 */

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Memory.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MemoryRoomDataBase extends RoomDatabase {

    public abstract MemoryDAO memoryDAO();

    private static volatile MemoryRoomDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Creates or returns the database
     * @param context
     * @return database
     */
    static MemoryRoomDataBase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized ((MemoryRoomDataBase.class)){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MemoryRoomDataBase.class, "memory_db").
                                addCallback(roomDataBaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Called when the database is created
     */
    public static Callback roomDataBaseCallback = new Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                MemoryDAO dao = INSTANCE.memoryDAO();
//                dao.deleteAll();

//                Memory memory = new Memory();
//                memory.setTextDay("Hello");
//                dao.insert(memory);
            });
        }
    };

}
