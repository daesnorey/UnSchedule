package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM Schedule")
    List<Schedule> getAll();

    @Query("SELECT * FROM Schedule WHERE scheduleId IN (:scheduleId)")
    List<Schedule> loadAllByIds(int[] scheduleId);

    @Query("SELECT * FROM Schedule WHERE scheduleId = :scheduleId")
    Schedule getById(int scheduleId);

    @Query("SELECT * FROM Schedule WHERE subject_id = :subjectId")
    List<Schedule> loadAllBySubjectId(int subjectId);

    @Insert
    void insertAll(Schedule... schedule);

    @Delete
    void delete(Schedule schedule);

}


