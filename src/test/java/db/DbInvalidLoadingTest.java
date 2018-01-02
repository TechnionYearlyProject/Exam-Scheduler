package test.java.db;

import db.Database;
import db.exception.SemesterFileMissing;
import db.exception.SemesterNotFound;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class DbInvalidLoadingTest {

    public static Database db;
    public static String baseDir;

    @Before
    public void initDb() {
        db = new Database();
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2)
                + "src" + db.sep + "test" + db.sep + "java" + db.sep + "db";
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
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        // No semester with same year or sem string
        try {
            db.loadSemester(2017, "spring");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterNotFound ignored) {
            // Nothing to do, expected case
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        // Same year but not same sem string
        try {
            db.loadSemester(2010, "spring");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterNotFound ignored) {
            // Nothing to do, expected case
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        // Same sem string but not same year
        try {
            db.loadSemester(2017, "winter");
            fail("Should have thrown SemesterNotFound exception");
        } catch (SemesterNotFound ignored) {
            // Nothing to do, expected case
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }

    @Test
    public void loadMissingFilesTest() {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        Map<Integer, String> map = new HashMap<>();
        map.put(2010, "study_programs");
        map.put(2011, "courses");
        map.put(2012, "scheduleA");
        map.put(2013, "scheduleB");
        map.put(2014, "constraintsA");
        map.put(2015, "constraintsB");
        for (int year: map.keySet()) {
            try {
                db.loadSemester(year, "winter");
                fail("Should have thrown SemesterFileMissing exception");
            } catch (SemesterFileMissing e) {
                String message = "Missing file in semester: " + db.baseDirectory + db.sep + Integer.toString(year) +
                        "_winter" + db.sep + map.get(year) + ".xml";
                assertEquals(e.getMessage(), message);
            } catch (Exception e) {
                fail("Unexpected exception: " + e.toString());
            }
        }
    }
}
