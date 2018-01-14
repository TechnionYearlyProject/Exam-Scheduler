package Logic;

import Logic.Exceptions.IllegalDaysBefore;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import db.Constraint;
import db.Database;
import db.Semester;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
//import sun.plugin.javascript.navig.Array;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CourseTest {
    Database db;
    Semester semester;
    Course c1,c2,c3,c4,c5;
    Calendar ca1,ca2,ca3,ca4,ca5,ca6,ca7,ca8;

    @Before
    public void setUp() throws Exception {
        c1= new Course("OS",234123,true,4.5);
        c2 = new Course("ALGO", 234247,true,3.0);
        c3 = new Course ("DS",234218,true,3.0);
        c4 = new Course("MAMAN",236363,false,3.0);
        c5 = new Course("Yearly project stage A",234311,true,3.0);
        db = new Database();
        db.loadSemester(2017, "winter_test");
        semester = db.getSemester(2017, "winter_test");
    }

    private void setCalendar(){
        ca1 = new GregorianCalendar();
        ca1.set(2018,Calendar.JANUARY,14);
        ca2 = new GregorianCalendar();
        ca2.set(2018,Calendar.JANUARY,15);
        ca3 = new GregorianCalendar();
        ca3.set(2018,Calendar.JANUARY,17);
        ca4 = new GregorianCalendar();
        ca4.set(2018,Calendar.JANUARY,20);
        ca5 = new GregorianCalendar();
        ca5.set(2018,Calendar.FEBRUARY,2);
        ca6 = new GregorianCalendar();
        ca6.set(2018,Calendar.FEBRUARY,3);
        ca7 = new GregorianCalendar();
        ca7.set(2018,Calendar.JANUARY,2);
        ca8 = new GregorianCalendar();
        ca8.set(2018,Calendar.JANUARY,5);
    }

    @Test
    public void addConstraint() throws Exception {
//        setCalendar();
//        Constraint con = new Constraint(ca1,ca2);
//        assertEquals(0,c1.getConstraints().size());
//        c1.addConstraint(con);
//        assertEquals(1,c1.getConstraints().size());
//        assertTrue(c1.getConstraints().contains(con));
    }

    @Test
    public void addConstraint1() throws Exception {
//        setCalendar();
//        Constraint con1,con2,con3,con4;
//        ArrayList<Constraint> aL = new ArrayList<>();
//        aL.add(con2 = new Constraint(ca3,ca4));
//        aL.add(con3 = new Constraint(ca5,ca6));
//        aL.add(new Constraint(ca7,ca8));
//        aL.add(con4 = new Constraint(ca7,ca8));
//        aL.add(new Constraint(ca1,ca2));
//        aL.add(con1 = new Constraint(ca1,ca2));
//        aL.add(new Constraint(ca1,ca2));
//        c1.addConstraint(aL);
//        assertEquals(4,c1.getConstraints().size());
//        assertTrue(c1.getConstraints().contains(con1));
//        assertTrue(c1.getConstraints().contains(con2));
//        assertTrue(c1.getConstraints().contains(con3));
//        assertTrue(c1.getConstraints().contains(con4));
    }

    @Test
    public void removeConstraint() throws Exception {
//        setCalendar();
//        Constraint con = new Constraint(ca1,ca2);
//        Constraint notCon = new Constraint(ca3,ca4);
//        c1.addConstraint(con);
//        c1.removeConstraint(notCon);
//        assertEquals(1,c1.getConstraints().size());
//        c1.removeConstraint(con);
//        assertEquals(0,c1.getConstraints().size());
    }

    @Test
    public void addConflictCourse() throws Exception {
        c1.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        assertEquals(1,c1.getNumOfConflictCourses());
        c1.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        assertEquals(1,c1.getNumOfConflictCourses());
        c1.addConflictCourse(c1.getCourseID(),c1.getCourseName());
        assertEquals(1,c1.getNumOfConflictCourses());
    }

    @Test
    public void verifyCreditPoints(){
        assertTrue(4==c1.getDaysBefore() + 1);
    }

    @Test
    public void removeConflictCourse() throws Exception {
        c1.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        c1.removeConflictCourse(c3.getCourseID());
        assertEquals(1,c1.getNumOfConflictCourses());
        c1.removeConflictCourse(c2.getCourseID());
        assertEquals(0,c1.getNumOfConflictCourses());
    }

    @Test
    public void addConflictCourses() throws Exception {
        ArrayList<Course> lst = new ArrayList<>();
        lst.add(c2);
        lst.add(c2);
        lst.add(c3);
        lst.add(c4);
        lst.add(c3);
        List<Pair<Integer,String>> iLst = lst.stream().
                map(a->new Pair<Integer,String>(a.getCourseID(),a.getCourseName())).collect(Collectors.toList());
        c1.addConflictCourses(iLst);
        assertEquals(3,c1.getNumOfConflictCourses());
        assertEquals(true,c1.getConflictCourses().containsKey(c2.getCourseID()));
        assertEquals(true,c1.getConflictCourses().containsKey(c3.getCourseID()));
        assertEquals(true,c1.getConflictCourses().containsKey(c4.getCourseID()));
    }

    @Test
    public void getCourseID() throws Exception {
        int id = 234247;
        assertTrue(id == c2.getCourseID());
    }

    @Test
    public void getCourseName() throws Exception {
        String st = "ALGO";
        assertEquals(st,c2.getCourseName());
    }

    @Test
    public void getHasTest(){
        assertTrue(c1.hasExam());
        c1.setHasExam(false);
        assertFalse(c1.hasExam());
    }

    @Test
    public void setDaysBefore() throws Exception {
        c1.setDaysBefore(2);
        assertTrue(2 == c1.getDaysBefore());
    }

    @Test(expected = IllegalDaysBefore.class)
    public void negativeDaysBeforeThrowsException() throws Exception{
        c1.setDaysBefore(-2);
        assertTrue(4 == c1.getDaysBefore());
    }

    @Test
    public void getDaysBefore() throws Exception {
        assertTrue( 3 == c1.getDaysBefore());
    }

    @Test
    public void isLast() throws Exception {
        c1.setAsLast(true);
        assertTrue(c1.isLast());
    }

    @Test
    public void isFirst() throws Exception {
        c2.setAsFirst(true);
        assertTrue(c2.isFirst());
    }

    @Test
    public void isRequired() throws Exception {
        c3.setAsRequired(true);
        assertTrue(c3.isRequired());
        assertTrue(c1.isRequired());
        assertFalse(c4.isRequired());
    }

    @Test
    public void getConflictCourses() throws Exception {
        assertEquals(0,c1.getConflictCourses().size());
        ArrayList<Course> lst = new ArrayList<>();
        lst.add(c2);
        lst.add(c4);
        lst.add(c3);
        List<Pair<Integer,String>> iLst = lst.stream().
                map(a->new Pair<Integer,String>(a.getCourseID(),a.getCourseName())).collect(Collectors.toList());
        c1.addConflictCourses(iLst);
        Map<Integer,String> m = c1.getConflictCourses();
        assertTrue(m.containsKey(c2.getCourseID()));
        assertTrue(m.containsKey(c4.getCourseID()));
        assertTrue(m.containsKey(c3.getCourseID()));
    }

    @Test
    public void getConstraints() throws Exception {
    }

    @Test
    public void getNumOfConflictCourses() throws Exception {
        assertEquals(0,c1.getNumOfConflictCourses());
    }

    @Test
    public void equals() throws Exception {
        assertFalse(c1.equals(null));
        assertFalse(c1.equals(c2));
        Course t = new Course("s",c1.getCourseID(),true,2.5);
        assertTrue(c1.equals(t));
    }

    @Test
    public void compareTo() throws Exception {
        assertEquals(-1, c1.compareTo(c2));
        assertEquals(1,c2.compareTo(c1));
        assertEquals(0,c2.compareTo(c3));
        c2.addConflictCourse(c3.getCourseID(),c3.getCourseName());
        assertEquals(1,c1.compareTo(c2));
        assertEquals(-1,c2.compareTo(c1));
        c3.setAsRequired(false);
        assertEquals(-1,c1.compareTo(c3));
        assertEquals(1,c3.compareTo(c1));
        c3.setAsRequired(false);
        assertEquals(0,c3.compareTo(c4));
        c3.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        assertEquals(-1,c3.compareTo(c4));
        assertEquals(1,c4.compareTo(c3));
    }

    @Test
    public void testSort() throws Exception{
        Course tmp =new Course("s",1,false,3.0);
        tmp.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        c1.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        c2.addConflictCourse(c3.getCourseID(),c3.getCourseName());
        ArrayList<Course> a = new ArrayList<>();
        a.add(tmp);
        a.add(c2);
        a.add(c4);
        a.add(c1);
        a.add(c3);

        Collections.sort(a);
        assertEquals(c1,a.get(0));
        assertEquals(c2,a.get(1));
        assertEquals(c3,a.get(2));
        assertEquals(tmp,a.get(3));
        assertEquals(c4,a.get(4));
    }
}