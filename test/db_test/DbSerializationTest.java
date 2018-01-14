package db_test;

import db.Database;
import db.Semester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

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
            if (sem.listFiles() == null) {
                continue;
            }
            for (File xml_file: sem.listFiles()) {
                xml_file.delete();
            }
            sem.delete();
        }
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
}
