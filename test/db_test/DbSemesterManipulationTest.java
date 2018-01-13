package db_test;

import db.Database;
import db.Semester;
import db.exception.CourseAlreadyExist;
import db.exception.CourseUnknown;
import db.exception.StudyProgramAlreadyExist;
import db.exception.StudyProgramUnknown;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class DbSemesterManipulationTest {
    public static Database db;
    private static String baseDir;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
    }

    @Test
    public void StudyProgramTest() {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        Semester semester = null;
        try {
            semester = db.createSemester(2017, "winter");
            assertNotNull(semester);
            semester.addStudyProgram("There is no spoon");
            semester.addStudyProgram("I see dead people");
            semester.addStudyProgram("Hasta la vista baby");
            semester.addStudyProgram("Zed is dead");
            assertEquals(4, semester.getStudyProgramCollection().size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.addStudyProgram("There is no spoon");
            fail("Should have thrown StudyProgramAlreadyExist exception");
        } catch (StudyProgramAlreadyExist e) {
            // Nothing to do
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.removeStudyProgram("I see dead people");
            semester.removeStudyProgram("Zed is dead");
            assertEquals(2, semester.getStudyProgramCollection().size());
            semester.removeStudyProgram("I see dead people"); // No effect
            assertEquals(2, semester.getStudyProgramCollection().size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }

    @Test
    public void CourseTest() {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        Semester semester = null;
        try {
            semester = db.createSemester(2017, "winter");
            assertNotNull(semester);
            semester.addCourse(123, "North By Northwest", 5.0);
            semester.addCourse(456, "Gone with the wind", 3.0);
            semester.addCourse(111, "Kill Bill", 3.0);
            semester.addCourse(7777, "Kill Bill", 3.0); // Course is defined by ID
            semester.addCourse(1234, "The good, the bad and the ugly", 7.0);
            assertEquals(5, semester.getCourseCollection().size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.addCourse(123, "Wowowow guy calm down", 99.99);
            fail("Should have thrown CourseAlreadyExist exception");
        } catch (CourseAlreadyExist e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.removeCourse(123);
            semester.removeCourse(7777);
            semester.removeCourse(111);
            assertEquals(2, semester.getCourseCollection().size());
            semester.removeCourse(123); // No effect
            semester.removeCourse(111); // No effect
            assertEquals(2, semester.getCourseCollection().size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }

    @Test
    public void CourseRegisterTest() {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        Semester semester = null;
        try {
            semester = db.createSemester(2017, "winter");
            assertNotNull(semester);
            semester.addStudyProgram("There is no spoon");
            semester.addStudyProgram("I see dead people");
            semester.addStudyProgram("Hasta la vista baby");
            semester.addStudyProgram("Zed is dead");
            semester.addCourse(123, "North By Northwest", 5.0);
            semester.addCourse(456, "Gone with the wind", 3.0);
            semester.addCourse(111, "Kill Bill", 3.0);
            semester.addCourse(7777, "Kill Bill", 3.0);
            semester.addCourse(1234, "The good, the bad and the ugly", 7.0);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.registerCourse(123, "There is no spoon", 1);
            semester.registerCourse(123, "I see dead people", 1);
            semester.registerCourse(123, "Hasta la vista baby", 3);
            semester.registerCourse(123, "Zed is dead", 2);
            semester.registerCourse(456, "There is no spoon", 4);
            semester.registerCourse(456, "I see dead people", 4);
            semester.registerCourse(456, "Hasta la vista baby", 4);
            semester.registerCourse(456, "Zed is dead", 4);
            semester.registerCourse(111, "There is no spoon", 1);
            semester.registerCourse(111, "I see dead people", 1);
            semester.registerCourse(111, "Hasta la vista baby", 1);
            semester.registerCourse(111, "Zed is dead", 1);
            semester.registerCourse(7777, "There is no spoon", 3);
            semester.registerCourse(7777, "I see dead people", 5);
            semester.registerCourse(7777, "Hasta la vista baby", 7);
            semester.registerCourse(7777, "Zed is dead", 99);
            semester.registerCourse(1234, "There is no spoon", 4);
            semester.registerCourse(1234, "I see dead people", 3);
            semester.registerCourse(1234, "Hasta la vista baby", 2);
            semester.registerCourse(1234, "Zed is dead", 123);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.registerCourse(0, "There is no spoon", 2);
            fail("Should have thrown CourseUnknown exception");
        } catch (CourseUnknown e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.registerCourse(123, "Try again my dear", 2);
            fail("Should have thrown StudyProgramUnknown exception");
        } catch (StudyProgramUnknown e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }
}
