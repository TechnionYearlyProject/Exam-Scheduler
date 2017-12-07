package db;


import java.util.*;


public class Semester {

    enum Moed {
        MOED_A("A"),
        MOED_B("B");

        String str;

        Moed(String str) {
            this.str = str;
        }
    }

    private Map<Integer, Course> courses;
    private Set<String> programs;
    private Map<Moed, Schedule> schedules;
    private Map<Moed, ConstraintList> constraints;

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

    public void addCourse(int courseId, String name) throws CourseAlreadyExist {
        if (courses.containsKey(courseId)) {
            throw new CourseAlreadyExist();
        }
        Course course = new Course(courseId, name);
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

    public void setStartDate(Moed moed, Calendar start) throws InvalidSchedule {
        schedules.get(moed).setStartDate(start);
    }

    public void setEndDate(Moed moed, Calendar end) throws InvalidSchedule {
        schedules.get(moed).setEndDate(end);
    }

    public void scheduleCourse(int courseId, Moed moed, Calendar date) throws CourseUnknown, DateOutOfSchedule,
            UninitializedSchedule, ScheduleDateAlreadyTaken {
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

    public void setConstraint(int courseId, Moed moed, Calendar start, Calendar end) throws UninitializedSchedule,
            DateOutOfSchedule, InvalidConstraint, CourseUnknown {
        if (schedules.get(moed).undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (start.before(schedules.get(moed).start) || end.after(schedules.get(moed).end)) {
            throw new DateOutOfSchedule();
        }
        if (!courses.containsKey(courseId)) {
            throw new CourseUnknown();
        }
        constraints.get(moed).setConstraint(courseId, start, end);
    }

    public void removeConstraint(int courseId, Moed moed) {
        constraints.get(moed).removeConstraint(courseId);
    }

    public ConstraintList getConstraintList(Moed moed) {
        ConstraintList cl = new ConstraintList();
        for (int courseId: courses.keySet()) {
            ConstraintList.Constraint constraint = constraints.get(moed).getConstraint(courseId);
            if (constraint != null) {
                try {
                    cl.setConstraint(courseId, constraint.start, constraint.end);
                } catch (InvalidConstraint ignored) {}
            }
        }
        return cl;
    }
}
