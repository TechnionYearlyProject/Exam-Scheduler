package Logic;
import Logic.Exceptions.IllegalRange;
import db.ConstraintList;
import db.Database;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Schedule {
    ArrayList<Day> schedulable_days;
    public Schedule(LocalDate begin, LocalDate end, HashSet<LocalDate> occupied) throws IllegalRange {
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
        int dist = -1;
        int days_before = -1*course.getDaysBefore();
        while (dist>=days_before) {
            (schedulable_days.get(index+dist)).insertCourse(course_id,dist);
            dist--;
        }
        dist=1;
        while (index+dist<schedulable_days.size()) {
            (schedulable_days.get(index+dist)).insertCourse(course_id,dist);
            dist++;
        }
    }

    public void produceSchedule(Database db, ConstraintList cl){
        CourseLoader loader = new CourseLoader(db, cl);
        //sort courses by number of conflicts
        List<Course> courses = loader.getCourses().values().stream().sorted((course1, course2)->
            course1.getNumOfConflictCourses() - course2.getNumOfConflictCourses()).collect(Collectors.toList());
        //try to schedule courses in such way, that every day will be ~ same number of exams (is it true to do it?)
        int uniformity = courses.size() / schedulable_days.size();
        for (Course course: courses){
            Set<Integer> course_conflicts = course.getConflictCourses().keySet();
            for (int i = 0; i < schedulable_days.size(); i++){
                Day day = schedulable_days.get(i);
                if (day.getNumOfCourses() > uniformity){
                    continue; //TODO: if in the end there is possibility to schedule only to this day, we should shut our eyes on uniformity
                }
                boolean can_schedule = true;
                for (int course_id: course_conflicts){
                    Integer distance = day.getDistance(course_id);
                    if (distance == null){
                        continue;
                    }
                    if (distance <= 0 || course.getDaysBefore() > distance){ //can't prepare to any of two courses
                        can_schedule = false;
                        break;
                    }
                }
                if (can_schedule){
                    assignCourse(course, i);
                }
            }
        }
    }
}
