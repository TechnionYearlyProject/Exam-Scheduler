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
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class DbValidLoadingTest {

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
    public void loadInexistantSemesterTest() {
        db.baseDirectory = baseDir + db.sep + "valid_db";
        try {
            db.loadSemester(2017, "winter");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        Semester semester = db.getSemester(2017, "winter");
        assertNotNull(semester);

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

        // Check schedules
        Calendar startDateA = parse("2017-01-01", dateParser);
        Calendar endDateA = parse("2017-02-01", dateParser);
        Calendar startDateB = parse("None", dateParser);
        Calendar endDateB = parse("None", dateParser);
        assertEquals(startDateA, semester.getStartDate(Semester.Moed.MOED_A));
        assertEquals(endDateA, semester.getEndDate(Semester.Moed.MOED_A));
        assertEquals(startDateB, semester.getStartDate(Semester.Moed.MOED_B));
        assertEquals(endDateB, semester.getEndDate(Semester.Moed.MOED_B));
        Map<Integer, Calendar> schedulesRef = new HashMap<>();
        schedulesRef.put(104166, parse("2017-01-03 13:00", hourParser));
        schedulesRef.put(104031, parse("2017-01-03 09:00", hourParser));
        schedulesRef.put(234114, parse("2017-01-07 09:00", hourParser));
        assertEquals(0, semester.getSchedule(Semester.Moed.MOED_B).size());
        assertEquals(schedulesRef.size(), semester.getSchedule(Semester.Moed.MOED_A).size());
        for (int courseId: semester.getSchedule(Semester.Moed.MOED_A).keySet()) {
            assertEquals(schedulesRef.get(courseId), semester.getSchedule(Semester.Moed.MOED_A).get(courseId));
        }

        // Check constraints
        for (int courseId: coursesRef) {
            assertEquals(null, semester.getConstraintList(Semester.Moed.MOED_B, courseId));
        }
        List<Constraint> constraintsRef = new ArrayList<>();
        constraintsRef.add(new Constraint(parse("2017-01-01", dateParser), parse("2017-01-03", dateParser)));
        constraintsRef.add(new Constraint(parse("2017-01-04", dateParser), parse("2017-01-07", dateParser)));
        assertEquals(constraintsRef.size(), semester.getConstraintList(Semester.Moed.MOED_A, 104031).size());
        for (Constraint ref: constraintsRef) {
            boolean check = false;
            for (Constraint constrait: semester.getConstraintList(Semester.Moed.MOED_A, 104031)) {
                if (constrait.start.equals(ref.start) && constrait.end.equals(ref.end)) {
                    check = true;
                    break;
                }
            }
            assertEquals(true, check);
        }
    }
}
