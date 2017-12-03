package db;

import java.util.HashMap;
import java.util.Map;

public class Course {
    public int id;
    public String name;
    private Map<String, Integer> programs;

    public Course(int id, String name) {
        this.id = id;
        this.name = name;
        programs = new HashMap<>();
    }

    public Course(Course other) {
        id = other.id;
        name = other.name;
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

    public void removeStudyProgram(String program) {
        programs.remove(program);
    }

    public int getStudyProgramSemester(String program) throws CourseUnregistered {
        if (!programs.containsKey(program)) {
            throw new CourseUnregistered();
        }
        return programs.get(program);
    }
}
