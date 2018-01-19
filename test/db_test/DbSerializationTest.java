package db_test;

import db.Course;
import db.Database;
import db.Semester;
import db.exception.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

import static junit.framework.TestCase.*;

public class DbSerializationTest {
    public static Database db;

    @Before
    public void initDb() {
        db = new Database();
        db.baseDirectory = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test" +
                db.sep + "empty_db_serialization";
    }

    @After
    public void deleteDb() {
        File baseDir = new File(db.baseDirectory);
        File[] dirs = baseDir.listFiles();
        if (dirs == null) {
            return;
        }
        for (File dir: dirs) {
            if (dir.getName().endsWith("dummy.txt")) {
                continue;
            }
            File[] files = dir.listFiles();
            if (files == null) {
                continue;
            }
            for (File f: files) {
                f.delete();
            }
            dir.delete();
        }
    }

    @Test
    public void saveEmptySemesterTest() throws SemesterAlreadyExist, SemesterNotFound, InvalidDatabase,
            SemesterFileMissing {
        Semester semester = db.createSemester(2017, "winter");
        assertNotNull(semester);
        db.saveSemester(2017, "winter");

        initDb(); // Create new db without loaded semesters
        semester = db.loadSemester(2017, "winter");
        assertNotNull(semester);

        // Check that all fields are empty
        assertEquals(0, semester.getStudyProgramCollection().size());
        assertEquals(0, semester.getCourseCollection().size());
        assertNull(semester.getStartDate(Semester.Moed.MOED_A));
        assertNull(semester.getEndDate(Semester.Moed.MOED_A));
        assertNull(semester.getStartDate(Semester.Moed.MOED_B));
        assertNull(semester.getEndDate(Semester.Moed.MOED_B));
        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_A).size());
        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_B).size());
        assertEquals(0, semester.getConstraintLists(Semester.Moed.MOED_A).size());
        assertEquals(0, semester.getConstraintLists(Semester.Moed.MOED_B).size());
        assertEquals(0, semester.conflicts.size());
    }

    @Test
    public void saveCompleteSemesterTest() throws SemesterAlreadyExist, InvalidDatabase, SemesterNotFound,
            SemesterFileMissing {
        List<String> programs = new ArrayList<>();
        programs.add("Led Zeppelin");
        programs.add("Dire Strait");
        programs.add("Pink Floyd");

        Map<Integer, String> courses = new HashMap<>();
        courses.put(101, "Physical Graffiti");
        courses.put(102, "A Night At The Opera");
        courses.put(103, "Dark Side Of The Moon");
        courses.put(104, "Brother In Arm");

        Map<Integer, LocalDate> exams = new HashMap<>();
        exams.put(101, LocalDate.parse("2018-01-04"));
        exams.put(102, LocalDate.parse("2018-01-13"));
        exams.put(104, LocalDate.parse("2018-01-19"));

        Map<Integer, LocalDate> constraints = new HashMap<>();
        constraints.put(101, LocalDate.parse("2018-01-04"));
        constraints.put(102, LocalDate.parse("2018-01-13"));
        constraints.put(103, LocalDate.parse("2018-01-21"));

        Map<Integer, Set<Integer>> conflicts = new HashMap<>();
        conflicts.put(101, new HashSet<>());
        conflicts.get(101).add(103);
        conflicts.put(103, new HashSet<>());
        conflicts.get(103).add(101);

        // Semester generation
        Semester semester = db.createSemester(2018, "winter");
        assertNotNull(semester);
        for (String program: programs) {
            semester.addStudyProgram(program);
        }
        for (Map.Entry<Integer, String> e: courses.entrySet()) {
            semester.addCourse(e.getKey(), e.getValue(), 3.0,
                    2, false, false, true, true);
        }
        int i = 0;
        for (Integer courseId: courses.keySet()) {
            for (String prog: programs) {
                semester.registerCourse(courseId, prog, (i % 8) + 1);
                i += 1;
            }
        }
        semester.setStartDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-01"));
        semester.setEndDate(Semester.Moed.MOED_A, LocalDate.parse("2018-01-31"));
        for (Integer courseId: exams.keySet()) {
            semester.scheduleCourse(courseId, Semester.Moed.MOED_A, exams.get(courseId));
        }
        for (Integer courseId: constraints.keySet()) {
            semester.addConstraint(courseId, Semester.Moed.MOED_A, constraints.get(courseId), false);
        }
        semester.conflicts = conflicts;

        // Write and reload semester
        db.saveSemester(2018, "winter");
        initDb(); // Create new db without loaded semesters
        semester = db.loadSemester(2018, "winter");
        assertNotNull(semester);

        // Check study programs
        assertEquals(3, semester.getStudyProgramCollection().size());
        for (String program: semester.getStudyProgramCollection()) {
            assertTrue(programs.contains(program));
        }

        // Check courses
        assertEquals(4, semester.getCourseCollection().size());
        for (Course course: semester.getCourseCollection()) {
            assertTrue(courses.get(course.courseID).equals(course.courseName));
            assertEquals(3.0, course.creditPoints);
            assertEquals(2, course.getDaysBefore());
            assertFalse(course.isFirst());
            assertFalse(course.isLast());
            assertTrue(course.isRequired);
            assertTrue(course.hasExam);
        }

        // Check schedules
        assertEquals(3, semester.getSchedule(Semester.Moed.MOED_A).size());
        assertEquals(LocalDate.parse("2018-01-01"), semester.getStartDate(Semester.Moed.MOED_A));
        assertEquals(LocalDate.parse("2018-01-31"), semester.getEndDate(Semester.Moed.MOED_A));
        for (int courseId: semester.getSchedule(Semester.Moed.MOED_A).keySet()) {
            assertEquals(exams.get(courseId), semester.getSchedule(Semester.Moed.MOED_A).get(courseId));
        }

        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_B).size());
        assertNull(semester.getStartDate(Semester.Moed.MOED_B));
        assertNull(semester.getEndDate(Semester.Moed.MOED_B));

        // Check constraints
        assertEquals(3, semester.getConstraintLists(Semester.Moed.MOED_A).size());
        for (int courseId: semester.getConstraintLists(Semester.Moed.MOED_A).keySet()) {
            assertEquals(constraints.get(courseId), semester.getConstraintList(Semester.Moed.MOED_A, courseId).get(0).date);
        }

        assertEquals(0, semester.getConstraintLists(Semester.Moed.MOED_B).size());

        // Check conflicts
        for (Integer courseId: conflicts.keySet()) {
            for (Integer conflictId: conflicts.get(courseId)) {
                assertTrue(semester.conflicts.get(courseId).contains(conflictId));
            }
        }
    }
}
