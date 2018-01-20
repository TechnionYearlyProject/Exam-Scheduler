package Logic;
import db.Constraint;
import java.time.LocalDate;
import java.util.*;

/**
 * @author dorbartov
 * @date 05/12/2018
 * The class represents an exam day. each course assigned here will have a matching integer. 0 if that is the day the
 * test will be held. negative int if the day is within the range of days assigned to study for the course. positive
 * int if the course has been scheduled earlier.
 */
public class Day {
    LocalDate date;
    HashMap<Integer,Integer> courses;
    public Day(LocalDate new_date) {
        date = new_date;
        courses = new HashMap<>();
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

    /**
     * @author Moisei Vainbaum
     * @date 15/12/2017
     Returns true if the course can be assigned to the day without conflict collisions
    * */
    public boolean canBeAssigned (Course course){
        for (int course_id: course.getConflictCourses().keySet()){
            Integer distance = this.getDistance(course_id);
            if (distance == null){
                continue;
            }
            if (distance <= 0 || course.getDaysBefore() >= distance){ //A student has not time to prepare to any of two courses
                return false;
            }
        }
        for (Constraint constraint: course.getConstraints()){//was getBadConstraints()
            if (constraint.date.equals(getDate())){
                return false;
            }
        }
        return true;
    }
    public void deleteCourse(int courseId){
        courses.remove(courseId);
    }
}
