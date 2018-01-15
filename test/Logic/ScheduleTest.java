package Logic;

import db.Database;
import db.Semester;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ScheduleTest {
    Database db;
    CourseLoader loader;
    Semester semester;
    Schedule moedA;
    Schedule moedB;

    final static int EXAM_DAYS_MOED_A = 23;
    final static int EXAM_DAYS_MOED_B = 16;
    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.loadSemester(2017, "winter_test");
        semester = db.getSemester(2017, "winter_test");
        loader = new CourseLoader(semester, null);
        moedA = new Schedule(LocalDate.of(2018, 1, 29), LocalDate.of(2018, 2, 23), null);
        HashSet<LocalDate> occupied = new HashSet<>();
        occupied.add(LocalDate.of(2018, 3, 1));
        occupied.add(LocalDate.of(2018, 3, 2));
        moedB = new Schedule(LocalDate.of(2018, 2, 27), LocalDate.of(2018, 3, 19), occupied, 18);
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
        for (Course course: loader.getSortedCourses()){
            moedA.assignCourse(course, course.getCourseID() % EXAM_DAYS_MOED_A);
        }
        for (Course course: loader.getSortedCourses()){
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
        moedA.produceSchedule(loader, semester.constraints.get(Semester.Moed.MOED_A), null);
        for (Course course: loader.getSortedCourses()){
            assert(isCourseInSchedule(moedA, course.getCourseID()));
            assert(isCourseConflictsRequirementsMet(moedA, course, true));
        }
        moedB.produceSchedule(loader, semester.constraints.get(Semester.Moed.MOED_B), moedA);
        for (Course course: loader.getSortedCourses()){
            assert(isCourseInSchedule(moedB, course.getCourseID()));
            assert(isCourseConflictsRequirementsMet(moedB, course, false));
        }
        //Print schedule (just for interest)
        System.out.println("============ MOED A ============");
        printSchedule(moedA);
        System.out.println("============ MOED B ============");
        printSchedule(moedB);

    }

    @Test
    public void produceScheduleWithIllegalData() throws Exception {
        //TODO: assert that producing schedule with illegal data throws exception
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
}