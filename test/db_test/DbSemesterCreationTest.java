package db_test;

import db.Constraint;
import db.Course;
import db.Database;
import db.Semester;
import db.exception.SemesterAlreadyExist;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class DbSemesterCreationTest {
    public static Database db;
    private static String baseDir;
    private static SimpleDateFormat dateParser, hourParser;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
        dateParser = new SimpleDateFormat("yyyy-MM-dd");
        hourParser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    private Calendar parse(String str, SimpleDateFormat parser) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(parser.parse(str));
        } catch (ParseException e) {
            return null;
        }
        return cal;
    }

    @Test
    public void createFromEmptyDatabaseTest() {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        Semester semester = null;
        try {
            semester = db.createSemester(2017, "winter");
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

        try {
            db.createSemester(2017, "winter");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterAlreadyExist e) {
            // Nothing to do, expected case
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }

    @Test
    public void createFromInvalidDatabasesTest() {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        Semester semester = null;
        try {
            semester = db.createSemester(2017, "winter");
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
    public void createFromValidDatabaseTest() {
        db.baseDirectory = baseDir + db.sep + "valid_db";
        Semester semester = null;
        try {
            semester = db.createSemester(2018, "winter");
            assertNotNull(semester);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }

        // Check study programs
        List<String> studyProgramsRef = new ArrayList<>();
        studyProgramsRef.add("מסלול כללי ארבע-שנתי");
        studyProgramsRef.add("מסלול כללי תלת-שנתי");
        studyProgramsRef.add("הנדסת תוכנה");
        assertEquals(studyProgramsRef.size(), semester.getStudyProgramCollection().size());
        for (String studyProgram: semester.getStudyProgramCollection()) {
            assertEquals(true, studyProgramsRef.contains(studyProgram));
        }

        // Check courses
        List<Integer> coursesRef = new ArrayList<>();
        coursesRef.add(234114);
        coursesRef.add(234145);
        coursesRef.add(104166);
        coursesRef.add(104031);
        Map<Integer, String> coursesNames = new HashMap<>();
        coursesNames.put(234114, "מבוא למדעי המחשב מ");
        coursesNames.put(234145, "מערכות ספרתיות");
        coursesNames.put(104166, "אלגברה א");
        coursesNames.put(104031, "חשבון אינפיניטסימלי 1מ");
        Map<Integer, Double> coursesWeights = new HashMap<>();
        coursesWeights.put(234114, 3.5);
        coursesWeights.put(234145, 3.0);
        coursesWeights.put(104166, 5.0);
        coursesWeights.put(104031, 5.5);
        Map<Integer, Map<String, Integer>> coursesPrograms = new HashMap<>();
        Map<String, Integer> tmp = new HashMap<>();
        tmp.put("מסלול כללי ארבע-שנתי", 1);
        tmp.put("מסלול כללי תלת-שנתי", 3);
        tmp.put("הנדסת תוכנה", 2);
        coursesPrograms.put(234114, tmp);
        tmp = new HashMap<>();
        tmp.put("מסלול כללי תלת-שנתי", 2);
        tmp.put("הנדסת תוכנה", 1);
        coursesPrograms.put(234145, tmp);
        tmp = new HashMap<>();
        tmp.put("מסלול כללי ארבע-שנתי", 2);
        tmp.put("מסלול כללי תלת-שנתי", 3);
        coursesPrograms.put(104166, tmp);
        tmp = new HashMap<>();
        tmp.put("מסלול כללי ארבע-שנתי", 2);
        tmp.put("מסלול כללי תלת-שנתי", 2);
        coursesPrograms.put(104031, tmp);
        assertEquals(coursesRef.size(), semester.getCourseCollection().size());
        for (Course course: semester.getCourseCollection()) {
            assertEquals(true, coursesRef.contains(course.id));
            assertEquals(course.name, coursesNames.get(course.id));
            assertEquals(course.weight, coursesWeights.get(course.id));
            assertEquals(course.studyProgramSize(), coursesPrograms.get(course.id).size());
            tmp = coursesPrograms.get(course.id);
            for (String program: tmp.keySet()) {
                if (course.getStudyProgramSemester(program) != 0) {
                    assertEquals((int) tmp.get(program), course.getStudyProgramSemester(program));
                }
            }
        }
    }
}
