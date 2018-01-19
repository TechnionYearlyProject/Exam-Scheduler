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

    public void setStudyProgram(String program, int semester) {
        programs.put(program, semester);
    }

    public int studyProgramSize(){
        return programs.size();
    }

    public void removeStudyProgram(String program) {
        programs.remove(program);
    }

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

    public void setDaysBefore(int daysBefore) throws IllegalDaysBefore {
        if(daysBefore < 1){
            throw new IllegalDaysBefore();
        }
        this.daysBefore = daysBefore;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setAsFirst(boolean isFirst) {
        if (isFirst) {
            this.isLast = false;
        }
        this.isFirst = isFirst;
    }

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
