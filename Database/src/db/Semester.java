package db;


import java.util.*;


public class Semester {
    public Map<Integer, Course> courses;
    private Set<String> programs;

    public Semester() {
        courses = new HashMap<>();
        programs = new HashSet<>();
    }

    public void addStudyProgram(String program) throws StudyProgramAlreadyExist {
        if (programs.contains(program)) {
            throw new StudyProgramAlreadyExist();
        }
        programs.add(program);
    }

    public void removeStudyProgram(String program) throws StudyProgramUnknown {
        if (!programs.contains(program)) {
            throw new StudyProgramUnknown();
        }
        programs.remove(program);
        for (Course course: courses.values()) {
            try {
                course.removeStudyProgram(program);
            } catch (CourseUnregistered e) {
                // Nothing to do...
            }
        }
    }

    public List<String> getStudyProgramCollection() {
        List<String> list = new ArrayList<>();
        list.addAll(programs);
        return list;
    }

    public void addCourse(int id, String name) throws CourseAlreadyExist {
        if (courses.containsKey(id)) {
            throw new CourseAlreadyExist();
        }
        Course course = new Course(id, name);
        courses.put(id, course);
    }

    public void removeCourse(int id) throws CourseUnknown {
        if (!courses.containsKey(id)) {
            throw new CourseUnknown();
        }
        courses.remove(id);
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

    public void unregisterCourse(int courseId, String program) throws CourseUnknown, StudyProgramUnknown, CourseUnregistered {
        if (!courses.keySet().contains(courseId)) {
            throw new CourseUnknown();
        }
        if (!programs.contains(program)) {
            throw new StudyProgramUnknown();
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
}
