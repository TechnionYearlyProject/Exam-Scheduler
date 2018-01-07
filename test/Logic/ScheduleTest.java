package Logic;

import db.Database;
import db.Semester;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ScheduleTest {
    Database db;
    CourseLoader loader;
    Semester semester;
    Schedule schedule;

    final static int EXAMS_DAYS = 33;
    @Before
    public void setUp() throws Exception {
        db = new Database();
        db.loadSemester(2017, "winter_test");
        semester = db.getSemester(2017, "winter_test");
        loader = new CourseLoader(db, null);
        schedule = new Schedule(LocalDate.of(2018, 2, 2), LocalDate.of(2018, 3, 12), null);
    }

    @Test
    public void getSize() throws Exception {
        System.out.println(schedule.getSize());
        assert(schedule.getSize() == EXAMS_DAYS);
    }

    @Test
    public void getSchedulableDays() throws Exception {
        ArrayList<Day> days = schedule.getSchedulableDays();
        assert(days.size() == EXAMS_DAYS);
        LocalDate date = LocalDate.of(2018, 2, 2);
        while(!(date.equals(LocalDate.of(2018, 3, 13)))){
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
            schedule.assignCourse(course, course.getCourseID() % EXAMS_DAYS);
        }
        for (Course course: loader.getSortedCourses()){
            int counter = -course.getDaysBefore();
            int index = counter + (course.getCourseID() % EXAMS_DAYS);
            if (index < 0){
                counter += index;
                index = 0;
            }
            while (index < EXAMS_DAYS){
                assert(schedule.getSchedulableDays().get(index).getDistance(course.getCourseID()) == counter);
                index ++;
                counter++;
            }
        }
    }

    @Test
    public void produceSchedule() throws Exception {
        schedule.produceSchedule(db, null);
        //TODO: Add legality checks
    }

}