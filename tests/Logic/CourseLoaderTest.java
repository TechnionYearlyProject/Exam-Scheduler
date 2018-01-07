package Logic;

import db.Course;
import db.Database;
import db.Semester;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CourseLoaderTest {
    Database db;
    CourseLoader loader;
    Semester semester;
    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.loadSemester(2017, "winter_test");
        semester = db.getSemester(2017, "winter_test");
    }

    @Test
    public void getCourses() throws Exception {
        List<Course> courses = semester.getCourseCollection();
        for (Course course: courses) {
            System.out.println(course.id);
        }
        System.out.println("+++++++++++++++++++++HERE++++++++++++++++");
        loader = new CourseLoader(db, null);
        for (Logic.Course course: loader.getSortedCourses()) {
            System.out.println(course.getCourseID() + " " + course.getNumOfConflictCourses());
        }
    }

}