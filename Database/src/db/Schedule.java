package db;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Schedule {
    public Calendar start;
    public Calendar end;
    private Map<Integer, Calendar> schedule;

    public Schedule() {
        this.start = null;
        this.end = null;
        this.schedule = new HashMap<>();
    }

    public Schedule(Calendar start, Calendar end) throws InvalidSchedule {
        if (end.before(start)) {
            throw new InvalidSchedule();
        }
        this.start = (Calendar) start.clone();
        this.end = (Calendar) end.clone();
        this.schedule = new HashMap<>();
    }

    private boolean undefinedStartOrEnd() {
        return start == null || end == null;
    }

    public void setStartDate(Calendar start) throws InvalidSchedule {
        if (end != null && start.after(end)) {
            throw new InvalidSchedule();
        }
        this.start = (Calendar) start.clone();
    }

    public void setEndDate(Calendar end) throws InvalidSchedule {
        if (start != null && end.before(start)) {
            throw new InvalidSchedule();
        }
        this.end = (Calendar) end.clone();
    }

    public void scheduleCourse(int id, Calendar calendar) throws DateOutOfSchedule, UninitializedSchedule,
            ScheduleDateAlreadyTaken {
        if (undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (calendar.before(start) || calendar.after(end)) {
            throw new DateOutOfSchedule();
        }
        for (Calendar cal: schedule.values()) {
            if (calendar.equals(cal)) {
                throw new ScheduleDateAlreadyTaken();
            }
        }
        schedule.put(id, (Calendar) calendar.clone());
    }

    public void unscheduleCourse(int id) {
        schedule.remove(id);
    }
}
