package Logic;

import db.*;
import db.Course;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**@author mvainbau
 * @date 07/01/2018*/
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

    @Test
    public void removeCourseCompletelyTest() {
        loader = new CourseLoader(semester, null);
        assertEquals(1, loader.getSortedCourses().stream().filter(c->c.getCourseID() == 234247).collect(Collectors.toList()).size());
        assertNotEquals(null, loader.getCourse(234247));
        loader.removeCourseCompletely(234247);
        assertEquals(null, loader.getCourse(234247));
    }

    @Test
    public void removeCourseTest(){
        loader = new CourseLoader(semester, null);
        assertEquals(1, loader.getSortedCourses().stream().filter(c->c.getCourseID() == 234247).collect(Collectors.toList()).size());
        assertNotEquals(null, loader.getCourse(234247));
        assert (loader.getSortedCourses().contains(loader.getCourse(234247)));
        loader.removeCourse(234247);
        assertNotEquals(null, loader.getCourse(234247));
        assertFalse(loader.getSortedCourses().contains(loader.getCourse(234247)));
    }

    @Test
    public void addNewCourseTest(){
        loader = new CourseLoader(semester, null);
        HashMap<String, Integer> programs = new HashMap<>();
        programs.put("הנדסת תוכנה", 4);
        Logic.Course introToSpace = new Logic.Course("מבוא לחלל", 111000, true, 3, programs);
        loader.addNewCourse(introToSpace);
        assertNotEquals(null, loader.getCourses().get(111000));
        assert(loader.getSortedCourses().contains(introToSpace));
        Logic.Course iToSp = loader.getSortedCourses().stream().filter(c->c.getCourseID() == 111000).findFirst().get();
        assert(iToSp.getPrograms().contains(new Pair<>("הנדסת תוכנה", 4)));
        assertEquals(1, iToSp.getPrograms().size());
        assertNotEquals(null, iToSp.getConflictCourses().get(234247));
        assertNotEquals(null,iToSp.getConflictCourses().get(234123));
        assertEquals(null, iToSp.getConflictCourses().get(104032));
        assertEquals(null, iToSp.getConflictCourses().get(234118));
    }
}
