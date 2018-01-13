package db_test;

import db.Database;
import db.Semester;
import db.exception.StudyProgramAlreadyExist;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

public class DbSemesterManipulationTest {
    public static Database db;
    private static String baseDir;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
    }

    @Test
    public void addRemoveSemesterTest() {
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
        } catch (StudyProgramAlreadyExist e) {
            // Nothing to do
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.removeStudyProgram("I see dead people");
            semester.removeStudyProgram("Zed is dead");
            assertEquals(2, semester.getStudyProgramCollection().size());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            semester.removeStudyProgram("There is no spoon");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }
}
