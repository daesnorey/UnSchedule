package co.edu.utadeo.unschedule.db;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import co.edu.utadeo.unschedule.db.subject.AcademicTerm;

/**
 * C:\Users\dnovoare\AppData\Local\Android\Sdk\platform-tools\adb.exe devices
 * C:\Users\dnovoare\AppData\Local\Android\Sdk\platform-tools\adb.exe -s emulator-XXXX shell
 * qlite3 un-schedule-db
 */
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
        this.appDataBase = Room
                .databaseBuilder(this.ctx, AppDataBase.class, "un-schedule-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     *
     * @param event to execute
     * @param callBack to call when the process is over
     * @param <T> Generic type
     */
    public static <T> void execute(Executor<T> event, CallBack<T> callBack) {
        try {
            Callable<T> task = event::methodToCallBack;

            ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Future<T> future = executor.submit(task);
            T value = future.get(1, TimeUnit.SECONDS);
            callBack.callBack(value);
        } catch (Exception e) {
            Log.d("AppDataBaseAccess", e.getMessage(), e);
            callBack.callBack((T) e.getMessage());
        }
    }

    public AppDataBase db() {
        return this.appDataBase;
    }

    public interface Executor<T> {
        T methodToCallBack();
    }

    public interface CallBack<T> {
        void callBack(T value);
    }
}
