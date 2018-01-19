package db_test;

import db.Course;
import db.Database;
import db.Semester;
import db.exception.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.*;

public class DbSemesterManipulationTest {
    public static Database db;

    @Before
    public void initDb() {
        db = new Database();
        db.baseDirectory = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep +
                "db_test" + db.sep + "empty_db";
    }

    @Test
    public void addRemoveStudyProgram() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addStudyProgram("There is no spoon");
        semester.addStudyProgram("I see dead people");
        semester.addStudyProgram("Hasta la vista baby");
        semester.addStudyProgram("Zed is dead");
        assertEquals(4, semester.getStudyProgramCollection().size());
        semester.removeStudyProgram("I see dead people");
        semester.removeStudyProgram("Zed is dead");
        assertEquals(2, semester.getStudyProgramCollection().size());
        semester.removeStudyProgram("I see dead people"); // No effect
        assertEquals(2, semester.getStudyProgramCollection().size());
    }

    @Test(expected = StudyProgramAlreadyExist.class)
    public void addDuplicateStudyProgram() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addStudyProgram("There is no spoon");
        semester.addStudyProgram("I see dead people");
        semester.addStudyProgram("There is no spoon");
    }

    @Test
    public void addRemoveCourse() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(123, "North By Northwest", 5.0);
        semester.addCourse(456, "Gone with the wind", 3.0);
        semester.addCourse(111, "Kill Bill", 3.0);
        semester.addCourse(7777, "Kill Bill", 3.0); // Course is defined by ID
        semester.addCourse(1234, "The good, the bad and the ugly", 7.0);
        assertEquals(5, semester.getCourseCollection().size());

        Course c = semester.getCourse(1234);
        assertEquals("The good, the bad and the ugly", c.courseName);

        semester.removeCourse(123);
        semester.removeCourse(7777);
        semester.removeCourse(111);
        assertEquals(2, semester.getCourseCollection().size());
        semester.removeCourse(123); // No effect
        semester.removeCourse(111); // No effect
        assertEquals(2, semester.getCourseCollection().size());
    }

    @Test(expected = CourseAlreadyExist.class)
    public void addDuplicateCourse() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(123, "North By Northwest", 5.0);
        semester.addCourse(456, "Gone with the wind", 3.0);
        semester.addCourse(111, "Kill Bill", 3.0);
        semester.addCourse(456, "Wowowow guy calm down", 99.99);
    }

    @Test(expected = CourseUnknown.class)
    public void getUnknownCourse() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(123, "North By Northwest", 5.0);
        semester.addCourse(456, "Gone with the wind", 3.0);
        semester.addCourse(111, "Kill Bill", 3.0);
        semester.getCourse(1234);
    }

    @Test(expected = CourseFirstAndLast.class)
    public void addCourseFirstAndLast() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(123, "North By Northwest", 5.0, 4,
                true, true, true, true);
    }

    @Test
    public void registerCourses() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);

        semester.addStudyProgram("There is no spoon");
        semester.addStudyProgram("Hasta la vista baby");
        semester.addStudyProgram("Zed is dead");
        semester.addCourse(123, "North By Northwest", 5.0);
        semester.addCourse(7777, "Kill Bill", 3.0);
        semester.addCourse(1234, "The good, the bad and the ugly", 7.0);
        semester.registerCourse(123, "There is no spoon", 1);
        semester.registerCourse(123, "Hasta la vista baby", 3);
        semester.registerCourse(123, "Zed is dead", 2);
        semester.registerCourse(7777, "There is no spoon", 1);
        semester.registerCourse(7777, "Hasta la vista baby", 7);
        semester.registerCourse(7777, "Zed is dead", 99);
        semester.registerCourse(1234, "There is no spoon", 1);
        semester.registerCourse(1234, "Hasta la vista baby", 2);
        semester.registerCourse(1234, "Zed is dead", 123);

        List<Course> courses = semester.getCourseCollection();
        List<String> programs = semester.getStudyProgramCollection();
        for (Course c: courses) {
            for (String p: programs) {
                assertNotSame(0, c.getStudyProgramSemester(p));
            }
        }

        courses = semester.getCourseBySemester(1);
        List<Integer> ids = new ArrayList<>();
        ids.add(123);
        ids.add(7777);
        ids.add(1234);
        assertEquals(ids.size(), courses.size());
        for(Course course: courses) {
            assertTrue(ids.contains(course.courseID));
        }

        semester.unregisterCourse(123, "There is no spoon");
        semester.unregisterCourse(123, "Hasta la vista baby");
        semester.unregisterCourse(123, "Zed is dead");
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

        semester.removeCourse(123);
        semester.removeStudyProgram("Zed is dead");
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

        // No effects
        semester.removeStudyProgram("Zed is dead");
        semester.unregisterCourse(1, "Zed is dead");
        semester.unregisterCourse(123, "Zed is dead");
    }

    @Test(expected = CourseUnknown.class)
    public void registerUnknownCourse() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addStudyProgram("There is no spoon");
        semester.addStudyProgram("Hasta la vista baby");
        semester.addCourse(7777, "Kill Bill", 3.0);
        semester.addCourse(1234, "The good, the bad and the ugly", 7.0);
        semester.registerCourse(1, "There is no spoon", 2);
    }

    @Test(expected = StudyProgramUnknown.class)
    public void registerUnknwonProgram() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addStudyProgram("There is no spoon");
        semester.addStudyProgram("Hasta la vista baby");
        semester.addCourse(7777, "Kill Bill", 3.0);
        semester.addCourse(1234, "The good, the bad and the ugly", 7.0);
        semester.registerCourse(1234, "Try again my dear", 2);
    }

    @Test
    public void scheduleCourses() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.addCourse(2, "Paul", 4.5);
        semester.addCourse(3, "Ringo", 4.5);
        semester.addCourse(4, "Georges", 4.5);

        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.setStartDate(Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"));
        semester.setEndDate(Semester.Moed.MOED_B, LocalDate.parse("2018-02-28"));

        semester.scheduleCourse(1, Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.scheduleCourse(2, Semester.Moed.MOED_A, LocalDate.parse("2018-01-02"));
        semester.scheduleCourse(3, Semester.Moed.MOED_A, LocalDate.parse("2018-01-03"));
        semester.scheduleCourse(4, Semester.Moed.MOED_A, LocalDate.parse("2018-01-04"));
        semester.scheduleCourse(1, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"));
        semester.scheduleCourse(2, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"));
        semester.scheduleCourse(3, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"));
        semester.scheduleCourse(4, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"));
        assertEquals(4, semester.getSchedule(Semester.Moed.MOED_A).size());
        assertEquals(4, semester.getSchedule(Semester.Moed.MOED_B).size());

        semester.unscheduleCourse(1, Semester.Moed.MOED_A);
        semester.unscheduleCourse(2, Semester.Moed.MOED_B);
        assertEquals(3, semester.getSchedule(Semester.Moed.MOED_A).size());
        assertEquals(3, semester.getSchedule(Semester.Moed.MOED_B).size());
        semester.removeCourse(4);
        assertEquals(2, semester.getSchedule(Semester.Moed.MOED_A).size());
        assertEquals(2, semester.getSchedule(Semester.Moed.MOED_B).size());
        semester.unscheduleCourse(1, Semester.Moed.MOED_A);
        semester.unscheduleCourse(2, Semester.Moed.MOED_B);
        assertEquals(2, semester.getSchedule(Semester.Moed.MOED_A).size());
        assertEquals(2, semester.getSchedule(Semester.Moed.MOED_B).size());
    }

    @Test(expected = UninitializedSchedule.class)
    public void uninitializedSchedule() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.scheduleCourse(1, Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
    }

    @Test(expected = InvalidSchedule.class)
    public void scheduleEndBeforeStart() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2017-01-01"));
    }

    @Test(expected = InvalidSchedule.class)
    public void scheduleStartAfterEnd() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.setEndDate(Semester.Moed.MOED_B, LocalDate.parse("2018-02-28"));
        semester.setStartDate(Semester.Moed.MOED_B, LocalDate.parse("2018-03-01"));
    }

    @Test(expected = CourseUnknown.class)
    public void scheduleUnknownCourse() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.addCourse(2, "Paul", 4.5);
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.scheduleCourse(10, Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
    }

    @Test(expected = DateOutOfSchedule.class)
    public void examOutOfSchedule() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.addCourse(2, "Paul", 4.5);
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.scheduleCourse(1, Semester.Moed.MOED_A, LocalDate.parse("2017-01-15"));
    }

    @Test
    public void addRemoveConstraints() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.addCourse(2, "Paul", 4.5);
        semester.addCourse(3, "Ringo", 4.5);
        semester.addCourse(4, "Georges", 4.5);

        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.setStartDate(Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"));
        semester.setEndDate(Semester.Moed.MOED_B, LocalDate.parse("2018-02-28"));

        semester.addConstraint(1, Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.addConstraint(2, Semester.Moed.MOED_A, LocalDate.parse("2018-01-02"));
        semester.addConstraint(3, Semester.Moed.MOED_A, LocalDate.parse("2018-01-03"));
        semester.addConstraint(4, Semester.Moed.MOED_A, LocalDate.parse("2018-01-04"));
        semester.addConstraint(1, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"), true);
        semester.addConstraint(2, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"), true);
        semester.addConstraint(3, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"), true);
        semester.addConstraint(4, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"), true);

        assertEquals(4, semester.getConstraintLists(Semester.Moed.MOED_A).size());
        assertEquals(4, semester.getConstraintLists(Semester.Moed.MOED_B).size());
        for (int i = 1; i < 4; i++) {
            assertEquals(1, semester.getConstraintList(Semester.Moed.MOED_A, i).size());
            assertEquals(1, semester.getConstraintList(Semester.Moed.MOED_B, i).size());
        }

        semester.removeConstraint(4, Semester.Moed.MOED_A, LocalDate.parse("2018-01-04"));
        semester.removeConstraint(3, Semester.Moed.MOED_B, LocalDate.parse("2018-02-01"));
        assertEquals(0, semester.getConstraintList(Semester.Moed.MOED_A, 4).size());
        assertEquals(0, semester.getConstraintList(Semester.Moed.MOED_B, 3).size());

        semester.removeCourse(1);
        assertEquals(3, semester.getConstraintLists(Semester.Moed.MOED_A).size());
        assertEquals(3, semester.getConstraintLists(Semester.Moed.MOED_B).size());

        // No effects
        semester.removeConstraint(5, Semester.Moed.MOED_B, LocalDate.parse("2018-01-01"));
        semester.removeConstraint(4, Semester.Moed.MOED_A, LocalDate.parse("2018-01-04"));
    }

    @Test(expected = UninitializedSchedule.class)
    public void constraintUninitializedSchedule() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.addConstraint(1, Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
    }

    @Test(expected = DateOutOfSchedule.class)
    public void constraintBeforeSchedule() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.addConstraint(1, Semester.Moed.MOED_A, LocalDate.parse("2017-01-01"));
    }

    @Test(expected = DateOutOfSchedule.class)
    public void constraintAfterSchedule() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.addConstraint(1, Semester.Moed.MOED_A, LocalDate.parse("2019-01-01"));
    }

    @Test(expected = CourseUnknown.class)
    public void constraintUnknownCourse() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.addConstraint(1, Semester.Moed.MOED_A, LocalDate.parse("2018-01-15"));
    }

    @Test(expected = DuplicateConstraints.class)
    public void addDuplicateConstraint() throws SemesterAlreadyExist, InvalidDatabase {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        semester.addCourse(1, "John", 4.5);
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        semester.addConstraint(1, Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.addConstraint(1, Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
    }
}

