package db_test;

import db.Database;
import db.exception.*;
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

    @Test(expected = SemesterNotFound.class)
    public void EmptyDatabase() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "empty_db";
        db.loadSemester(2017, "winter");
    }

    @Test(expected = SemesterNotFound.class)
    public void NoYearSemester() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2010, "winter");
    }

    @Test(expected = SemesterNotFound.class)
    public void NoSemester() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2010, "missing_conflicts");
    }

    @Test(expected = SemesterNotFound.class)
    public void NoYear() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "winter");
    }

    @Test(expected = SemesterFileMissing.class)
    public void missingPrograms() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "missing_programs");
    }

    @Test(expected = SemesterFileMissing.class)
    public void missingCourses() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "missing_courses");
    }

    @Test(expected = SemesterFileMissing.class)
    public void missingConflicts() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "missing_conflicts");
    }

    @Test(expected = SemesterFileMissing.class)
    public void missingScheduleA() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "missing_schedule_a");
    }

    @Test(expected = SemesterFileMissing.class)
    public void missingScheduleB() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "missing_schedule_b");
    }

    @Test(expected = SemesterFileMissing.class)
    public void missingConstraintsA() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "missing_constraints_a");
    }

    @Test(expected = SemesterFileMissing.class)
    public void missingConstraintsB() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "missing_files_db";
        db.loadSemester(2017, "missing_constraints_b");
    }

    @Test(expected = InvalidXMLFile.class)
    public void invalidXMLfile() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "invalid_xml");
    }

    @Test(expected = StudyProgramAlreadyExist.class)
    public void duplicatePrograms() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "duplicate_programs");
    }

    @Test(expected = CourseAlreadyExist.class)
    public void duplicateCourseId() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "duplicate_course_id");
    }

    @Test(expected = StudyProgramUnknown.class)
    public void unknownProgram() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "unknown_program");
    }

    @Test(expected = CourseFirstAndLast.class)
    public void courseFirstAndLast() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "course_first_and_last");
    }

    @Test(expected = InvalidSchedule.class)
    public void badScheduleDate() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "bad_schedule_date");
    }

    @Test(expected = InvalidSchedule.class)
    public void scheduleEndBeforeStart() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "schedule_end_before_start");
    }

    @Test(expected = UninitializedSchedule.class)
    public void missingScheduleDate() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "missing_schedule_date");
    }

    @Test(expected = CourseUnknown.class)
    public void courseUnknownInSchedule() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "course_unknown_in_schedule");
    }

    @Test(expected = DateOutOfSchedule.class)
    public void examOutOfSchedule() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "exam_out_of_schedule");
    }

    @Test(expected = DuplicateConstraints.class)
    public void duplicateConstraints() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "duplicate_constraints");
    }

    @Test(expected = UninitializedSchedule.class)
    public void constraintsWithoutSchedule() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "constraints_without_schedule");
    }

    @Test(expected = CourseUnknown.class)
    public void courseUnknownInConstraint() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "course_unknown_in_constraint");
    }

    @Test(expected = DateOutOfSchedule.class)
    public void constraintOutOfSchedule() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "constraint_out_of_schedule");
    }

    @Test(expected = CourseUnknown.class)
    public void courseUnknownInConflicts() throws SemesterNotFound, InvalidDatabase, SemesterFileMissing {
        db.baseDirectory = baseDir + db.sep + "invalid_db";
        db.loadSemester(2014, "course_unknown_in_conflicts");
    }
}
