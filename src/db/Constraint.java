package db;

import java.util.Calendar;

public class Constraint implements Comparable<Constraint>{
    public Calendar start;
    public Calendar end;
    public boolean forbidden;

    public Constraint(Calendar start, Calendar end, boolean forbidden) {
        this.start = (Calendar) start.clone();
        this.end = (Calendar) end.clone();
        this.forbidden = forbidden;
    }

    public Constraint(Calendar start, Calendar end) {
        this.start = (Calendar) start.clone();
        this.end = (Calendar) end.clone();
        this.forbidden = false;
    }

    @Override
    public int compareTo(Constraint other) {
        if (start.before(other.start)) {
            return -1;
        }
        if (start.after(other.start)) {
            return 1;
        }
        return 0;
    }

    public Calendar getStart(){
        return (Calendar)start.clone();
    }

    public Calendar getEnd(){
        return (Calendar)end.clone();
    }
}
