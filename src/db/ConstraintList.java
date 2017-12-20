package db;

import db.exception.InvalidConstraint;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConstraintList {
    Map<Integer, Constraint> constraints;

    static class Constraint {
        public Calendar start;
        public Calendar end;

        public Constraint(Calendar start, Calendar end) {
            this.start = (Calendar) start.clone();
            this.end = (Calendar) end.clone();
        }
    }

    public ConstraintList() {
        constraints = new HashMap<>();
    }

    public void setConstraint(int courseId, Calendar start, Calendar end) throws InvalidConstraint {
        if (end.before(start)) {
            throw new InvalidConstraint();
        }
        constraints.put(courseId, new Constraint(start, end));
    }

    public void removeConstraint(int courseId) {
        constraints.remove(courseId);
    }

    public Constraint getConstraint(int courseId) {
        Constraint constraint = constraints.get(courseId);
        if (constraint == null) {
            return null;
        }
        return new Constraint(constraint.start, constraint.end);
    }
}
