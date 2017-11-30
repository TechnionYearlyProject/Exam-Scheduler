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
            course.removeStudyProgram(program);
        }
    }

    public List<String> getStudyProgramCollection() {
        List<String> list = new ArrayList<>();
        list.addAll(programs);
        return list;
    }

    public Course addCourse(int id, String name) throws CourseAlreadyExist {
        if (courses.keySet().contains(id)) {
            throw new CourseAlreadyExist();
        }
        Course course = new Course(id, name);
        courses.put(id, course);
        return course;
    }

    public Course getCourse(int id) {
        if (!courses.keySet().contains(id)) {
            return null;
        }
        return courses.get(id);
    }

    public Collection<Course> getCourseCollection() {
        return courses.values();
    }
}
