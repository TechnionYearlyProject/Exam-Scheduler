package db;

import db.exception.InvalidConstraint;
import db.exception.OverlappingConstraints;

import java.time.LocalDate;
import java.util.*;

public class ConstraintList {
    public Map<Integer, List<Constraint>> constraints;

    public ConstraintList() {
        constraints = new HashMap<>();
    }

    public void addConstraint(int courseId, Calendar start, Calendar end, boolean forbidden) throws InvalidConstraint,
            OverlappingConstraints {
        if (end.before(start)) {
            throw new InvalidConstraint();
        }
        if (!constraints.containsKey(courseId)) {
            constraints.put(courseId, new ArrayList<>());
        }
        for (Constraint constraint: constraints.get(courseId)) {
            if (start.after(constraint.end) || end.before(constraint.start)) {
                continue;
            }
            throw new OverlappingConstraints();
        }
        constraints.get(courseId).add(new Constraint(start, end, forbidden));
        Collections.sort(constraints.get(courseId)); // Ordered by start date
    }

    public  void addConstraint(int courseId, Calendar start, Calendar end) throws OverlappingConstraints,
            InvalidConstraint {
        addConstraint(courseId, start, end, false);
    }

    public void removeConstraint(int courseId, Calendar start, Calendar end) {
        Iterator<Constraint> it = constraints.get(courseId).iterator();
        while (it.hasNext()) {
            Constraint constraint = it.next();
            if (constraint.start.equals(start) && constraint.end.equals(end)) {
                it.remove();
                break;
            }
        }
    }

    public List<Constraint> getConstraints(int courseId) {
        if (!constraints.containsKey(courseId)) {
            return null;
        }
        List<Constraint> list = new ArrayList<>();
        for (Constraint constraint: constraints.get(courseId)) {
            list.add(new Constraint(constraint.start, constraint.end));
        }
        return list;
    }

    public void removeDateConstraint(LocalDate date) {
        GregorianCalendar calendar = new GregorianCalendar(date.getYear(),date.getMonthValue(),date.getDayOfMonth());
        for (Integer course_id:constraints.keySet()) {
            ArrayList<Constraint> copied_constraints = new ArrayList<Constraint>(constraints.get(course_id));
            for (Constraint constraint:copied_constraints) {
                if (constraint.getStart().equals(calendar) && constraint.getEnd().equals(calendar))
                    removeConstraint(course_id,calendar,calendar);
            }
        }
    }
}
