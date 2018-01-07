package Logic;

import db.Course;
import db.Database;
import db.Semester;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class DayTest {
    Database db;
    Semester semester;
    Day day;
    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.loadSemester(2017, "winter_test");
        semester = db.getSemester(2017, "winter_test");
        day = new Day(LocalDate.now());
    }

    @Test
    public void insertCourse() throws Exception {
        for (Course course: semester.getCourseCollection()) {
            day.insertCourse(course.id, 0);
        }
        for (Course course: semester.getCourseCollection()) {
            assert(day.getDistance(course.id) == 0);
        }
    }

    @Test
    public void getDate() throws Exception {
        assert(day.getDate().equals(LocalDate.now()));
    }

    @Test
    public void getDistance() throws Exception {
        for (Course course: semester.getCourseCollection()){
            int coef;
            if ((course.id / 1000) % 2 == 0){
                coef = -1;
            } else {
                coef = 1;
            }
            day.insertCourse(course.id, coef * (course.id % 5));
        }
        for (Course course: semester.getCourseCollection()){
            int coef;
            if ((course.id / 1000) % 2 == 0){
                coef = -1;
            } else {
                coef = 1;
            }
            int dist = coef * (course.id % 5);
            assert(day.getDistance(course.id) == dist);
        }
    }

    @Test
    public void getNumOfCourses() throws Exception {
        for (Course course: semester.getCourseCollection()) {
            day.insertCourse(course.id, 0);
        }
        assert(day.getNumOfCourses() == semester.getCourseCollection().size());
    }

}