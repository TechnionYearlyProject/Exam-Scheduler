package db;


import db.exception.*;

import java.time.LocalDate;
import java.util.*;


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

    public void addStudyProgram(String program) throws StudyProgramAlreadyExist {
        if (programs.contains(program)) {
            throw new StudyProgramAlreadyExist();
        }
        programs.add(program);
    }

    public void removeStudyProgram(String program) {
        programs.remove(program);
        for (Course course: courses.values()) {
            course.removeStudyProgram(program);
        }
    }

    public List<String> getStudyProgramCollection() {
        return new ArrayList<>(programs);
    }

    public void addCourse(int courseId, String name, double creditPoint, int daysBefore, boolean isFirst, boolean isLast,
                          boolean isRequired, boolean hasExam) throws CourseAlreadyExist {
        if (courses.containsKey(courseId)) {
            throw new CourseAlreadyExist();
        }
        Course course = new Course(courseId, name, creditPoint, daysBefore, isFirst, isLast, isRequired, hasExam);
        courses.put(courseId, course);
    }

    public void addCourse(int courseId, String name, double creditPoint) throws CourseAlreadyExist {
        addCourse(courseId, name, creditPoint, -1, false, false, true, true);
    }

    public void removeCourse(int courseId) {
        courses.remove(courseId);
        for (Schedule schedule: schedules.values()) {
            schedule.unscheduleCourse(courseId);
        }
    }

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

    public void setStartDate(Moed moed, LocalDate start) throws InvalidSchedule {
        boolean wasNull = schedules.get(moed).start == null;
        schedules.get(moed).setStartDate(start);
        // Schedules are already updated
        if (!wasNull) {
            updateConstraints(moed);
        }
    }

    public void setEndDate(Moed moed, LocalDate end) throws InvalidSchedule {
        boolean wasNull = schedules.get(moed).end == null;
        schedules.get(moed).setEndDate(end);
        // Schedules are already updated
        if (!wasNull) {
            updateConstraints(moed);
        }
    }

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

    public void addConstraint(int courseId, Moed moed, LocalDate date)
            throws UninitializedSchedule, DateOutOfSchedule, CourseUnknown, DuplicateConstraints {
        addConstraint(courseId, moed, date, false);
    }

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
