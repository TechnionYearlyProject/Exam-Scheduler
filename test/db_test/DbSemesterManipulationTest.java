package db_test;

import db.Course;
import db.Database;
import db.Semester;
import db.exception.*;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class DbSemesterManipulationTest {
    public static Database db;
    private static String baseDir;
    private static SimpleDateFormat dateParser;

    private Calendar parse(String str) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateParser.parse(str));
        } catch (ParseException e) {
            return null;
        }
        return cal;
    }

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
        dateParser = new SimpleDateFormat("yyyy-MM-dd");
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
        try {
            Course c = semester.getCourse(1234);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            Course c = semester.getCourse(123);
            fail("Should have thrown CourseUnknown exception");
        } catch (CourseUnknown e) {
            // Expected
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
            semester.registerCourse(123, "There is no spoon", 1);
            semester.registerCourse(123, "I see dead people", 1);
            semester.registerCourse(123, "Hasta la vista baby", 3);
            semester.registerCourse(123, "Zed is dead", 2);
            semester.registerCourse(456, "There is no spoon", 1);
            semester.registerCourse(456, "I see dead people", 4);
            semester.registerCourse(456, "Hasta la vista baby", 4);
            semester.registerCourse(456, "Zed is dead", 4);
            semester.registerCourse(111, "There is no spoon", 1);
            semester.registerCourse(111, "I see dead people", 1);
            semester.registerCourse(111, "Hasta la vista baby", 1);
            semester.registerCourse(111, "Zed is dead", 1);
            semester.registerCourse(7777, "There is no spoon", 1);
            semester.registerCourse(7777, "I see dead people", 5);
            semester.registerCourse(7777, "Hasta la vista baby", 7);
            semester.registerCourse(7777, "Zed is dead", 99);
            semester.registerCourse(1234, "There is no spoon", 1);
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
        try {
            List<Course> tmp = semester.getCourseBySemester(1);
            int idArray[] = {123, 456, 7777, 1234, 111};
            assertEquals(idArray.length, tmp.size());

        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }


        List<Course> courses = semester.getCourseCollection();
        List<String> programs = semester.getStudyProgramCollection();
        for (Course c: courses) {
            for (String p: programs) {
                assertNotSame(0, c.getStudyProgramSemester(p));
            }
        }
        try {
            semester.unregisterCourse(123, "There is no spoon");
            semester.unregisterCourse(123, "I see dead people");
            semester.unregisterCourse(123, "Hasta la vista baby");
            semester.unregisterCourse(123, "Zed is dead");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        courses = semester.getCourseCollection();
        for (Course c: courses) {
            for (String p: programs) {
                if (c.courseID == 123) {
                    assertEquals(0, c.getStudyProgramSemester(p));
                } else {
                    assertNotSame(0, c.getStudyProgramSemester(p));
                }
            }
        }
        try {
            semester.removeCourse(123);
            semester.removeStudyProgram("Zed is dead");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        courses = semester.getCourseCollection();
        programs = semester.getStudyProgramCollection();
        for (Course c: courses) {
            for (String p: programs) {
                if (p.equals("Zed is dead")) {
                    assertEquals(0, c.getStudyProgramSemester(p));
                } else {
                    assertNotSame(0, c.getStudyProgramSemester(p));
                }
            }
        }
        try {
            semester.removeStudyProgram("Zed is dead");
            semester.unregisterCourse(1, "Zed is dead");
            semester.unregisterCourse(123, "Zed is dead");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }

    @Test
    public void ScheduleTest() {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        Semester semester = null;
        try {
            semester = db.createSemester(2017, "winter");
            assertNotNull(semester);
            semester.addCourse(1, "John", 4.5);
            semester.addCourse(2, "Paul", 4.5);
            semester.addCourse(3, "Ringo", 4.5);
            semester.addCourse(4, "Georges", 4.5);
            // Can't schedule exam before setting start and end date of exam period
            semester.scheduleCourse(1, Semester.Moed.MOED_A, parse("2018-01-01"));
            fail("Should have thrown UninitializedSchedule exception");
        } catch (UninitializedSchedule e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Set start date
        try {
            semester.setStartDate(Semester.Moed.MOED_A, parse("2018-01-01"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Try to set end date before start date
        try {
            semester.setEndDate(Semester.Moed.MOED_A, parse("2017-01-01"));
            fail("Should have thrown UninitializedSchedule exception");
        } catch (InvalidSchedule e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Set end dates
        try {
            semester.setEndDate(Semester.Moed.MOED_A, parse("2018-01-31"));
            semester.setEndDate(Semester.Moed.MOED_B, parse("2018-02-28"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Try to set start date after end date
        try {
            semester.setStartDate(Semester.Moed.MOED_B, parse("2018-03-01"));
            fail("Should have thrown UninitializedSchedule exception");
        } catch (InvalidSchedule e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Set start date
        try {
            semester.setStartDate(Semester.Moed.MOED_B, parse("2018-02-01"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Schedule some exams, all are valid
        try {
            semester.scheduleCourse(1, Semester.Moed.MOED_A, parse("2018-01-01"));
            semester.scheduleCourse(2, Semester.Moed.MOED_A, parse("2018-01-02"));
            semester.scheduleCourse(3, Semester.Moed.MOED_A, parse("2018-01-03"));
            semester.scheduleCourse(4, Semester.Moed.MOED_A, parse("2018-01-04"));
            semester.scheduleCourse(1, Semester.Moed.MOED_B, parse("2018-02-01"));
            semester.scheduleCourse(2, Semester.Moed.MOED_B, parse("2018-02-01"));
            semester.scheduleCourse(3, Semester.Moed.MOED_B, parse("2018-02-01"));
            semester.scheduleCourse(4, Semester.Moed.MOED_B, parse("2018-02-01"));
            assertEquals(4, semester.getSchedule(Semester.Moed.MOED_A).size());
            assertEquals(4, semester.getSchedule(Semester.Moed.MOED_B).size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Try to schedule on non-existant course
        try {
            semester.scheduleCourse(10, Semester.Moed.MOED_A, parse("2018-01-01"));
            fail("Should have thrown CourseUnknown exception");
        } catch (CourseUnknown e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Try to schedule exam day before exam period start date
        try {
            semester.scheduleCourse(1, Semester.Moed.MOED_A, parse("2014-01-01"));
            fail("Should have thrown DateOutOfSchedule exception");
        } catch (DateOutOfSchedule e) {
            // Expected
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Unschedule some exams, should work
        try {
            semester.unscheduleCourse(1, Semester.Moed.MOED_A);
            semester.unscheduleCourse(2, Semester.Moed.MOED_B);
            assertEquals(3, semester.getSchedule(Semester.Moed.MOED_A).size());
            assertEquals(3, semester.getSchedule(Semester.Moed.MOED_B).size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Remove course, should remove schedules of this course
        try {
            semester.removeCourse(4);
            assertEquals(2, semester.getSchedule(Semester.Moed.MOED_A).size());
            assertEquals(2, semester.getSchedule(Semester.Moed.MOED_B).size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Unschedule non-existant schedules - no effect
        try {
            semester.unscheduleCourse(1, Semester.Moed.MOED_A);
            semester.unscheduleCourse(2, Semester.Moed.MOED_B);
            assertEquals(2, semester.getSchedule(Semester.Moed.MOED_A).size());
            assertEquals(2, semester.getSchedule(Semester.Moed.MOED_B).size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }
}
