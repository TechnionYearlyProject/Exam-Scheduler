package db_test;

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
        baseDir = db.baseDirectory.substring(0, db.baseDirectory.length() - 2) + "test" + db.sep + "db_test";
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
            // course with missing courseName
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
            //fail("Unexpected exception: " + e.toString());
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
            // Unknown course in schedule
            db.loadSemester(2015, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Schedule 'A' contain unknown course : '123456'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Invalid constraint dates
            db.loadSemester(2016, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Course '104031' has invalid constraint date : '2017-02-01/2017-01-03' in schedule 'A'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Overlapping constraint dates
            db.loadSemester(2001, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Course '104031' has overlapping constraint : '2017-01-02 - 2017-01-07' in schedule 'A'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Constraints on schedule without start/end dates
            db.loadSemester(2001, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Start/End date missing in schedule B", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Unknown course in constraints
            db.loadSemester(2002, "winter");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Constraint List 'A' contain unknown course : '123456'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
        try {
            // Constraint dates out of schedule dates
            db.loadSemester(2002, "spring");
            fail("Should have thrown InvalidDatabase exception");
        } catch (InvalidDatabase e) {
            assertEquals("Course '123456' constraint is out of the schedule dates : '2017-01-04/2017-03-07' in schedule 'A'", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.toString());
        }
    }
}
