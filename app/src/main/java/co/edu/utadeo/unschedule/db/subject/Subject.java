package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = AcademicTerm.class,
        parentColumns = "id", childColumns = "academy_term_id"),
        indices = @Index(value = {"academy_term_id", "subject_name"})
)
public class Subject {

    @PrimaryKey(autoGenerate = true)
    private int subjectId;

    @ColumnInfo(name = "academy_term_id")
    private int academyTermId;

    @ColumnInfo(name = "subject_name")
    private String subjectName;

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getAcademyTermId() {
        return academyTermId;
    }

    public void setAcademyTermId(int academyTermId) {
        this.academyTermId = academyTermId;
    }
}
