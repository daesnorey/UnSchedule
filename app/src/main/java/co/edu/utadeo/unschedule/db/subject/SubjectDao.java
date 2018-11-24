package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Subject subject);

    @Update
    void update(Subject... subjects);

    @Delete
    void delete(Subject subject);
}
