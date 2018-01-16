/*
package Logic;

import db.Course;
import db.Database;
import db.Semester;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
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
        List<Integer> coursesIds = new LinkedList<>();
        for (Course course: courses) {
            coursesIds.add(course.id);
        }
        loader = new CourseLoader(semester, null);
        Logic.Course prev = null;
        for (Logic.Course course: loader.getSortedCourses()) {
            assert(coursesIds.contains(course.getCourseID()));
            int conflicts = course.getNumOfConflictCourses();
            assert(conflicts >= 0 && conflicts <=30); //There is no way to
            if (prev != null){
                //List of courses need to be sorted in descending order by num of conflicts
                assert(prev.getNumOfConflictCourses() >= conflicts);
            }
            prev = course;
            // course to have more conflicts in our db
        }
    }

}*/
