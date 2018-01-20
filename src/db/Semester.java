package db;


import db.exception.*;

import java.time.LocalDate;
import java.util.*;

/**
 * This class contains all the data needed for the algorithm, and provide the API to edit
 * this data. It ensure the semester's data stays valid and logical.
 * @author Rephael Azoulay
 * @date 19/01/2018
 */
public class Semester {

    public enum Moed {
        MOED_A("A"),
        MOED_B("B");

        String str;

        Moed(String str) {
            this.str = str;
        }
    }

    Map<Integer, Course> courses;
    Set<String> programs;
    Map<Moed, Schedule> schedules;
    public Map<Moed, ConstraintList> constraints;
    public Map<Integer, Set<Integer>> conflicts;

    public Semester() {
        courses = new HashMap<>();
        programs = new HashSet<>();
        schedules = new HashMap<>();
        schedules.put(Moed.MOED_A, new Schedule());
        schedules.put(Moed.MOED_B, new Schedule());
        constraints = new HashMap<>();
        constraints.put(Moed.MOED_A, new ConstraintList());
        constraints.put(Moed.MOED_B, new ConstraintList());
        conflicts = new HashMap<>();
    }

    /**
     * Add a study program to the semester.
     * @param program the name of the study program.
     * @throws StudyProgramAlreadyExist If there is already a study program with this name
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void addStudyProgram(String program) throws StudyProgramAlreadyExist {
        if (programs.contains(program)) {
            throw new StudyProgramAlreadyExist();
        }
        programs.add(program);
    }

    /**
     * Remove a study program from the semester.
     * @param program the name of the study program
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void removeStudyProgram(String program) {
        programs.remove(program);
        for (Course course: courses.values()) {
            course.removeStudyProgram(program);
        }
    }

    public List<String> getStudyProgramCollection() {
        return new ArrayList<>(programs);
    }

    /**
     * Add a course to the semester, with all the data needed for the scheduling algorithm.
     * @throws CourseAlreadyExist If the is a course with the same ID in the Semester.
     * @throws CourseFirstAndLast If the IsFirst and IsLast flags are both true.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void addCourse(int courseId, String name, double creditPoint, int daysBefore, boolean isFirst,
                          boolean isLast, boolean isRequired, boolean hasExam) throws CourseAlreadyExist,
            CourseFirstAndLast {
        if (courses.containsKey(courseId)) {
            throw new CourseAlreadyExist();
        }
        if (isFirst && isLast) {
            throw new CourseFirstAndLast();
        }
        Course course = new Course(courseId, name, creditPoint, daysBefore, isFirst, isLast, isRequired, hasExam);
        courses.put(courseId, course);
    }

    /**
     * Overloaded method to add course with default value flags.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void addCourse(int courseId, String name, double creditPoint) throws CourseAlreadyExist, CourseFirstAndLast {
        addCourse(courseId, name, creditPoint, -1,
                false, false, true, true);
    }

    /**
     * Remove a course from the semester, and the exam dates and constraints associated to this course.
     * @param courseId the ID of the course.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void removeCourse(int courseId) {
        courses.remove(courseId);
        for (Schedule schedule: schedules.values()) {
            schedule.unscheduleCourse(courseId);
        }
        for (ConstraintList list: constraints.values()) {
            list.constraints.remove(courseId);
        }
    }

    /**
     * Define at which semester the course should be taken in the given study program.
     * @param courseId the ID of the course.
     * @param program The name of the program.
     * @param semesterNum The number of the semester.
     * @throws CourseUnknown If there is no course with the given ID.
     * @throws StudyProgramUnknown If there is no study program with the given name.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void registerCourse(int courseId, String program, int semesterNum) throws CourseUnknown,
            StudyProgramUnknown {
        if (!courses.containsKey(courseId)) {
            throw new CourseUnknown();
        }
        if (!programs.contains(program)) {
            throw new StudyProgramUnknown();
        }
        courses.get(courseId).setStudyProgram(program, semesterNum);
    }

    /**
     * Remove a semester assignation, in a study program, from a course. Has no effect if there is no
     * course/study program corresponding to the parameters.
     * @param courseId the ID of the course.
     * @param program The name of the program.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void unregisterCourse(int courseId, String program) {
        if (!courses.keySet().contains(courseId)) {
            return;
        }
        courses.get(courseId).removeStudyProgram(program);
    }

    public Course getCourse(int courseId) throws CourseUnknown {
        if (!courses.containsKey(courseId)) {
            throw new CourseUnknown();
        }
        return courses.get(courseId);
    }

    public List<Course> getCourseCollection() {
        List<Course> list = new ArrayList<>();
        for (Course course: courses.values()) {
            list.add(new Course(course)); // Copy ctor perform deep copy
        }
        return list;
    }

    /**
     * Return a list of all the courses that should be taken at the semester number i, in
     * all of the study programs.
     * @param i The number of the semester
     * @return A list of courses.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public List<Course> getCourseBySemester(int i) {
        List<Course> list = new ArrayList<>();
        for (Course course: courses.values()) {
            for (String program: programs) {
                if (course.getStudyProgramSemester(program) == i) {
                    list.add(new Course(course)); // Copy ctor perform deep copy
                    break;
                }
            }
        }
        return list;
    }

    private void updateConstraints(Moed moed) {
        for (List<Constraint> list: constraints.get(moed).constraints.values()) {
            list.removeIf(c ->
                    c.date.isBefore(schedules.get(moed).start) || c.date.isAfter(schedules.get(moed).end));
        }
    }

    public LocalDate getStartDate(Moed moed) {
        if (schedules.get(moed).start == null) {
            return null;
        }
        return schedules.get(moed).start;
    }

    public LocalDate getEndDate(Moed moed) {
        if (schedules.get(moed).end == null) {
            return null;
        }
        return schedules.get(moed).end;
    }

    /**
     * Define the start day of one of the exam period (Moed A or B).
     * @param moed The Moed to be scheduled
     * @param start the Start date of the schedule
     * @throws InvalidSchedule if the end date of this Moed is before the given date.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setStartDate(Moed moed, LocalDate start) throws InvalidSchedule {
        boolean wasNull = schedules.get(moed).start == null;
        schedules.get(moed).setStartDate(start);
        // Schedules are already updated
        if (!wasNull) {
            updateConstraints(moed);
        }
    }

    /**
     * Define the end day of one of the exam period (Moed A or B).
     * @param moed The Moed to be scheduled
     * @param end the end date of the schedule
     * @throws InvalidSchedule if the start date of this Moed is after the given date.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setEndDate(Moed moed, LocalDate end) throws InvalidSchedule {
        boolean wasNull = schedules.get(moed).end == null;
        schedules.get(moed).setEndDate(end);
        // Schedules are already updated
        if (!wasNull) {
            updateConstraints(moed);
        }
    }

    /**
     * Define an exam day for a course, in one of the exam periods (Moed A or B).
     * @param courseId the ID of the course.
     * @param moed the Moed to be scheduled
     * @param date the date of the exam.
     * @throws CourseUnknown If there is no course with the given ID.
     * @throws DateOutOfSchedule If the given date is before the start date or after the end date of the moed.
     * @throws UninitializedSchedule If Moed has no start or end date.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void scheduleCourse(int courseId, Moed moed, LocalDate date) throws CourseUnknown, DateOutOfSchedule,
            UninitializedSchedule {
        if (!courses.containsKey(courseId)) {
            throw new CourseUnknown();
        }
        if (schedules.get(moed).undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (date.isBefore(schedules.get(moed).start) || date.isAfter(schedules.get(moed).end)) {
            throw new DateOutOfSchedule();
        }
        schedules.get(moed).scheduleCourse(courseId, date);
    }

    /**
     * Remove the exam day of a course from the Semester. Has no effect if there is no course with
     * the given ID or if the course has no exam date.
     * @param courseId the ID of the course.
     * @param moed The Moed to be unscheduled.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void unscheduleCourse(int courseId, Moed moed) {
        schedules.get(moed).unscheduleCourse(courseId);
    }

    public Map<Integer, LocalDate> getSchedule(Moed moed) {
        Map<Integer, LocalDate> schedule = new HashMap<>();
        for (int courseId: courses.keySet()) {
            LocalDate date = schedules.get(moed).getCourseSchedule(courseId);
            if (date != null) {
                schedule.put(courseId, date);
            }
        }
        return schedule;
    }

    /**
     * Add a constraint to a course, at which the algorithm must (or musnt) schedule the exam.
     * @param courseId The id of the course.
     * @param moed the Moed to be scheduled.
     * @param date the date of the exam.
     * @param forbidden Whether the algorithm must or musn't schedule the exam to this date.
     * @throws UninitializedSchedule If the moed has no start or end date.
     * @throws DateOutOfSchedule If the given date is before the start date or after the end date of the moed.
     * @throws CourseUnknown If there is no course in the semester with the given ID.
     * @throws DuplicateConstraints If there is already a constraint set to the same date.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void addConstraint(int courseId, Moed moed, LocalDate date, boolean forbidden)
            throws UninitializedSchedule, DateOutOfSchedule, CourseUnknown, DuplicateConstraints {
        if (schedules.get(moed).undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (date.isBefore(schedules.get(moed).start) || date.isAfter(schedules.get(moed).end)) {
            throw new DateOutOfSchedule();
        }
        if (!courses.containsKey(courseId)) {
            throw new CourseUnknown();
        }
        constraints.get(moed).addConstraint(courseId, date, forbidden);
    }

    /**
     * Overloaded method to add constraint with default value flags.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void addConstraint(int courseId, Moed moed, LocalDate date)
            throws UninitializedSchedule, DateOutOfSchedule, CourseUnknown, DuplicateConstraints {
        addConstraint(courseId, moed, date, false);
    }

    /**
     * Remove a constraint from a course. If there is no course with this ID, or if the course has
     * no constraint at this date in the given Moed, the function has no effect.
     * @param courseId the ID of the course.
     * @param moed the moed to be scheduled.
     * @param date the date of the constraint to be removed.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void removeConstraint(int courseId, Moed moed, LocalDate date) {
        constraints.get(moed).removeConstraint(courseId, date);
    }

    public List<Constraint> getConstraintList(Moed moed, int courseId) {
        return constraints.get(moed).getConstraints(courseId);
    }

    public Map<Integer, List<Constraint>> getConstraintLists(Moed moed) {
        Map<Integer, List<Constraint>> map = new HashMap<>();
        for (int courseId: constraints.get(moed).constraints.keySet()) {
            map.put(courseId, constraints.get(moed).getConstraints(courseId));
        }
        return map;
    }

    /**
     * Return a list of the courses that should be taken in the given semester of a specific study program.
     * @param program the name of the program.
     * @param semesterNum the number of the semester.
     * @return a list of Course objects.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public List<Course> getCoursesByProgramAndSemester(String program, int semesterNum) {
        List<Course> l = new ArrayList<>();
        for (Course course: courses.values()) {
            if (course.getStudyProgramSemester(program) == semesterNum) {
                l.add(new Course(course));
            }
        }
        return l;
    }
}
