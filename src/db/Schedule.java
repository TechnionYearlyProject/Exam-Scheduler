package db;

import db.exception.DateOutOfSchedule;
import db.exception.InvalidSchedule;
import db.exception.UninitializedSchedule;

import java.time.LocalDate;
import java.util.*;

public class Schedule {
    public LocalDate start;
    public LocalDate end;
    Map<Integer, LocalDate> schedule;

    public Schedule() {
        this.start = null;
        this.end = null;
        this.schedule = new HashMap<>();
    }

    public boolean undefinedStartOrEnd() {
        return start == null || end == null;
    }

    private void updateSchedules() {
        if (start != null) {
            schedule.entrySet().removeIf(entry -> entry.getValue().isBefore(start));
        }
        if (end != null) {
            schedule.entrySet().removeIf(entry -> entry.getValue().isAfter(end));
        }
    }

    public void setStartDate(LocalDate start) throws InvalidSchedule {
        if (end != null && start.isAfter(end)) {
            throw new InvalidSchedule();
        }
        this.start = start;
        updateSchedules();
    }

    public void setEndDate(LocalDate end) throws InvalidSchedule {
        if (start != null && end.isBefore(start)) {
            throw new InvalidSchedule();
        }
        this.end = end;
        updateSchedules();
    }

    public void scheduleCourse(int courseId, LocalDate date) throws DateOutOfSchedule, UninitializedSchedule {
        if (undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (date.isBefore(start) || date.isAfter(end)) {
            throw new DateOutOfSchedule();
        }
        schedule.put(courseId, date);
    }

    public void unscheduleCourse(int id) {
        schedule.remove(id);
    }

    public LocalDate getCourseSchedule(int courseId) {
        if (!schedule.containsKey(courseId)) {
            return null;
        }
        return schedule.get(courseId);
    }
}
