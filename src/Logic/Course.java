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

    /*
     * number of credit points will be added here..
     */
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

    /*
     * Adding new constraint to the constraints list.
     * @Param c: the Constraint to add.
     */
    void addConstraint(Constraint c){
        if(constraints.contains(c)){
            return;
        }
        constraints.add(new Constraint(c.start,c.end));
    }

    /*
     * Adding a list of Constraints. if one of the Constraints already exists, it won't be added again.
     * @Param ls: the list of constraints to add.
     */
    void addConstraint(List<Constraint> ls){
        for (Constraint c: ls) {
            addConstraint(c);
        }
    }

    /*
     * Remove constraint from the constraints list. if there is such constraint it won't affect the list.
     * @Param c: the Constraint to remove.
     */
    void removeConstraint(Constraint c){
        constraints.remove(c);
    }

    /*
     * defining new Conflict of the course. if the course already defined as conflicted, it won't be defined again.
     * @Param courseID: the ID of the course to be defined as conflict.
     * @Param courseName: the name of the course to be defined as conflict.
     */
    void addConflictCourse(int courseID, String courseName){
        if(this.courseID == courseID){
            return;
        }
        if(conflictCourses.containsKey(courseID)){
            return;
        }
        conflictCourses.put(courseID,courseName);
    }

    /*
     * Remove the course from the conflict courses list. if there is no course with such id, the list won't be effected.
     * @Param c: the id of the course to remove.
     */
    void removeConflictCourse(int c){
        conflictCourses.remove(c);
    }

    /*
     * Defining each course within the given list as conflicted course,
     * if one of the courses is already defined, it won't be defined twice.
     * @Param courses: list of courses to define as conflicts.
     */
    void addConflictCourses(List<db.Course> courses){
        for (db.Course c: courses) {
            addConflictCourse(c.id,c.name);
        }
    }

    /*
     * get the course id.
     * @return: id of the course.
     */
    public int getCourseID() {
        return courseID;
    }

    /*
     * get the course name.
     * @return: name of the course.
     */
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

    /*
     * get size of the courseConflicts list.
     * @return: size of cinflictCourses.
     */
    int getNumOfConflictCourses(){
        return conflictCourses.size();
    }

    /*
     * two courses are considered to be equal, iff they have the same courseID.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return this == o || (o instanceof Course && courseID == ((Course)o).courseID);
    }

    @Override
    public int compareTo(Course o) {
        if(isRequired && o.isRequired){
            return compareCoursesByNumberOfConflicts(o);
        } else if(isRequired){
            return -1;
        } else if(o.isRequired){
            return 1;
        } else {
            return compareCoursesByNumberOfConflicts(o);
        }
    }

    private int compareCoursesByNumberOfConflicts(Course o) {
        if (getNumOfConflictCourses() > o.getNumOfConflictCourses()) {
            return -1;
        } else if (getNumOfConflictCourses() < o.getNumOfConflictCourses()) {
            return 1;
        } else {
            return 0;
        }
    }
}
