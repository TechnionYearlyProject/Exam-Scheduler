package db;


import java.util.*;


public class Semester {

    enum Moed {
        MOED_A,
        MOED_B
    }

    private Map<Integer, Course> courses;
    private Set<String> programs;
    private Map<Moed, Schedule> schedules;

    public Semester() {
        courses = new HashMap<>();
        programs = new HashSet<>();
        schedules = new HashMap<>();
        schedules.put(Moed.MOED_A, new Schedule());
        schedules.put(Moed.MOED_B, new Schedule());
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

    public void registerCourse(int courseId, String program, int semesterNum) throws CourseUnknown, StudyProgramUnknown {
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

    public void setStartDate(Moed moed, Date start) throws InvalidSchedule {
        schedules.get(moed).setStartDate(start);
    }

    public void setEndDate(Moed moed, Date end) throws InvalidSchedule {
        schedules.get(moed).setEndDate(end);
    }

    public void scheduleCourse(int courseId, Moed moed, Date date) throws CourseUnknown, DateOutOfSchedule, UninitializedSchedule {
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
}
