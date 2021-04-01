package cz.vancura.retrofitstackexchangefragment.model.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
Room DB - get dB instance
 */

@Database(entities = {RoomUserPOJO.class}, version = 1)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    public abstract RoomUserDao userDao();
    private static RoomDatabase dbInstance;

    // Executor Service for Async threating
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // create dB
    public static RoomDatabase getDatabase(final Context context) {

        if (dbInstance == null) {
            synchronized (RoomDatabase.class) {
                if (dbInstance == null) {
                    // Create database here
                    dbInstance = Room.databaseBuilder(context.getApplicationContext(), RoomDatabase.class, "user_database")
                            .fallbackToDestructiveMigration()
                            //.addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return dbInstance;
    }

}
