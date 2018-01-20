package db;

import db.exception.DuplicateConstraints;

import java.time.LocalDate;
import java.util.*;

/**
 * This class keep all the Constraint objects of a semester, and ensure no course has
 * duplicate Constraint (same dates), and that each Constraint is linked to a course.
 * @author Rephael Azoulay
 * @date 19/01/2018
 */
public class ConstraintList {
    public Map<Integer, List<Constraint>> constraints;

    public ConstraintList() {
        constraints = new HashMap<>();
    }

    /**
     * Add a constraint to a specific course
     * @param courseId The ID of the course
     * @param date The date at which the course must/musnt be scheduled
     * @param forbidden Whether the course has to be scheduled this day or musnt be scheduled.
     * @throws DuplicateConstraints If the course contains already a constraint to this date.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
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

    /**
     * Overloaded method to give false default value to the parameter "forbidden".
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public  void addConstraint(int courseId, LocalDate date) throws DuplicateConstraints {
        addConstraint(courseId, date, false);
    }

    /**
     * Remove the constraint of a course corresponding to a specific date. Has no effect if
     * there is no constraint at this date.
     * @param courseId The ID of the course
     * @param date The date to remove from the list
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void removeConstraint(int courseId, LocalDate date) {
        if (constraints.get(courseId) == null)
            return;
        Iterator<Constraint> it = constraints.get(courseId).iterator();
        while (it.hasNext()) {
            Constraint constraint = it.next();
            if (constraint.date.isEqual(date)) {
                it.remove();
                break;
            }
        }
    }

    /**
     * Remove all the constraints corresponding to the date.
     * @param date The date at which constraints will be removed.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void removeConstraint(LocalDate date) {
        for (Integer courseId: constraints.keySet()) {
            removeConstraint(courseId, date);
        }
    }

    /**
     * Remove all the constraints of a specific course.
     * @param courseID The course which all the constraints will be removed.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void removeConstraint(Integer courseID) {
        constraints.remove(courseID);
    }

    /**
     * Return a list of all the constraints of a specific course.
     * @param courseId The ID of the course.
     * @return a list of Constraint objects containing a date and the forbidden flag.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
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
