package Logic;

import db.*;
import db.Course;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
            coursesIds.add(course.courseID);
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

    @Test
    public void constraintsAssignedCorrectTest(){
        ConstraintList cl = new ConstraintList();
        cl.constraints = semester.getConstraintLists(Semester.Moed.MOED_A);
        loader = new CourseLoader(semester, cl);
        List<Logic.Course> courses = loader.getSortedCourses();
        for (Logic.Course course: courses){
            List<Constraint> constraints= cl.getConstraints(course.getCourseID());
            if (constraints == null || constraints.size() == 0){
                continue;
            }
            List<Constraint> constraintsInCourse = course.getConstraints();
            for (Constraint constraint: constraints){
                assert (constraintsInCourse.contains(constraint));
            }
        }
    }
}
