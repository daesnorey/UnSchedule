package co.edu.utadeo.unschedule.db;

import android.arch.persistence.room.Room;
import android.content.Context;

public class AppDataBaseAccess {
    private static AppDataBaseAccess ourInstance = null;

    private AppDataBase appDataBase;
    private Context ctx;

    public static AppDataBaseAccess getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new AppDataBaseAccess(ctx);
        } else {
            ourInstance.ctx = ctx;
        }
        return ourInstance;
    }

    private AppDataBaseAccess(Context ctx) {
        this.ctx = ctx;
        this.appDataBase = Room.databaseBuilder(this.ctx, AppDataBase.class, "unschedule-db").build();
    }

    public AppDataBase db() {
        return this.appDataBase;
    }
}
