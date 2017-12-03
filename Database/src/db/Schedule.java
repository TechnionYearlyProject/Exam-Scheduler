package db;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Schedule {
    public Date start;
    public Date end;
    private Map<Integer, Date> schedule;

    // TODO Add ctor for new Schedule without dates
    // TODO Handle missing start/end dates cases

    public Schedule(Date start, Date end) throws InvalidSchedule {
        if (end.before(start)) {
            throw new InvalidSchedule();
        }
        this.start = (Date) start.clone();
        this.end = (Date) start.clone();
        this.schedule = new HashMap<>();
    }

    public void setStartDate(Date start) throws InvalidSchedule {
        if (start.after(end)) {
            throw new InvalidSchedule();
        }
        this.start = (Date) start.clone();
    }

    public void setEndDate(Date end) throws InvalidSchedule {
        if (end.before(start)) {
            throw new InvalidSchedule();
        }
        this.end = (Date) end.clone();
    }

    public void scheduleCourse(int id, Date date) throws DateOutOfSchedule {
         if (date.before(start) || date.after(end)) {
             throw new DateOutOfSchedule();
         }
         schedule.put(id, (Date) date.clone());
    }

    public void unscheduleCourse(int id) {
        schedule.remove(id);
    }
}
