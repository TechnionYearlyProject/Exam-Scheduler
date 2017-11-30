package db;


import java.util.*;


public class Semester {
    public Map<Integer, Course> courses;
    private Set<String> programs;

    public Semester() {
        courses = new HashMap<>();
        programs = new HashSet<>();
    }

    public void addStudyProgram(String program) {
        // TODO Check if add successful
        programs.add(program);
    }

    public void removeStudyProgram(String program) {
        // TODO Check if removal successful
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

    public Course addCourse(int id, String name) {
        Course course = new Course(id, name);
        if (courses.keySet().contains(id)) {
            return null;
        }
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
