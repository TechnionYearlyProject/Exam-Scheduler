package Logic;

import db.Course;
import db.Database;
import db.Semester;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

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
            day.insertCourse(course.courseID, 0);
        }
        for (Course course: semester.getCourseCollection()) {
            assert(day.getDistance(course.courseID) == 0);
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
            if ((course.courseID / 1000) % 2 == 0){
                coef = -1;
            } else {
                coef = 1;
            }
            day.insertCourse(course.courseID, coef * (course.courseID % 5));
        }
        for (Course course: semester.getCourseCollection()){
            int coef;
            if ((course.courseID / 1000) % 2 == 0){
                coef = -1;
            } else {
                coef = 1;
            }
            int dist = coef * (course.courseID % 5);
            assert(day.getDistance(course.courseID) == dist);
        }
    }

    @Test
    public void getNumOfCourses() throws Exception {
        for (Course course: semester.getCourseCollection()) {
            day.insertCourse(course.courseID, 0);
        }
        assert(day.getNumOfCourses() == semester.getCourseCollection().size());
    }

    @Test
    public void getCoursesScheduledToTheDay() throws Exception {
        for (Course course: semester.getCourseCollection()) {
            day.insertCourse(course.courseID, 0);
        }
        List<Integer> scheduledCourses = day.getCoursesScheduledToTheDay();
        for (Integer courseId: scheduledCourses){
            assert(semester.getCourseCollection().stream().filter(c->c.courseID == courseId).findFirst().get().courseID == courseId);
        }
        for (Course course: semester.getCourseCollection()) {
            assert(scheduledCourses.stream().filter(id-> id.equals(course.courseID)).findFirst().get().equals(course.courseID));
        }
    }

}