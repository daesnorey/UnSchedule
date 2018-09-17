package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = Subject.class,
        parentColumns = "subjectId",
        childColumns = "subject_id"), indices = @Index(value = {"subject_id"}))
public class Schedule {

    @PrimaryKey
    private int scheduleId;

    @ColumnInfo(name = "subject_id")
    private int subjectId;

    @ColumnInfo(name = "day_id")
    private int dayId;

    @ColumnInfo(name = "place")
    private String place;

    @ColumnInfo(name = "start_time")
    private int startDate;

    @ColumnInfo(name = "end_time")
    private int endTime;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getSubjectId() { return subjectId; }

    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
