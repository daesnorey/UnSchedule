package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AcademicTermDao {

    @Query("SELECT * FROM  AcademicTerm")
    List<AcademicTerm> getAll();

    @Query("SELECT * FROM AcademicTerm WHERE start_date <= :today AND end_date >= :today")
    List<AcademicTerm> getCurrent(long today);

    @Query("SELECT MAX(id) + 1 FROM AcademicTerm")
    int next();

    @Insert()
    List<Long> insertAll(AcademicTerm... academicTerms);

    @Insert()
    long insert(AcademicTerm academicTerms);

    @Delete()
    void delete(AcademicTerm academicTerm);

    @Update()
    int update(AcademicTerm academicTerm);
}
