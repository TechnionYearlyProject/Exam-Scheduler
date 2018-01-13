package Logic;
import Logic.Exceptions.IllegalRange;
import db.ConstraintList;
import db.Database;
import db.Semester;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;

public class Schedule {
    ArrayList<Day> schedulable_days;
    private int gap; //This parameter is relevant only for moed B schedule, and shows how many days should be between exams on same course
    public class CanNotBeScheduledException extends Exception{};
    public Schedule(LocalDate begin, LocalDate end, HashSet<LocalDate> occupied) throws IllegalRange {
        if (occupied == null){
            occupied = new HashSet<LocalDate>();
        }
        if (!end.isAfter(begin))
            throw new IllegalRange();
        schedulable_days = new ArrayList<Day>();
        LocalDate curr=begin;
        while (!curr.isAfter(end)) {
            if (!(curr.getDayOfWeek()==DayOfWeek.SATURDAY) && !(occupied.contains(curr)))
                schedulable_days.add(new Day(curr));
            curr=curr.plusDays(1);
        }
    }

    public Schedule(LocalDate begin, LocalDate end, HashSet<LocalDate> occupied, int gap) throws IllegalRange {
        this(begin, end, occupied);
        this.gap = gap;

    }
    public int getSize() {
        return schedulable_days.size();
    }
    public ArrayList<Day> getSchedulableDays() {
        return schedulable_days;
    }
    public void assignCourse(Course course, int index) {
        int course_id = course.getCourseID();
        (schedulable_days.get(index)).insertCourse(course_id,0);
        int days_before = -1*course.getDaysBefore();
        index = index + days_before;
        if (index < 0){
            days_before += (-index);
            index = 0;
        }
        while (index < schedulable_days.size()){
            schedulable_days.get(index).insertCourse(course_id, days_before);
            days_before++;
            index++;
        }
    }
    public Day getDayWhenScheduled(int courseId){
        for (int i = 0; i < schedulable_days.size();){
            Integer distance = schedulable_days.get(i).getDistance(courseId);
            if ( distance == null){
                i++;
                continue;
            } else if (distance == 0) {
                return schedulable_days.get(i);
            } else {
                i += (-distance);
            }
        }
        return null;
    }

    public void produceSchedule(Semester semester, ConstraintList cl, Schedule moedA) throws CanNotBeScheduledException{
        CourseLoader loader = new CourseLoader(semester, cl);
        //First need to schedule courses with constraints
        //sort courses by number of conflicts
        List<Course> courses = loader.getSortedCourses();
        //try to schedule courses in such way, that every day will be ~ same number of exams (is it true to do it?)
        for (Course course: courses){
            boolean scheduled = false;
            int uniformity = 1;
            int beginFrom = getFirstIndexOfDayWhenCanBeScheduled(moedA, course.getCourseID());
            while (!scheduled){
                try {
                    scheduleExamFor(course, uniformity, beginFrom);
                    scheduled = true;
                } catch (CanNotBeScheduledException e){
                    uniformity++;
                }
                if(uniformity > courses.size()){ //Can't schedule anyway
                    throw new CanNotBeScheduledException();
                }
            }
        }
    }

    private void scheduleExamFor(Course course, Integer uniformity, int beginFrom) throws CanNotBeScheduledException{
        Set<Integer> course_conflicts = course.getConflictCourses().keySet();
        boolean scheduled = false;
        for (int i = beginFrom; i < schedulable_days.size(); i++){
            Day day = schedulable_days.get(i);
            if (uniformity != null && day.getNumOfCourses() > uniformity){
                continue;
            }
            boolean can_schedule = true;
            for (int course_id: course_conflicts){
                Integer distance = day.getDistance(course_id);
                if (distance == null){
                    continue;
                }
                if (distance <= 0 || course.getDaysBefore() >= distance){ //A student has not time to prepare to any of two courses
                    can_schedule = false;
                    break;
                }
            }
            if (can_schedule){
                assignCourse(course, i);
                scheduled = true;
                break;
            }
        }
        if (!scheduled){
            throw new CanNotBeScheduledException();
        }
    }

    //This function finds first index of day, when course can be scheduled
    //It is useful when we want to be sure that gap between exams in two moeds is sufficient
    //if moedA schedule isn't provided, returns 0
    private int getFirstIndexOfDayWhenCanBeScheduled(Schedule moedA, int courseId){
        if (moedA == null){
            return 0;
        }
        LocalDate moedAExamDate = moedA.getDayWhenScheduled(courseId).getDate();
        LocalDate moedBBeginDate = schedulable_days.get(0).getDate();
        int index = (int)DAYS.between(moedAExamDate.plusDays(gap), moedBBeginDate);
        return index < 0 ? 0: index;
    }
}
