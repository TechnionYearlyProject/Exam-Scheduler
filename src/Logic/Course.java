package Logic;

import db.Constraint;
import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.floor;

public class Course implements Comparable<Course>{
    private int courseID,daysBefore;
    private String courseName;
    private Map<Integer,String> conflictCourses;
    private boolean isLast, isFirst, isRequired;
    private double creditPoints;
    private ArrayList<Constraint> constraints;

    /*
     * Logic.Course copy c0'tor performing deepCopy.
     */
    public Course(Course o) {
        courseID = o.courseID;
        courseName = o.courseName;
        isRequired = o.isRequired;
        isLast = o.isLast;
        isFirst = o.isFirst;
        daysBefore = o.daysBefore;
        creditPoints = o.creditPoints;
        constraints = new ArrayList<>(o.getConstraints());
        conflictCourses = new HashMap<>(o.getConflictCourses());
    }

    /*
     * number of credit points will be added here..
     */
    public Course(String courseName, int courseID, boolean isRequired, double cPoints){
        conflictCourses = new HashMap<>();
        constraints = new ArrayList<>();
        this.courseID = courseID;
        this.courseName = courseName;
        this.isLast = false;
        this.isFirst = false;
        this.isRequired = isRequired;
        this.creditPoints = cPoints;
        this.daysBefore = (int)floor(creditPoints);
    }

    /*
     * Adding new constraint to the constraints list.
     * @Param c0: the Constraint to add.
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
     * @Param c0: the Constraint to remove.
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
     * @Param c0: the id of the course to remove.
     */
    void removeConflictCourse(int c){
        conflictCourses.remove(c);
    }

    /*
     * Defining each course within the given list as conflicted course,
     * if one of the courses is already defined, it won't be defined twice.
     * @Param courses: list of courses to define as conflicts.
     */
    void addConflictCourses(List<Pair<Integer,String>> courses){
        for (Pair c: courses) {
            addConflictCourse((int)c.getKey(),(String)c.getValue());
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
        if(daysBefore < 1){
            return;//TODO? MAYBE THROW EXCEPTION
        }
        this.daysBefore = daysBefore;
    }
    int getDaysBefore(){
        return daysBefore;
    }

    void setAsLast(boolean isLast){
        if(isLast){this.isFirst = false;} //Course can't be as last and as first at the same time
        this.isLast = isLast;
    }
    boolean isLast(){
        return isLast;
    }

    void setAsFirst(boolean isFirst){
        if(isFirst){this.isLast = false;} //Course can't be as last and as first at the same time
        this.isFirst = isFirst;
    }
    boolean isFirst(){
        return isFirst;
    }

    void setAsRequired(boolean isRequired){this.isRequired = isRequired;}
    boolean isRequired(){return isRequired;}

    double getCreditPoints(){
        return creditPoints;
    }

    Map<Integer,String> getConflictCourses(){
       return new HashMap<>(conflictCourses);

    }

    ArrayList<Constraint> getConstraints(){
        Collections.sort(constraints);
        return new ArrayList<>(constraints);
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
            return Integer.compare(o.daysBefore, daysBefore);
        }
    }
}
