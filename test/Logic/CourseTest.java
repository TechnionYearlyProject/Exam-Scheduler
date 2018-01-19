package Logic;

import Logic.Exceptions.IllegalDaysBefore;
import db.Constraint;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CourseTest {
    Course c1,c2,c3,c4,c5;
    LocalDate ca1,ca2,ca3,ca4,ca5,ca6,ca7,ca8;

    @Before
    public void setUp() throws Exception {
        c1= new Course("OS",234123,true,4.5);
        c2 = new Course("ALGO", 234247,true,3.0);
        c3 = new Course ("DS",234218,true,3.0);
        c4 = new Course("MAMAN",236363,false,3.0);
        c5 = new Course("Yearly project stage A",234311,true,3.0);
    }

    @Test
    public void compareTo() throws Exception {
        assertEquals(-1, c1.compareTo(c2));
        assertEquals(1,c2.compareTo(c1));
        assertEquals(0,c2.compareTo(c3));
        c2.setAsLast(true);
        assertEquals(-1,c5.compareTo(c2));
        c1.setAsLast(true);
        assertTrue(c1.compareTo(c2) == 0);
        c3.addConflictCourse(c4.getCourseID(),c4.getCourseName());
        assertEquals(-1,c3.compareTo(c1));
        c1.setAsLast(false);
        c2.setAsLast(false);
        c2.addConflictCourse(c5.getCourseID(),c5.getCourseName());
        assertEquals(1,c1.compareTo(c2));
        assertEquals(-1,c2.compareTo(c1));
        assertEquals(1,c4.compareTo(c5));

        assertEquals(1,c1.compareTo(c3));
        assertEquals(-1,c3.compareTo(c1));
        assertEquals(-1,c3.compareTo(c4));
        c1.setAsLast(true);
        assertEquals(1,c1.compareTo(c2));
        assertEquals(-1,c2.compareTo(c1));
    }

    private void setCalendar(){
        ca1 = LocalDate.of(2018,1,14);
        ca2 =LocalDate.of(2018,1,15);
        ca3 =LocalDate.of(2018,1,17);
        ca4 =LocalDate.of(2018,1,20);
        ca5 =LocalDate.of(2018,2,2);
        ca6 =LocalDate.of(2018,2,3);
        ca7 =LocalDate.of(2018,2,2);
    }

    @Test
    public void addConstraint() throws Exception {
        setCalendar();
        Constraint con = new Constraint(ca1);
        assertEquals(0,c1.getConstraints().size());
        c1.addConstraint(con);
        assertEquals(1,c1.getConstraints().size());
        assertTrue(c1.getConstraints().contains(con));
    }

    @Test
    public void copyCtorDeepCloning(){
        c1.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        c1.addConstraint(new Constraint(LocalDate.of(2018,2,2)));
        Course copied = new Course(c1);
        assertEquals(c1.isLast(),copied.isLast());
        assertEquals(c1.isFirst(),copied.isFirst());
        assertEquals(c1.hasExam(),copied.hasExam());
        assertEquals(c1.isRequired(),copied.isRequired());
        assertEquals(c1.getCreditPoints(),copied.getCreditPoints());
        assertEquals(c1.getCourseID(),copied.getCourseID());
        assertEquals(c1.getCourseName(),copied.getCourseName());
        assertFalse(c1.getConflictCourses() == copied.getConflictCourses());
        assertFalse(c1.getConstraints() == copied.getConstraints());
    }

    @Test
    public void addConstraint1() throws Exception {
        setCalendar();
        Constraint con1,con5,con3,con7;
        ArrayList<Constraint> aL = new ArrayList<>();
        aL.add(con3 = new Constraint(ca3));
        aL.add(con5 = new Constraint(ca5));
        aL.add(new Constraint(ca7));
        aL.add(con7 = new Constraint(ca7));
        aL.add(new Constraint(ca1));
        aL.add(con1 = new Constraint(ca1));
        aL.add(new Constraint(ca1));
        c1.addConstraint(aL);
        assertEquals(3,c1.getConstraints().size());
        assertTrue(c1.getConstraints().contains(con1));
        assertTrue(c1.getConstraints().contains(con3));
        assertTrue(c1.getConstraints().contains(con5));
        assertTrue(c1.getConstraints().contains(con7));
    }

    @Test
    public void removeConstraint() throws Exception {
        setCalendar();
        Constraint con = new Constraint(ca1);
        Constraint notCon = new Constraint(ca3);
        c1.addConstraint(con);
        c1.removeConstraint(notCon);
        assertEquals(1,c1.getConstraints().size());
        c1.removeConstraint(con);
        assertEquals(0,c1.getConstraints().size());
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
        assertTrue(3 == c1.getDaysBefore());
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