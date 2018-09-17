package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SubjectDao {
    @Query("SELECT * FROM Subject")
    List<Subject> getAll();

    @Query("SELECT * FROM Subject WHERE subjectId IN (:subjectId)")
    List<Subject> loadAllByIds(int[] subjectId);

    @Query("SELECT * FROM Subject WHERE subjectId = :subjectId")
    Subject getById(int subjectId);

    @Insert
    void insertAll(Subject... subjects);

    @Delete
    void delete(Subject subject);
}
