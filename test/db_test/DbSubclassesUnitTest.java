package db_test;

import db.Constraint;
import db.ConstraintList;
import db.Course;
import Logic.Exceptions.IllegalDaysBefore;
import db.exception.DuplicateConstraints;
import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class DbSubclassesUnitTest {

    @Test
    public void CourseUnitTest() throws IllegalDaysBefore {
        Course course = new Course(101, "Edmond Dantes", 5.0, 4,
                false, true, true, true);
        Course copy = new Course(course);
        assertEquals(101, copy.getCourseID());
        assertEquals("Edmond Dantes", copy.getCourseName());
        assertEquals(5.0, copy.creditPoints);
        assertEquals(4, copy.getDaysBefore());
        assertFalse(copy.isFirst());
        assertTrue(copy.isLast());
        assertTrue(copy.isRequired);
        assertTrue(copy.hasExam);

        copy.setDaysBefore(10);
        assertEquals(10, copy.getDaysBefore());

        copy.setAsFirst(true);
        assertTrue(copy.isFirst());
        assertFalse(copy.isLast());
        copy.setAsLast(true);
        assertFalse(copy.isFirst());
        assertTrue(copy.isLast());
    }

    @Test(expected = IllegalDaysBefore.class)
    public void CourseBadDaysBefore() throws IllegalDaysBefore {
        Course course = new Course(101, "Edmond Dantes", 5.0, 4,
                false, false, true, true);
        course.setDaysBefore(-1);
    }

    @Test
    public void ConstraintUnitTest() {
        Constraint c1 = new Constraint(LocalDate.parse("2017-01-01"), false);
        Constraint c2 = new Constraint(LocalDate.parse("2017-01-07"));
        Constraint c3 = new Constraint(c1);
        Constraint c4 = new Constraint(LocalDate.parse("2018-01-01"));
        assertTrue(c1.equals(c3));
        assertEquals(-1, c1.compareTo(c2));
        assertEquals(1, c4.compareTo(c2));
        assertEquals(0, c1.compareTo(c3));
    }

    @Test
    public void ConstraintListUnitTest() throws DuplicateConstraints {
        ConstraintList list = new ConstraintList();
        list.addConstraint(1, LocalDate.parse("2014-01-01"));
        list.addConstraint(1, LocalDate.parse("2014-01-02"), true);
        list.addConstraint(2, LocalDate.parse("2014-01-02"));
        list.addConstraint(3, LocalDate.parse("2014-01-03"));

        assertEquals(2, list.getConstraints(1).size());
        assertEquals(1, list.getConstraints(2).size());
        assertEquals(1, list.getConstraints(3).size());

        list.removeConstraint(3, LocalDate.parse("2014-01-03"));
        list.removeConstraint(1);
        list.removeConstraint(LocalDate.parse("2014-01-02"));

        assertEquals(0, list.getConstraints(1).size());
        assertEquals(0, list.getConstraints(2).size());
        assertEquals(0, list.getConstraints(3).size());
    }

    @Test(expected = DuplicateConstraints.class)
    public void addDuplicateConstraint() throws DuplicateConstraints {
        ConstraintList list = new ConstraintList();
        list.addConstraint(1, LocalDate.parse("2014-01-01"));
        list.addConstraint(1, LocalDate.parse("2014-01-01"), true);
    }
}
