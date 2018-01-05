/*package Logic;

import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CourseTest {
    private static Course c0 = new Course("yearly project",234311,true,3);
    private static Course c1 = new Course("OS",234123,true,4.5);
    private static Course c2 = new Course("ALGO",234247,true,3);
    private static Course c3 = new Course("OOP",236703,true,3);
    private static Course c4 = new Course("Software Design",236700,false,3);

    @Test
    public void simpleTest(){
        assertEquals(c0.isFirst(),false);
        assertEquals(c0.isLast(),false);
        assertEquals(c0.getDaysBefore(),3);
        assertEquals(c0.getConstraints().size(),0);
        assertEquals(c0.getNumOfConflictCourses(),0);
    }

    @Test
    public void conflictCourseAddedOnlyOnce(){
        c0.addConflictCourse(c1.getCourseID(),c1.getCourseName());
        c0.addConflictCourse(c1.getCourseID(),c1.getCourseName());
        assertEquals(1,c0.getNumOfConflictCourses());
    }

    @Test
    public void addingListWithDuplicationsAddsEachCourseOnlyOnce(){
        List<Pair<Integer,String>> listWithDuplications = new ArrayList<>();
        listWithDuplications.add(new Pair<>(c1.getCourseID(),c1.getCourseName()));
        listWithDuplications.add(new Pair<>(c2.getCourseID(),c2.getCourseName()));
        listWithDuplications.add(new Pair<>(c1.getCourseID(),c1.getCourseName()));
        listWithDuplications.add(new Pair<>(c2.getCourseID(),c2.getCourseName()));
        listWithDuplications.add(new Pair<>(c1.getCourseID(),c1.getCourseName()));
        c0.addConflictCourses(listWithDuplications);
        assertEquals(2,c0.getNumOfConflictCourses());
    }

    @Test
    public void removingUnexistingCourseDoesNotEffectTheCoursesList(){
        c1.addConflictCourse(c0.getCourseID(),c0.getCourseName());
        c1.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        c1.removeConflictCourse(123);
        assertEquals(2,c1.getNumOfConflictCourses());
        c1.removeConflictCourse(c1.getCourseID());
        assertEquals(2,c1.getNumOfConflictCourses());
    }

    @Test
    public void removeExistingCourseSucceeds(){
        c1.addConflictCourse(c0.getCourseID(),c0.getCourseName());
        c1.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        c1.removeConflictCourse(c0.getCourseID());
        assertEquals(false,c1.getConflictCourses().containsKey(c0.getCourseID()));
    }

    @Test
    public void courseConflictsMapContainsAllCoursesThatDefinedAsConflicts(){
        c0.addConflictCourse(c1.getCourseID(),c1.getCourseName());
        c0.addConflictCourse(c2.getCourseID(),c2.getCourseName());
        c0.addConflictCourse(c3.getCourseID(),c3.getCourseName());
        Map<Integer,String> m = c0.getConflictCourses();
        assertEquals(true,m.containsKey(c1.getCourseID()));
        assertEquals(true,m.containsKey(c2.getCourseID()));
        assertEquals(true,m.containsKey(c3.getCourseID()));
    }

    @Test
    public void tryingToSetDaysBeforeWithNegativeValueFails(){
        c0.setDaysBefore(-1);
        assertEquals(3,c0.getDaysBefore());
    }

    @Test
    public void requiredCourseConsideredBiggerThanUnRequiredOne(){
        assertEquals(-1,c0.compareTo(c4));
    }

    @Test
    public void requiredCoursesWithNoConflictsWithDifferentDaysBeforeConsideredDifferent(){
        assertEquals(-1,c1.compareTo(c0));
    }
}*/