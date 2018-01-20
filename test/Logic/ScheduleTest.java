package Logic;

import db.Constraint;
import db.ConstraintList;
import db.Database;
import db.Semester;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**@author mvainbau
 * @date 07/01/2018*/
public class ScheduleTest {
    Database db;
    CourseLoader loaderA;
    CourseLoader loaderB;
    Semester semester;
    Schedule moedA;
    Schedule moedB;

    final static int EXAM_DAYS_MOED_A = 22;
    final static int EXAM_DAYS_MOED_B = 16;
    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.loadSemester(2017, "winter_test");
        semester = db.getSemester(2017, "winter_test");
        ConstraintList constraintList = new ConstraintList();
        constraintList.constraints = semester.getConstraintLists(Semester.Moed.MOED_A);
        loaderA = new CourseLoader(semester, constraintList);
        moedA = new Schedule(LocalDate.of(2018, 1, 29), LocalDate.of(2018, 2, 22), null);
        HashSet<LocalDate> occupied = new HashSet<>();
        occupied.add(LocalDate.of(2018, 3, 1));
        occupied.add(LocalDate.of(2018, 3, 2));
        constraintList.constraints = semester.getConstraintLists(Semester.Moed.MOED_B);
        loaderB = new CourseLoader(semester, constraintList);
        moedB = new Schedule(LocalDate.of(2018, 2, 27), LocalDate.of(2018, 3, 19), occupied,  20);
    }

    @Test
    public void getSize() throws Exception {
        assert(moedA.getSize() == EXAM_DAYS_MOED_A);
        assert(moedB.getSize() == EXAM_DAYS_MOED_B);
    }

    @Test
    public void getSchedulableDays() throws Exception {
        ArrayList<Day> days = moedA.getSchedulableDays();
        assert(days.size() == EXAM_DAYS_MOED_A);
        LocalDate date = LocalDate.of(2018, 1, 29);
        while(!(date.equals(LocalDate.of(2018, 2, 23)))){
            if(date.getDayOfWeek()== DayOfWeek.SATURDAY){
                for (Day day: days){
                    assert(!(day.getDate().equals(date)));
                }
            } else {
                boolean contains = false;
                for (Day day: days){
                    if(day.getDate().equals(date)){
                        contains = true;
                        break;
                    }
                }
                assert(contains);
            }
            date = date.plusDays(1);
        }
    }

    @Test
    public void assignCourse() throws Exception {
        for (Course course: loaderA.getSortedCourses()){
            moedA.assignCourse(course, course.getCourseID() % EXAM_DAYS_MOED_A);
        }
        for (Course course: loaderA.getSortedCourses()){
            int counter = -course.getDaysBefore();
            int index = counter + (course.getCourseID() % EXAM_DAYS_MOED_A);
            if (index < 0){
                counter += (-index);
                index = 0;
            }
            while (index < EXAM_DAYS_MOED_A){
                assert(moedA.getSchedulableDays().get(index).getDistance(course.getCourseID()) == counter);
                index ++;
                counter++;
            }
        }
    }

    @Test
    public void produceSchedule() throws Exception { //this is test for legal schedule
        moedA.produceSchedule(loaderA, semester.constraints.get(Semester.Moed.MOED_A), null);
        for (Course course: loaderA.getSortedCourses()){
            assert(isCourseInSchedule(moedA, course.getCourseID()));
            if (findDateToScheduleConstraint(course.getConstraints()) == null){
                assert(isCourseConflictsRequirementsMet(moedA, course, true));
            } else {
                assert(courseAssignedToConstraintDate(moedA, course));
            }
        }
        moedB.produceSchedule(loaderB, semester.constraints.get(Semester.Moed.MOED_B), moedA);
        for (Course course: loaderB.getSortedCourses()){
            assert(isCourseInSchedule(moedB, course.getCourseID()));
            if (findDateToScheduleConstraint(course.getConstraints()) == null){
                assert(isCourseConflictsRequirementsMet(moedB, course, false));
            } else {
                assert(courseAssignedToConstraintDate(moedB, course));
            }

        }
        //Print schedule (just for interest)
        System.out.println("============ MOED A ============");
        printSchedule(moedA);
        System.out.println("============ MOED B ============");
        printSchedule(moedB);

    }

    @Test
    public void moveCourseTest(){
        Course algo = loaderA.getCourse(234247);
        Course os = loaderA.getCourse(234123);
        moedA.assignCourse(algo, 0);
        moedA.assignCourse(os, 10);
        LocalDate dateWhenOsScheduled = moedA.getDayWhenScheduled(234123).date;
        LocalDate dateWhenAlgoScheduled = moedA.getDayWhenScheduled(234247).date;
        assertFalse(moedA.isMovePossible(algo, dateWhenOsScheduled, loaderA));
        assertTrue(moedA.isMovePossible(os, dateWhenAlgoScheduled, loaderA));
        assert(moedA.isMovePossible(algo, dateWhenOsScheduled.plusDays(3), loaderA));
        assert(moedA.isMovePossible(os, dateWhenAlgoScheduled, loaderA));
        assertEquals(dateWhenOsScheduled.plusDays(3), moedA.getDayWhenScheduled(algo.getCourseID()).date);
        assertEquals(dateWhenAlgoScheduled, moedA.getDayWhenScheduled(os.getCourseID()).date);
    }

    @Test
    public void daysBetweenTest() {
        Course algo = loaderA.getCourse(234247);
        Course os = loaderA.getCourse(234123);
        moedA.assignCourse(algo, 0);
        moedA.assignCourse(os, 10);
        assertEquals(10, moedA.daysBetween(moedA.getDayWhenScheduled(algo.getCourseID()).date, moedA.getDayWhenScheduled(os.getCourseID()).date));
    }

    private boolean isCourseInSchedule (Schedule schedule, int courseId) {
        for (Day day: schedule.getSchedulableDays()){
            Integer distance = day.getDistance(courseId);
            if (distance == null){
                continue;
            }
            if (distance == 0){
                return (day.getDate().getDayOfWeek() != DayOfWeek.SATURDAY); //Exam must not be on Saturday (ofc, there is
                //no Saturdays in schedulable days. TODO: check if day is locked (how?)
            }
        }
        return false; //If got here- exam is not in schedule
    }

    private boolean isCourseConflictsRequirementsMet(Schedule schedule, Course course, boolean isMoedA){
        for (Day day: schedule.getSchedulableDays()){
            Integer distance = day.getDistance(course.getCourseID());
            if (distance == null || distance >= 0){ //As checks are symmetric,
                continue;
            } else {
                for (Integer conflictId: course.getConflictCourses().keySet()){
                    Integer conflictDistance = day.getDistance(conflictId);
                    //as we write negative distance only for days we need for preparation, it is iilegal to
                    //conflict courses to have negative distance in same day
                    if (conflictDistance != null && (conflictDistance <= 0 || (isMoedA && conflictDistance - distance < course.getDaysBefore()))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void printSchedule(Schedule schedule){
        for(Day day: schedule.getSchedulableDays()){
            System.out.println("============  Day " + day.getDate().toString());
            for (Integer courseId: day.courses.keySet()){
                if(day.courses.get(courseId) == 0) {
                    System.out.println(courseId);
                }
            }
        }
    }

    private LocalDate findDateToScheduleConstraint(List<Constraint> constraints){
        for (Constraint constraint: constraints){
            if (!constraint.forbidden){
                return constraint.date;
            }
        }
        return null;
    }

    private boolean courseAssignedToConstraintDate(Schedule schedule, Course course){
        LocalDate date = findDateToScheduleConstraint(course.getConstraints());
        if (date == null){
            return true;
        }
        return date.equals(schedule.getDayWhenScheduled(course.getCourseID()).getDate());
    }
}