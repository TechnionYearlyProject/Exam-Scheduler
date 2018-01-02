package test.java.db;

import db.Database;
import db.exception.InvalidDatabase;
import db.exception.SemesterNotFound;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class DbInvalidLoadingTests {

    public static Database db;
    public static String baseDir;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory + db.sep + "test" + db.sep + "java" + db.sep + "db";
    }

    @Test
    public void loadInexistantSemesterTest() {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        // No semesters at all
        try {
            db.loadSemester(2017, "winter");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterNotFound ignored) {
            // Nothing to do, expected case
        } catch (InvalidDatabase invalidDatabase) {
            fail("Unexpected exception");
        }
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        // No semester with same year or sem string
        try {
            db.loadSemester(2017, "spring");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterNotFound ignored) {
            // Nothing to do, expected case
        } catch (InvalidDatabase invalidDatabase) {
            fail("Unexpected exception");
        }
        // Same year but not same sem string
        try {
            db.loadSemester(2010, "spring");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterNotFound ignored) {
            // Nothing to do, expected case
        } catch (InvalidDatabase invalidDatabase) {
            fail("Unexpected exception");
        }
        // Same sem string but not same year
        try {
            db.loadSemester(2017, "winter");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterNotFound ignored) {
            // Nothing to do, expected case
        } catch (InvalidDatabase invalidDatabase) {
            fail("Unexpected exception");
        }
    }
}
