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
    public Integer getDistance(int course_id) {
        return courses.get(course_id);
    }
    public int getNumOfCourses(){
        int size = 0;
        for (Integer distance: courses.values()){
            if (distance == 0){ //we are interested only in exams that are in this particular day
                size++;
            }
        }
        return size;
    }

    public ArrayList<Integer> getCoursesScheduledToTheDay(){
        ArrayList<Integer> coursesScheduledToTheDay = new ArrayList<>();
        for (Integer courseId: courses.keySet()){
            if(courses.get(courseId) == 0){
                coursesScheduledToTheDay.add(courseId);
            }
        }
        return coursesScheduledToTheDay;
    }
}
