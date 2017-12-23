package Logic;
import Logic.Exceptions.IllegalRange;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;

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
}
