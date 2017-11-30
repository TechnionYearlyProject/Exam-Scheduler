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

    public void setStudyProgram(String program, int semester) {
        programs.put(program, semester);
    }

    public void removeStudyProgram(String program) {
        programs.remove(program);
    }

    public int getStudyProgramSemester(String program) {
        return programs.get(program);
    }
}
