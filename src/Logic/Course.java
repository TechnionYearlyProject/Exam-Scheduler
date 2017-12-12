package Logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Course {
    private int courseID;
    private String courseName;
    private ArrayList<Course> conflictCourses;
    private boolean isLast, isFirst, isRequired;//TODO: maybe change to other name
    private int daysBefore;
//    private ArrayList<Constraint> constraints;
//    static class Constraint {
//        Calendar start;
//        Calendar end;
//
//        public Constraint(Calendar start, Calendar end) {
//            this.start = (Calendar) start.clone();
//            this.end = (Calendar) end.clone();
//        }
//    }

    static void setCoursesAsConflicts(Course c1, Course c2){
        c1.addConflictCourse(c2);
        c2.addConflictCourse(c1);
    }

    Course(String courseName,int courseID, boolean isLast, boolean isFirst, int daysBefore, boolean isRequired) {
        conflictCourses = new ArrayList<>();
        this.courseID = courseID;
        this.courseName = courseName;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.daysBefore = daysBefore;
        this.isRequired = isRequired;
    }

    Course(String courseName, int courseID, boolean isRequired){
        conflictCourses = new ArrayList<>();
        this.courseID = courseID;
        this.courseName = courseName;
        this.isLast = false;
        this.isFirst = false;
        this.isRequired = isRequired;
        this.daysBefore = 4;
    }

    void addConflictCourse(Course c){
        if(conflictCourses.contains(c)){
            return;
        }
        conflictCourses.add(c);
    }

    void removeConflictCourse(Course c){
        conflictCourses.remove(c);
    }

    void addConflictCourses(List<Course> courses){
        for (Course c:courses) {
            addConflictCourse(c);
        }
    }

    public int getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }
    int getDaysBefore(){
        return daysBefore;
    }

    void setCourseAsLast(){
        this.isLast = true;
    }
    boolean isLast(){
        return isLast;
    }

    void setCourseAsFirst(){
        this.isFirst = true;
    }
    boolean isFirst(){
        return isFirst;
    }

    void setCourseAsRequired(){this.isRequired = true;}
    boolean isRequired(){return isRequired;}

    ArrayList<Course> getConflictCourses(){
        return conflictCourses;
    }
    int getNumOfConflictCourses(){
        return conflictCourses.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return this == o || (o instanceof Course && courseID == ((Course)o).courseID);
    }
}
