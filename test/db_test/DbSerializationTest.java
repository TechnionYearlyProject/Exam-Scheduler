package db_test;

import db.Course;
import db.Database;
import db.Semester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.*;

public class DbSerializationTest {
    public static Database db;
    private static String baseDir;
    private static SimpleDateFormat dateParser;

    @Before
    public void initDb() {
        db = new Database();
        db.baseDirectory = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test" +
                db.sep + "empty_db_for_serialization";
        dateParser = new SimpleDateFormat("yyyy-MM-dd");
    }

    @After
    public void deleteDb() {
        File dir = new File(db.baseDirectory);
        if (dir.listFiles() == null) {
            return;
        }
        for (File sem: dir.listFiles()) {
            if (sem.getName().endsWith("dummy.txt")) {
                continue;
            }
            if (sem.listFiles() == null) {
                continue;
            }
            for (File xml_file: sem.listFiles()) {
                xml_file.delete();
            }
            sem.delete();
        }
    }

    private Calendar parse(String str) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateParser.parse(str));
        } catch (ParseException e) {
            return null;
        }
        return cal;
    }

    @Test
    public void saveEmptySemesterTest() {
        Semester semester = null;
        try {
            semester = db.createSemester(2017, "winter");
            assertNotNull(semester);
            db.saveSemester(2017, "winter");
            initDb(); // Create new db without loaded semesters
            semester = db.loadSemester(2017, "winter");
            assertNotNull(semester);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        assertEquals(0, semester.getStudyProgramCollection().size());
        assertEquals(0, semester.getCourseCollection().size());
        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_A).size());
        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_B).size());
        assertEquals(null, semester.getEndDate(Semester.Moed.MOED_A));
        assertEquals(null, semester.getEndDate(Semester.Moed.MOED_B));
        assertEquals(0, semester.getConstraintLists(Semester.Moed.MOED_A).size());
        assertEquals(0, semester.getConstraintLists(Semester.Moed.MOED_B).size());
    }

    @Test
    public void saveCompleteSemesterTest() {
        Semester semester = null;

        List<String> programs = new ArrayList<>();
        programs.add("Led Zeppelin");
        programs.add("Dire Strait");
        programs.add("Pink Floyd");

        Map<Integer, String> courses = new HashMap<>();
        courses.put(234123, "Dark side of the moon");
        courses.put(236315, "Physical Graffiti");
        courses.put(236777, "Abbey Road");
        courses.put(234126, "A Night at the Opera");

        Calendar startDateA = parse("2018-01-01");
        Calendar endDateA = parse("2018-01-31");

        Map<Integer, Calendar> exams = new HashMap<>();
        exams.put(234123, parse("2018-01-04"));
        exams.put(236315, parse("2018-01-13"));
        exams.put(236777, parse("2018-01-19"));

        Map<Integer, Calendar> constraints = new HashMap<>();
        constraints.put(234123, parse("2018-01-04"));
        constraints.put(236315, parse("2018-01-13"));
        constraints.put(234126, parse("2018-01-21"));

        try {
            semester = db.createSemester(2018, "winter");
            assertNotNull(semester);
            for (String program: programs) {
                semester.addStudyProgram(program);
            }
            for (Map.Entry<Integer, String> e: courses.entrySet()) {
                semester.addCourse(e.getKey(), e.getValue(), 3.0);
            }
            int i = 0;
            for (Integer courseId: courses.keySet()) {
                for (String prog: programs) {
                    semester.registerCourse(courseId, prog, (i % 8) + 1);
                    i += 1;
                }
            }
            semester.setStartDate(Semester.Moed.MOED_A, startDateA);
            semester.setEndDate(Semester.Moed.MOED_A, endDateA);
            for (Integer courseId: exams.keySet()) {
                semester.scheduleCourse(courseId, Semester.Moed.MOED_A, exams.get(courseId));
            }
            for (Integer courseId: constraints.keySet()) {
                semester.addConstraint(courseId, Semester.Moed.MOED_A, constraints.get(courseId), constraints.get(courseId));
            }
            db.saveSemester(2018, "winter");
            initDb(); // Create new db without loaded semesters
            semester = db.loadSemester(2018, "winter");
            assertNotNull(semester);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        assertEquals(3, semester.getStudyProgramCollection().size());
        for (String prog: semester.getStudyProgramCollection()) {
            assertTrue(programs.contains(prog));
        }
        assertEquals(4, semester.getCourseCollection().size());
        for (Course course: semester.getCourseCollection()) {
            assertTrue(courses.get(course.courseID).equals(course.courseName));
        }
        assertEquals(3, semester.getSchedule(Semester.Moed.MOED_A).size());
        for (int courseId: semester.getSchedule(Semester.Moed.MOED_A).keySet()) {
            assertEquals(exams.get(courseId), semester.getSchedule(Semester.Moed.MOED_A).get(courseId));
        }
        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_B).size());
        assertNotNull(semester.getStartDate(Semester.Moed.MOED_A));
        assertNotNull(semester.getEndDate(Semester.Moed.MOED_A));
        assertNull(semester.getStartDate(Semester.Moed.MOED_B));
        assertNull(semester.getEndDate(Semester.Moed.MOED_B));
        assertEquals(3, semester.getConstraintLists(Semester.Moed.MOED_A).size());
        for (int courseId: semester.getConstraintLists(Semester.Moed.MOED_A).keySet()) {
            assertEquals(constraints.get(courseId), semester.getConstraintList(Semester.Moed.MOED_A, courseId).get(0).start);
            assertEquals(constraints.get(courseId), semester.getConstraintList(Semester.Moed.MOED_A, courseId).get(0).end);
        }
        assertEquals(0, semester.getConstraintLists(Semester.Moed.MOED_B).size());
    }
}
