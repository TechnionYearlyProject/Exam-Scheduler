package db;


import db.exception.*;

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

    public Semester() {
        courses = new HashMap<>();
        programs = new HashSet<>();
        schedules = new HashMap<>();
        schedules.put(Moed.MOED_A, new Schedule());
        schedules.put(Moed.MOED_B, new Schedule());
        constraints = new HashMap<>();
        constraints.put(Moed.MOED_A, new ConstraintList());
        constraints.put(Moed.MOED_B, new ConstraintList());
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
        List<String> list = new ArrayList<>();
        list.addAll(programs);
        return list;
    }

    public void addCourse(int courseId, String name, double weight) throws CourseAlreadyExist {
        if (courses.containsKey(courseId)) {
            throw new CourseAlreadyExist();
        }
        Course course = new Course(courseId, name, weight);
        courses.put(courseId, course);
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

    public List<Course> getCourseCollection() {
        List<Course> list = new ArrayList<>();
        for (Course course: courses.values()) {
            list.add(new Course(course)); // Copy ctor perform deep copy
        }
        return list;
    }

    public List<Course> getCourseBySemester(int i) {
        List<Course> list = new ArrayList<>();
        for (String program: programs) {
            for (Course course : courses.values()) {
                if (course.getStudyProgramSemester(program) == i) {
                    list.add(new Course(course)); // Copy ctor perform deep copy
                }
            }
        }
        return list;
    }

    private void updateConstraints(Moed moed) {
        for (int courseId: constraints.get(moed).constraints.keySet()) {
            for (Constraint c: constraints.get(moed).getConstraints(courseId)) {
                Constraint old = new Constraint(c.start, c.end);
                boolean update = false;
                if (c.start.before(schedules.get(moed).start)) {
                    update = true;
                    c.start = schedules.get(moed).start;
                }
                if (c.end.after(schedules.get(moed).end)) {
                    update = true;
                    c.end = schedules.get(moed).end;
                }
                if (update) {
                    constraints.get(moed).removeConstraint(courseId, old.start, old.end);
                    try {
                        constraints.get(moed).addConstraint(courseId, c.start, c.end);
                    } catch (InvalidConstraint | OverlappingConstraints ignored) {}
                }
            }
        }
    }

    public Calendar getStartDate(Moed moed) {
        if (schedules.get(moed).start == null) {
            return null;
        }
        return (Calendar) schedules.get(moed).start.clone();
    }

    public Calendar getEndDate(Moed moed) {
        if (schedules.get(moed).end == null) {
            return null;
        }
        return (Calendar) schedules.get(moed).end.clone();
    }

    public void setStartDate(Moed moed, Calendar start) throws InvalidSchedule {
        boolean wasNull = schedules.get(moed).start == null;
        schedules.get(moed).setStartDate(start);
        // Schedules are already updated
        if (!wasNull) {
            updateConstraints(moed);
        }
    }

    public void setEndDate(Moed moed, Calendar end) throws InvalidSchedule {
        boolean wasNull = schedules.get(moed).end == null;
        schedules.get(moed).setEndDate(end);
        // Schedules are already updated
        if (!wasNull) {
            updateConstraints(moed);
        }
    }

    public void scheduleCourse(int courseId, Moed moed, Calendar date) throws CourseUnknown, DateOutOfSchedule,
            UninitializedSchedule {
        if (!courses.containsKey(courseId)) {
            throw new CourseUnknown();
        }
        if (date.before(schedules.get(moed).start) || date.after(schedules.get(moed).end)) {
            throw new DateOutOfSchedule();
        }
        schedules.get(moed).scheduleCourse(courseId, date);
    }

    public void unscheduleCourse(int courseId, Moed moed) {
        schedules.get(moed).unscheduleCourse(courseId);
    }

    public Map<Integer, Calendar> getSchedule(Moed moed) {
        Map<Integer, Calendar> schedule = new HashMap<>();
        for (int courseId: courses.keySet()) {
            Calendar date = schedules.get(moed).getCourseSchedule(courseId);
            if (date != null) {
                schedule.put(courseId, date);
            }
        }
        return schedule;
    }

    public void addConstraint(int courseId, Moed moed, Calendar start, Calendar end) throws UninitializedSchedule,
            DateOutOfSchedule, InvalidConstraint, CourseUnknown, OverlappingConstraints {
        if (schedules.get(moed).undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (start.before(schedules.get(moed).start) || end.after(schedules.get(moed).end)) {
            throw new DateOutOfSchedule();
        }
        if (!courses.containsKey(courseId)) {
            throw new CourseUnknown();
        }
        constraints.get(moed).addConstraint(courseId, start, end);
    }

    public void removeConstraint(int courseId, Moed moed, Calendar start, Calendar end) {
        constraints.get(moed).removeConstraint(courseId, start, end);
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
