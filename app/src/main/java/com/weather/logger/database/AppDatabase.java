package com.weather.logger.database;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.weather.logger.database.converter.DateConverter;
import com.weather.logger.database.dao.WeatherDataDao;
import com.weather.logger.database.entity.WeatherInfoEntity;


/**
 * @author Enlistech.
 */
@Database(entities = {WeatherInfoEntity.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDatabase;

    @VisibleForTesting
    private static final String DATABASE_NAME = "YouSoGetMe";

    public abstract WeatherDataDao weatherDataDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context) {
        if (appDatabase == null) {
            synchronized (AppDatabase.class) {
                if (appDatabase == null) {
                    appDatabase = buildDatabase(context.getApplicationContext());
                    appDatabase.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return appDatabase;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().fallbackToDestructiveMigration()
//                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    private LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}

