package Logic;

import db.Constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course implements Comparable<Course>{
    private int courseID;
    private String courseName;
    private Map<Integer,String> conflictCourses;
    private boolean isLast, isFirst, isRequired;
    private int daysBefore;
    //private double points;
    private ArrayList<Constraint> constraints;

    /*
     * Logic.Course copy c'tor performing deepCopy.
     */
    public Course(Course o) {
        courseID = o.courseID;
        courseName = o.courseName;
        isRequired = o.isRequired;
        isLast = o.isLast;
        isFirst = o.isFirst;
        daysBefore = o.daysBefore;
        constraints = new ArrayList<>(o.getConstraints());
        conflictCourses = new HashMap<>(o.getConflictCourses());
    }

    Course(String courseName, int courseID, boolean isRequired){
        conflictCourses = new HashMap<>();
        constraints = new ArrayList<>();
        this.courseID = courseID;
        this.courseName = courseName;
        this.isLast = false;
        this.isFirst = false;
        this.isRequired = isRequired;
        this.daysBefore = 4;//TODO: what is the real value?
    }

    void addConstraint(Constraint c){
        if(constraints.contains(c)){
            return;
        }
        constraints.add(new Constraint(c.start,c.end));
    }

    void addConstraint(List<Constraint> ls){
        for (Constraint c: ls) {
            addConstraint(c);
        }
    }

    void removeConstraint(Constraint c){
        constraints.remove(c);
    }

    void addConflictCourse(int courseID, String courseName){
        if(this.courseID == courseID){
            return;
        }
        if(conflictCourses.containsKey(courseID)){
            return;
        }
        conflictCourses.put(courseID,courseName);
    }

    void removeConflictCourse(int c){
        conflictCourses.remove(c);
    }

    void addConflictCourses(List<db.Course> courses){
        for (db.Course c: courses) {
            addConflictCourse(c.id,c.name);
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

    void setAsLast(boolean isLast){
        this.isLast = isLast;
    }
    boolean isLast(){
        return isLast;
    }

    void setAsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
    boolean isFirst(){
        return isFirst;
    }

    void setAsRequired(boolean isRequired){this.isRequired = isRequired;}
    boolean isRequired(){return isRequired;}

    Map<Integer,String> getConflictCourses(){
       return new HashMap<>(conflictCourses);

    }

    ArrayList<Constraint> getConstraints(){
        ArrayList<Constraint> cconstraints = new ArrayList<>();
        cconstraints.addAll(this.constraints);
        return cconstraints;
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

    @Override
    public int compareTo(Course o) {
        return 0;
    }
}
