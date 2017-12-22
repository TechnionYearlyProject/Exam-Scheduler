package db;

import db.exception.InvalidConstraint;
import db.exception.OverlappingConstraints;

import java.util.*;

public class ConstraintList {
    Map<Integer, List<Constraint>> constraints;

    public ConstraintList() {
        constraints = new HashMap<>();
    }

    public void addConstraint(int courseId, Calendar start, Calendar end) throws InvalidConstraint, OverlappingConstraints {
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
        constraints.get(courseId).add(new Constraint(start, end));
        Collections.sort(constraints.get(courseId)); // Ordered by start date
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
}
