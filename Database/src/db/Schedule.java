package db;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Schedule {
    public Date start;
    public Date end;
    private Map<Integer, Date> schedule;

    public Schedule() {
        this.start = null;
        this.end = null;
        this.schedule = new HashMap<>();
    }

    public Schedule(Date start, Date end) throws InvalidSchedule {
        if (end.before(start)) {
            throw new InvalidSchedule();
        }
        this.start = (Date) start.clone();
        this.end = (Date) start.clone();
        this.schedule = new HashMap<>();
    }

    private boolean undefinedStartOrEnd() {
        return start == null || end == null;
    }

    public void setStartDate(Date start) throws InvalidSchedule {
        if (end != null && start.after(end)) {
            throw new InvalidSchedule();
        }
        this.start = (Date) start.clone();
    }

    public void setEndDate(Date end) throws InvalidSchedule {
        if (start != null && end.before(start)) {
            throw new InvalidSchedule();
        }
        this.end = (Date) end.clone();
    }

    public void scheduleCourse(int id, Date date) throws DateOutOfSchedule, UninitializedSchedule {
        if (undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (date.before(start) || date.after(end)) {
            throw new DateOutOfSchedule();
        }
        schedule.put(id, (Date) date.clone());
    }

    public void unscheduleCourse(int id) {
        schedule.remove(id);
    }
}
