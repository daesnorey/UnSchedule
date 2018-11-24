package co.edu.utadeo.unschedule.db.subject;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Subject.class,
        parentColumns = "subjectId",
        childColumns = "subject_id"), indices = @Index(value = {"subject_id"}))
public class Schedule {

    public Schedule(int subjectId) {
        this.subjectId = subjectId;
    }

    public Schedule() {
    }

    @PrimaryKey(autoGenerate = true)
    private int scheduleId;

    @ColumnInfo(name = "subject_id")
    private int subjectId;

    @ColumnInfo(name = "day_id")
    private int dayId;

    @ColumnInfo(name = "place")
    private String place;

    @ColumnInfo(name = "start_hour")
    private int startHour;

    @ColumnInfo(name = "start_minute")
    private int startMinute;

    @ColumnInfo(name = "end_hour")
    private int endHour;

    @ColumnInfo(name = "end_minute")
    private int endMinute;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

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

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
}
