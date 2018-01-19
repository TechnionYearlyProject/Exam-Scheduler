package db_test;

import db.Course;
import db.Database;
import db.Semester;
import db.exception.SemesterAlreadyExist;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertNull;

public class DbSemesterCreationTest {
    public static Database db;
    private static String baseDir;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
    }

    @Test
    public void createFromEmptyDatabase() throws SemesterAlreadyExist {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        Semester semester = db.createSemester(2017, "winter");
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

    @Test(expected = SemesterAlreadyExist.class)
    public void createDuplicateSemester() throws SemesterAlreadyExist {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        db.createSemester(2017, "winter");
        db.createSemester(2017, "winter");
    }

    @Test
    public void createFromInvalidDatabases() throws SemesterAlreadyExist {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        Semester semester = db.createSemester(2015, "winter");
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
    public void createFromValidDatabaseTest() throws SemesterAlreadyExist {
        db.baseDirectory = baseDir + db.sep + "valid_db";
        Semester semester = db.createSemester(2018, "winter");

        // Check study programs
        List<String> programs = semester.getStudyProgramCollection();
        assertEquals(3, programs.size());
        assertTrue(programs.contains("Led Zeppelin"));
        assertTrue(programs.contains("Pink Floyd"));
        assertTrue(programs.contains("Dire Strait"));

        // Check courses
        Map<Integer, Course> courses = new HashMap<>();
        for (Course c: semester.getCourseCollection()) {
            courses.put(c.courseID, c);
        }
        assertEquals(4, courses.size());

        assertTrue(courses.keySet().contains(101));
        assertEquals("Physical Graffiti", courses.get(101).courseName);
        assertEquals(3.5, courses.get(101).creditPoints);
        assertEquals(2, courses.get(101).getDaysBefore());
        assertTrue(courses.get(101).isFirst());
        assertFalse(courses.get(101).isLast());
        assertTrue(courses.get(101).isRequired);
        assertTrue(courses.get(101).hasExam);
        for(String program: programs) {
            assertEquals(1, courses.get(101).getStudyProgramSemester(program));
        }

        assertTrue(courses.keySet().contains(102));
        assertEquals("A Night At The Opera", courses.get(102).courseName);
        assertEquals(3.0, courses.get(102).creditPoints);
        assertEquals(2, courses.get(102).getDaysBefore());
        assertFalse(courses.get(102).isFirst());
        assertFalse(courses.get(102).isLast());
        assertTrue(courses.get(102).isRequired);
        assertTrue(courses.get(102).hasExam);
        for(String program: programs) {
            assertEquals(2, courses.get(102).getStudyProgramSemester(program));
        }

        assertTrue(courses.keySet().contains(103));
        assertEquals("Dark Side Of The Moon", courses.get(103).courseName);
        assertEquals(5.0, courses.get(103).creditPoints);
        assertEquals(4, courses.get(103).getDaysBefore());
        assertFalse(courses.get(103).isFirst());
        assertFalse(courses.get(103).isLast());
        assertFalse(courses.get(103).isRequired);
        assertTrue(courses.get(103).hasExam);
        for(String program: programs) {
            if (program.equals("Dire Strait")) {
                assertEquals(0, courses.get(103).getStudyProgramSemester(program));
            } else {
                assertEquals(3, courses.get(103).getStudyProgramSemester(program));
            }
        }

        assertTrue(courses.keySet().contains(104));
        assertEquals("Brothers In Arm", courses.get(104).courseName);
        assertEquals(5.5, courses.get(104).creditPoints);
        assertEquals(2, courses.get(104).getDaysBefore());
        assertFalse(courses.get(104).isFirst());
        assertTrue(courses.get(104).isLast());
        assertTrue(courses.get(104).isRequired);
        assertFalse(courses.get(104).hasExam);
        for(String program: programs) {
            if (program.equals("Dire Strait")) {
                assertEquals(0, courses.get(104).getStudyProgramSemester(program));
            } else {
                assertEquals(4, courses.get(104).getStudyProgramSemester(program));
            }
        }
    }
}
