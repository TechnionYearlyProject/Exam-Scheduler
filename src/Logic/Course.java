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
    private ArrayList<Constraint> constraints;
    private Set<Pair<String, Integer>> programs;

    /**
     * @author ucfBader.
     * Copy constructor (deep copy).
     * @param o: the Course to be copied.
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
        hasExam = o.hasExam;
        programs = o.programs;
    }

    /**
     * @author ucfBader.
     * Course constructor.
     */
    public Course(String courseName, Integer courseID, boolean isRequired, double cPoints) {
        conflictCourses = new HashMap<>();
        constraints = new ArrayList<>();
        this.courseID = courseID;
        this.courseName = courseName;
        this.isLast = false;
        this.isFirst = false;
        this.isRequired = isRequired;
        this.creditPoints = cPoints;
        this.daysBefore = (int)floor(creditPoints) - 1;
        this.hasExam = true;
        this.programs = new HashSet<>();
    }


    public Course(String courseName, Integer courseID, boolean isRequired, double cPoints, Map<String, Integer> programs) {
        this(courseName, courseID, isRequired, cPoints);
        for(Map.Entry<String, Integer> program: programs.entrySet()){
            this.programs.add(new Pair<>(program.getKey(), program.getValue()));
        }
    }

    /**
     * @author ucfBader
     * Adding new constraint to the constraints list.
     * @param c the constraint to add.
     */
    void addConstraint(Constraint c){
        if(constraints.contains(c)){
            return;
        }
        constraints.add(new Constraint(c.date));
    }

    /**
     * @author ucfBader
     * Adding a list of Constraints. if one of the Constraints already exists, it won't be added again.
     * @Param ls: the list of constraints to add.
     */
    void addConstraint(List<Constraint> ls){
        for (Constraint c: ls) {
            addConstraint(c);
        }
    }

    /**
     * @author ucfBader
     * Remove constraint from the constraints list. if there is such constraint it won't affect the list.
     * @Param c: the Constraint to remove.
     */
    void removeConstraint(Constraint c){
        constraints.remove(c);
    }

    /**
     * @author ucfBader
     * defining new Conflict of the course. if the course already defined as conflicted, it won't be defined again.
     * @Param courseID: the ID of the course to be defined as conflict.
     * @Param courseName: the name of the course to be defined as conflict.
     */
    public void addConflictCourse(Integer courseID, String courseName){
        if(this.courseID == courseID){
            return;
        }
        if(conflictCourses.containsKey(courseID)){
            return;
        }
        conflictCourses.put(courseID,courseName);
    }

    /**
     * @author ucfBader.
     * Remove the course from the conflict courses list. if there is no course with such id, the list won't be effected.
     * @Param c0: the id of the course to remove.
     */
    public void removeConflictCourse(Integer c){
        conflictCourses.remove(c);
    }

    /**
     * @author ucfBader.
     * Defining each course within the given list as conflicted course,
     * if one of the courses is already defined, it won't be defined twice.
     * @Param courses: list of courses to define as conflicts.
     */
    void addConflictCourses(List<Pair<Integer,String>> courses){
        for (Pair c: courses) {
            addConflictCourse((int)c.getKey(),(String)c.getValue());
        }
    }

    /**
     * @author ucfBader.
     * get course id.
     * @return: id of the course.
     */
    public Integer getCourseID() {
        return courseID;
    }

    /**
     * @author ucfBader.
     * get the course name.
     * @return: name of the course.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @author ucfBader.
     * manually change the study days needed for this course.
     * @param daysBefore : the new number of days.
     * @throws IllegalDaysBefore : if the given number is less than 1.
     */
    public void setDaysBefore(int daysBefore) throws IllegalDaysBefore {
        if(daysBefore < 1){
            throw new IllegalDaysBefore();
        }
        this.daysBefore = daysBefore;
    }

    /**
     * @author ucfBader.
     * @return num of days before.
     */
    public int getDaysBefore(){
        return daysBefore;
    }

    /**
     * @author ucfBader.
     * assigning the course at the end of the study period.
     */
    public void setAsLast(boolean isLast) {
        if (isLast) {
            this.isFirst = false;
        } //Course can't be as last and as first at the same time
        this.isLast = isLast;
    }

    /**
     * @author ucfBader
     * if the user want to schedule the course at the current exams period.
     */
    public void setHasExam(boolean t){
        hasExam = t;
    }

    /**
     * @author ucfBader.
     * @return if the course has an exam returns true, ow, false.
     */
    public boolean hasExam(){
        return hasExam;
    }
    public boolean isLast(){
        return isLast;
    }

    /**
     * @author ucfBader
     * assigning the course at the start of the study period.
     */
    public void setAsFirst(boolean isFirst) {
        if (isFirst) {
            this.isLast = false;
        } //Course can't be as last and as first at the same time
        this.isFirst = isFirst;
    }

    /**
     * @author ucfBader.
     * @return returns true if the course was defined as first (to schedule at the start of exam period), ow, returns false.
     */
    public boolean isFirst(){
        return isFirst;
    }

    //void setAsRequired(boolean isRequired){this.isRequired = isRequired;}

    /**
     * @author ucfBader.
     * @return if the course is mandatory at at least 1 study program returns true, ow, false.
     */
    public boolean isRequired(){return isRequired;}

    /**
     * @author ucfBader.
     * @return new copy of the cpurse conflicts.
     */
    public Map<Integer,String> getConflictCourses(){
        return new HashMap<>(conflictCourses);

    }

    /**
     * @author ucfBader.
     * @return sorted copy of the course constraints.
     */
    public ArrayList<Constraint> getConstraints(){
        Collections.sort(constraints);
        return new ArrayList<>(constraints);
    }

    /*
     * @author ucfBader.
     * get size of the courseConflicts list.
     * @return: size of cinflictCourses.
     */
    int getNumOfConflictCourses(){
        return conflictCourses.size();
    }

    /**
     * @author ucfBader.
     * two courses are considered to be equal, iff they have the same courseID.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return this == o || (o instanceof Course && courseID == (((Course) o).courseID));
    }

    /**
     * @author ucfBader.
     * comparing this course to other courses, relevant for sorting methods.
     * @param o : the course to be compared to.
     * @return 0 if the courses considered equal.
     *         -1 if this course has more conflicts or needs more days to study than the o course, or is required.
     *         1 otherwise.
     */
    @Override
    public int compareTo(Course o) {
        //Courses that defined to be last/first have the less priority
        if ((isLast || isFirst) && (o.isLast || o.isFirst)){
            return 0;
        } else if (isLast || isFirst){
            return 1;
        } else if (o.isLast || o.isFirst){
            return -1;
        }
        //Otherwise, required courses with more conflicts have prority
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

    public Set<Pair<String, Integer>> getPrograms() {
        return programs;
    }

    public Double getCreditPoints() {
        return creditPoints;
    }
}
