package fpt.edu.vn.vinho_app.data.local;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient mInstance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ViNhoDB")
                .fallbackToDestructiveMigration() // Optional: Handles migrations by destroying and rebuilding the database
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
