package co.edu.utadeo.unschedule.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import co.edu.utadeo.unschedule.db.subject.AcademicTerm;
import co.edu.utadeo.unschedule.db.subject.AcademicTermDao;
import co.edu.utadeo.unschedule.db.subject.Schedule;
import co.edu.utadeo.unschedule.db.subject.ScheduleDao;
import co.edu.utadeo.unschedule.db.subject.Subject;
import co.edu.utadeo.unschedule.db.subject.SubjectDao;

@Database(entities = {Subject.class, Schedule.class, AcademicTerm.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public abstract ScheduleDao scheduleDao();
    public abstract SubjectDao subjectDao();
    public abstract AcademicTermDao academicTermDao();

}
