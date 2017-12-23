package Logic;
import java.time.LocalDate;
import java.util.*;

public class Day {
    LocalDate date;
    HashMap<Integer,Integer> courses;
    public Day(LocalDate new_date) {
        date = new_date;
        courses = new HashMap<Integer,Integer>();
    }
    public void insertCourse(int course_id, int distance) {
        courses.put(course_id,distance);
    }
    public LocalDate getDate() {
        return date;
    }
}
