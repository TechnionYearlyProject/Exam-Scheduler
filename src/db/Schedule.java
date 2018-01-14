package db;

import db.exception.DateOutOfSchedule;
import db.exception.InvalidSchedule;
import db.exception.UninitializedSchedule;

import java.util.*;

public class Schedule {
    public Calendar start;
    public Calendar end;
    Map<Integer, Calendar> schedule;

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
            schedule.entrySet().removeIf(entry -> entry.getValue().before(start));
        }
        if (end != null) {
            schedule.entrySet().removeIf(entry -> entry.getValue().after(end));
        }
    }

    public void setStartDate(Calendar start) throws InvalidSchedule {
        if (end != null && start.after(end)) {
            throw new InvalidSchedule();
        }
        this.start = (Calendar) start.clone();
        updateSchedules();
    }

    public void setEndDate(Calendar end) throws InvalidSchedule {
        if (start != null && end.before(start)) {
            throw new InvalidSchedule();
        }
        this.end = (Calendar) end.clone();
        updateSchedules();
    }

    public void scheduleCourse(int courseId, Calendar date) throws DateOutOfSchedule, UninitializedSchedule {
        if (undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (date.before(start) || date.after(end)) {
            throw new DateOutOfSchedule();
        }
        schedule.put(courseId, (Calendar) date.clone());
    }

    public void unscheduleCourse(int id) {
        schedule.remove(id);
    }

    public Calendar getCourseSchedule(int courseId) {
        if (!schedule.containsKey(courseId)) {
            return null;
        }
        return (Calendar) schedule.get(courseId).clone();
    }
}
