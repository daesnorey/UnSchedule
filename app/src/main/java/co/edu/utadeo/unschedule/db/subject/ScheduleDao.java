package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM Schedule ORDER BY day_id, start_hour, start_minute, end_hour, end_minute")
    List<Schedule> getAll();

    @Query("SELECT * FROM Schedule WHERE scheduleId IN (:scheduleId) ORDER BY day_id, start_hour, start_minute, end_hour, end_minute")
    List<Schedule> loadAllByIds(int[] scheduleId);

    @Query("SELECT * FROM Schedule WHERE scheduleId = :scheduleId")
    Schedule getById(int scheduleId);

    @Query("SELECT * FROM Schedule WHERE subject_id = :subjectId")
    List<Schedule> loadAllBySubjectId(int subjectId);

    @Insert
    void insertAll(Schedule... schedule);

    @Insert
    long insert(Schedule schedule);

    @Update
    void update(Schedule schedule);

    @Delete
    void delete(Schedule schedule);

}


