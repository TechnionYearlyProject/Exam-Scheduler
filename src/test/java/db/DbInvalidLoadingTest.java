package test.java.db;

import db.Database;
import db.exception.InvalidDatabase;
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
    private static String baseDir;

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

    @Test
    public void loadInvalidSemesterTest() {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        try {
            // course with missing name
            db.loadSemester(2010, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            String message = "Invalid XML file courses.xml :";
            assertEquals(true, e.getMessage().startsWith(message));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // schedule with missing end date
            db.loadSemester(2010, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            String message = "Invalid XML file scheduleB.xml :";
            assertEquals(true, e.getMessage().startsWith(message));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // constraint with unknown element
            db.loadSemester(2011, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            String message = "Invalid XML file constraintsA.xml :";
            assertEquals(true, e.getMessage().startsWith(message));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // course with duplicate element
            db.loadSemester(2011, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            String message = "Invalid XML file courses.xml :";
            assertEquals(true, e.getMessage().startsWith(message));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }

    @Test
    public void loadBadSemesterTest() {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        try {
            // Dupplicate study programs
            db.loadSemester(2012, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            String message = "Duplicate study program in database: ";
            assertEquals(true, e.getMessage().startsWith(message));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Dupplicate course IDs
            db.loadSemester(2012, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            String message = "Duplicate course in database: ";
            assertEquals(true, e.getMessage().startsWith(message));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Course with unknown study program
            db.loadSemester(2013, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            String message = "Course 'test' contains unknown program study: ";
            assertEquals(true, e.getMessage().startsWith(message));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Bad date
            db.loadSemester(2013, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Schedule 'A' contains invalid date : '2017'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // End date before start date
            db.loadSemester(2014, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Schedule 'A' end date is before start date", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Schedule without end date
            db.loadSemester(2014, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Start/End date missing in schedule A", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Invalid exam hour
            db.loadSemester(2015, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Schedule 'A' contains invalid hour : '1300'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Unknown course in schedule
            db.loadSemester(2015, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Schedule 'A' contain unknown course : '123456'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Invalid exam hour
            db.loadSemester(2016, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Course '104166' has invalid schedule date : '2017-03-03' in schedule 'A'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Invalid exam hour
            db.loadSemester(2016, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Course '104031' scheduled to an already taken date : '2017-01-03 09:00' in schedule 'A'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }
}
