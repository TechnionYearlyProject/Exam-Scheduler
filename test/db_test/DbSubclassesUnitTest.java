package db_test;

import db.Constraint;
import db.Course;
import Logic.Exceptions.IllegalDaysBefore;
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
}
