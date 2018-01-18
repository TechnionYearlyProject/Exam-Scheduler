package db;

import db.exception.DuplicateConstraints;

import java.time.LocalDate;
import java.util.*;

public class ConstraintList {
    public Map<Integer, List<Constraint>> constraints;

    public ConstraintList() {
        constraints = new HashMap<>();
    }

    public void addConstraint(int courseId, LocalDate date, boolean forbidden) throws DuplicateConstraints {
        if (!constraints.containsKey(courseId)) {
            constraints.put(courseId, new ArrayList<>());
        }
        for (Constraint constraint: constraints.get(courseId)) {
            if (date.isEqual(constraint.date)) {
                throw new DuplicateConstraints();
            }
        }
        constraints.get(courseId).add(new Constraint(date, forbidden));
        Collections.sort(constraints.get(courseId)); // Ordered by start date
    }

    public  void addConstraint(int courseId, LocalDate date) throws DuplicateConstraints {
        addConstraint(courseId, date, false);
    }

    public void removeConstraint(int courseId, LocalDate date) {
        Iterator<Constraint> it = constraints.get(courseId).iterator();
        while (it.hasNext()) {
            Constraint constraint = it.next();
            if (constraint.date.isEqual(date)) {
                it.remove();
                break;
            }
        }
    }

    public void removeConstraint(LocalDate date) {
        for (Integer courseId: constraints.keySet()) {
            removeConstraint(courseId, date);
        }
    }

    public void removeConstraint(Integer courseID) {
        constraints.remove(courseID);
    }

    public List<Constraint> getConstraints(int courseId) {
        if (!constraints.containsKey(courseId)) {
            return new ArrayList<Constraint>();
        }
        List<Constraint> list = new ArrayList<>();
        for (Constraint constraint: constraints.get(courseId)) {
            list.add(new Constraint(constraint));
        }
        return list;
    }
}
