package test.java.db;

import db.Course;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CourseTest {

    public static Course c;

    @Before
    public void initCourse() {
        c = new Course(123456, "abcdef");
    }

    @Test
    public void setStudyProgramTest() {
        c.setStudyProgram("abc", 1);
        c.setStudyProgram("def", 1);
        assertEquals(1, c.getStudyProgramSemester("abc"));
        assertEquals(1, c.getStudyProgramSemester("def"));
        assertEquals(0, c.getStudyProgramSemester("abcdef"));
    }
}