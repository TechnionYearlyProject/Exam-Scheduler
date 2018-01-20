package db;

import Logic.Exceptions.IllegalDaysBefore;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represent a course of the faculty and contain all the parameters available to
 * the user to configure the schedule.
 * @author Rephael Azoulay
 * @date 19/01/2018
 */
public class Course {
    public int courseID;
    public String courseName;
    public double creditPoints;
    public int daysBefore;
    public boolean isRequired, hasExam;
    public boolean isLast, isFirst;
    public Map<String, Integer> programs;

    public Course(int courseID, String courseName, double creditPoints, int daysBefore, boolean isFirst, boolean isLast,
                  boolean isRequired, boolean hasExam) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.creditPoints = creditPoints;
        if (daysBefore == -1) {
            this.daysBefore = (int) creditPoints - 1;
        } else {
            this.daysBefore = daysBefore;
        }
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.isRequired = isRequired;
        this.hasExam = hasExam;
        programs = new HashMap<>();
    }

    public Course(Course other) {
        courseID = other.courseID;
        courseName = other.courseName;
        creditPoints = other.creditPoints;
        daysBefore = other.daysBefore;
        isFirst = other.isFirst;
        isLast = other.isLast;
        isRequired = other.isRequired;
        hasExam = other.hasExam;
        programs = new HashMap<>();
        for (Map.Entry<String, Integer> entry: other.programs.entrySet()) {
            String program = entry.getKey();
            int semester = entry.getValue();
            programs.put(program, semester);
        }
    }

    /**
     * Assign the course to a semester in the given study program.
     * @param program The name of the study program.
     * @param semester The number of the semester.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setStudyProgram(String program, int semester) {
        programs.put(program, semester);
    }

    public int studyProgramSize(){
        return programs.size();
    }

    /**
     * Remove the assignation from a study program.
     * @param program The name of the study program.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void removeStudyProgram(String program) {
        programs.remove(program);
    }

    /**
     * Get the number of the semester in the study program at which the course was assigned.
     * @param program The name of the study program.
     * @return The number of the semester in the given study program, or 0 if the course
     * was not assigned to this study program.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public int getStudyProgramSemester(String program) {
        if (!programs.containsKey(program)) {
            return 0;
        }
        return programs.get(program);
    }

    public Map<String, Integer> getPrograms() {
        return programs;
    }

    public int getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    /**
     * Set the required number of days to prepare the exam.
     * @param daysBefore The number of days. The number must be strictly positive.
     * @throws IllegalDaysBefore If the value passed is 0 or negative.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setDaysBefore(int daysBefore) throws IllegalDaysBefore {
        if(daysBefore < 1){
            throw new IllegalDaysBefore();
        }
        this.daysBefore = daysBefore;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    /**
     * Define if the course should be scheduled at the beginning of the exam period.
     * @param isFirst Whether the course should be scheduled at the beginning of the exam period or not.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setAsFirst(boolean isFirst) {
        if (isFirst) {
            this.isLast = false;
        }
        this.isFirst = isFirst;
    }

    /**
     * Define if the course should be scheduled at the end of the exam period.
     * @param isLast Whether the course should be scheduled at the end of the exam period or not.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setAsLast(boolean isLast) {
        if (isLast) {
            this.isFirst = false;
        }
        this.isLast = isLast;
    }

    public boolean isFirst(){
        return isFirst;
    }

    public boolean isLast() {
        return isLast;
    }
}
