package Logic;

import Logic.Exceptions.IllegalDaysBefore;
import db.Constraint;
import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.floor;

public class Course implements Comparable<Course>{
    private int daysBefore, courseID;
    private String courseName;
    private Map<Integer,String> conflictCourses;
    private boolean isLast, isFirst, isRequired, hasExam;
    private double creditPoints;
    private Set<Pair<String, Integer>> programs;
    private ArrayList<Constraint> goodConstraints;
    private ArrayList<Constraint> badConstraints;
    private boolean assigned;

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
        programs = o.programs;
        conflictCourses = new HashMap<>(o.getConflictCourses());
        goodConstraints = new ArrayList<>(o.goodConstraints);
        badConstraints = new ArrayList<>(o.badConstraints);
        assigned = o.assigned;
    }

    /*
     * number of credit points will be added here..
     */
    public Course(String courseName, Integer courseID, boolean isRequired, double cPoints) {
        conflictCourses = new HashMap<>();
        this.courseID = courseID;
        this.courseName = courseName;
        this.isLast = false;
        this.isFirst = false;
        this.isRequired = isRequired;
        this.creditPoints = cPoints;
        this.daysBefore = (int)floor(creditPoints) - 1; //For now (after many experiments) need to leave as cPoints-1
        this.hasExam = true;
        this.programs = new HashSet<>();
        goodConstraints = new ArrayList<>();
        badConstraints = new ArrayList<>();
        assigned = false;
    }

    public Course(String courseName, Integer courseID, boolean isRequired, double cPoints, Map<String, Integer> programs) {
        this(courseName, courseID, isRequired, cPoints);
        for(Map.Entry<String, Integer> program: programs.entrySet()){
            this.programs.add(new Pair<>(program.getKey(), program.getValue()));
        }
    }

    /*
     * Adding new constraint to the constraints list.
     * @Param c0: the Constraint to add.
     */
    void addConstraint(Constraint c){
        if(c.forbidden){//badConstraint
            if(badConstraints.contains(c)){
                return;
            }
            badConstraints.add(new Constraint(c.start,c.end,c.forbidden));
        } else {
            if(goodConstraints.contains(c)){
                return;
            }
            goodConstraints.add(new Constraint(c.start,c.end));
        }
    }

    /*
     * Remove constraint(bad or good) from the constraints lists. if there is such constraint it won't affect the list.
     * @Param c0: the Constraint to remove.
     */
    void removeConstraint(Constraint c){
        if(c.forbidden) {
            badConstraints.remove(c);
        } else {
            goodConstraints.remove(c);
        }
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
     * returns true if the given Constraint is bad constraint
     * (the course can not be assigned to this range of days).
     */
    boolean isBadConstraint(Constraint c){
        return badConstraints.contains(c);
    }

    /*
     * returns true if the given Constraint is good constraint
     * (the course can not be assigned to this range of days).
     */
    boolean isGoodConstraint(Constraint c){
        return goodConstraints.contains(c);
    }

    ArrayList<Constraint> getGoodConstraints(){
        Collections.sort(goodConstraints);
        return new ArrayList<>(goodConstraints);
    }

    ArrayList<Constraint> getBadConstraints(){
        Collections.sort(badConstraints);
        return new ArrayList<>(badConstraints);
    }

    /*
     * defining new Conflict of the course. if the course already defined as conflicted, it won't be defined again.
     * @Param courseID: the ID of the course to be defined as conflict.
     * @Param courseName: the name of the course to be defined as conflict.
     */
    void addConflictCourse(Integer courseID, String courseName){
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
    void removeConflictCourse(Integer c){
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
    public Integer getCourseID() {
        return courseID;
    }

    /*
     * get the course name.
     * @return: name of the course.
     */
    public String getCourseName() {
        return courseName;
    }


    void setDaysBefore(int daysBefore) throws IllegalDaysBefore {
        if(daysBefore < 1){
            throw new IllegalDaysBefore();
        }
        this.daysBefore = daysBefore;
    }
    int getDaysBefore(){
        return daysBefore;
    }


    void setAsLast(boolean isLast) {
        if (isLast) {
            this.isFirst = false;
        } //Course can't be as last and as first at the same time
        this.isLast = isLast;
    }

    public void setHasExam(boolean t){
        hasExam = t;
    }

    public boolean hasExam(){
        return hasExam;
    }
    public boolean isLast(){
        return isLast;
    }

    void setAsFirst(boolean isFirst) {
        if (isFirst) {
            this.isLast = false;
        } //Course can't be as last and as first at the same time
        this.isFirst = isFirst;
    }
    public boolean isFirst(){
        return isFirst;
    }

    void setAsRequired(boolean isRequired){this.isRequired = isRequired;}
    public boolean isRequired(){return isRequired;}

    Map<Integer,String> getConflictCourses(){
        return new HashMap<>(conflictCourses);
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
        return this == o || (o instanceof Course && courseID == (((Course) o).courseID));
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
            return Integer.compare(o.daysBefore,daysBefore);
        }
    }

    public void assign(){
        assigned = true;
    }

    public void unAssign(){
        assigned = false;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public Set<Pair<String, Integer>> getPrograms() {
        return programs;
    }
}
