package co.edu.utadeo.unschedule.services;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import co.edu.utadeo.unschedule.db.AppDataBase;
import co.edu.utadeo.unschedule.db.AppDataBaseAccess;
import co.edu.utadeo.unschedule.db.subject.AcademicTerm;

public class ScheduleService {
    private static final ScheduleService ourInstance = new ScheduleService();

    public static AcademicTerm currentAcademicTerm;

    public static ScheduleService getInstance() {
        return ourInstance;
    }

    private ScheduleService() {

    }

    /**
     *
     * @param ctx Application context
     * @return Boolean indicating if there is a current academic term configured
     */
    public boolean hasValidClassConfiguration(Context ctx) {
        AppDataBase db = AppDataBaseAccess.getInstance(ctx).db();

        AppDataBaseAccess.Executor<List<AcademicTerm>> executor = (() ->
                db.academicTermDao().getCurrent(new Date().getTime())
        );

        AppDataBaseAccess.CallBack<List<AcademicTerm>> callBack = terms -> {
            if (terms != null && !terms.isEmpty()) {
                currentAcademicTerm = terms.get(0);
            }
        };

        AppDataBaseAccess.execute(executor, callBack);

        return currentAcademicTerm != null;
    }
}
