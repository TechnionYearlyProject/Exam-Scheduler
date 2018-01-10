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

public class Schedule {
    ArrayList<Day> schedulable_days;
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

    public void produceSchedule(Semester semester, ConstraintList cl) throws CanNotBeScheduledException{
        CourseLoader loader = new CourseLoader(semester, cl);
        //sort courses by number of conflicts
        List<Course> courses = loader.getSortedCourses();
        //try to schedule courses in such way, that every day will be ~ same number of exams (is it true to do it?)
        for (Course course: courses){
            boolean scheduled = false;
            int uniformity = 1;
            while (!scheduled){
                try {
                    scheduleExamFor(course, uniformity);
                    scheduled = true;
                } catch (CanNotBeScheduledException e){
                    uniformity++;
                }
            }
        }
    }

    private void scheduleExamFor(Course course, Integer uniformity) throws CanNotBeScheduledException{
        Set<Integer> course_conflicts = course.getConflictCourses().keySet();
        boolean scheduled = false;
        for (int i = 0; i < schedulable_days.size(); i++){
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
                if (distance <= 0 || course.getDaysBefore() >= distance){ //can't prepare to any of two courses
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
}
