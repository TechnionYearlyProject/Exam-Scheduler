package db_test;

import db.Database;
import db.Semester;
import db.exception.SemesterAlreadyExist;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class DbSemesterCreationTest {
    public static Database db;
    private static String baseDir;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
    }

    @Test
    public void createFromEmptyDatabase() {
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
    public void createFromInvalidDatabases() {
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
}
