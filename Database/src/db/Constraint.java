package db;

import java.util.Date;

public class Constraint {
    public int course_id;
    public Date start;
    public Date end;

    public Constraint(int course_id, Date start, Date end) throws InvalidConstraint {
        if (end.before(start)) {
            throw new InvalidConstraint();
        }
        this.course_id = course_id;
        this.start = start;
        this.end = end;
    }
}
