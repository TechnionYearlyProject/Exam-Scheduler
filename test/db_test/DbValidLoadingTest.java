package db_test;

import db.Constraint;
import db.Course;
import db.Database;
import db.Semester;
import db.exception.InvalidDatabase;
import db.exception.SemesterFileMissing;
import db.exception.SemesterNotFound;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;

public class DbValidLoadingTest {

    public static Database db;
    private static String baseDir;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
    }

    @Test
    public void loadValidEmptySemester() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "valid_db";
        db.loadSemester(2015, "spring");
        Semester semester = db.getSemester(2015, "spring");
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
    public void loadValidSemester() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "valid_db";
        db.loadSemester(2017, "winter");
        Semester semester = db.getSemester(2017, "winter");
        assertNotNull(semester);

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

        // Check schedules
        assertEquals(LocalDate.parse("2017-01-01"), semester.getStartDate(Semester.Moed.MOED_A));
        assertEquals(LocalDate.parse("2017-01-31"), semester.getEndDate(Semester.Moed.MOED_A));
        Map<Integer, LocalDate> moedA = semester.getSchedule(Semester.Moed.MOED_A);
        assertEquals(4, moedA.size());
        assertEquals(LocalDate.parse("2017-01-11"), moedA.get(101));
        assertEquals(LocalDate.parse("2017-01-07"), moedA.get(102));
        assertEquals(LocalDate.parse("2017-01-03"), moedA.get(103));
        assertEquals(LocalDate.parse("2017-01-03"), moedA.get(104));

        assertNull(semester.getStartDate(Semester.Moed.MOED_B));
        assertNull(semester.getEndDate(Semester.Moed.MOED_B));
        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_B).size());

        // Check constraints
        Map<Integer, List<Constraint>> constraints = semester.getConstraintLists(Semester.Moed.MOED_A);
        assertEquals(2, constraints.size());
        assertEquals(LocalDate.parse("2017-01-01"), constraints.get(102).get(0).date);
        assertEquals(LocalDate.parse("2017-01-04"), constraints.get(104).get(0).date);
        assertFalse(constraints.get(102).get(0).forbidden);
        assertTrue(constraints.get(104).get(0).forbidden);

        assertEquals(0, semester.getConstraintLists(Semester.Moed.MOED_B).size());

        // Check conflicts
        assertEquals(3, semester.conflicts.size());
        assertTrue(semester.conflicts.get(101).contains(102));
        assertTrue(semester.conflicts.get(101).contains(103));
        assertTrue(semester.conflicts.get(102).contains(101));
        assertTrue(semester.conflicts.get(103).contains(101));
    }
}
