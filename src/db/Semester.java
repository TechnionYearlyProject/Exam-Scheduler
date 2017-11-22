package db;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Semester {
    public Map<Integer, Course> courses;

    public Semester() {
        courses = new HashMap<>();
    }

    public Course addCourse(int id, String name) {
        Course course = new Course(id, name);
        if (courses.keySet().contains(id)) {
            // Course already exists
            // Throw something ?
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
