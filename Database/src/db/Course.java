package db;

import java.util.HashMap;
import java.util.Map;

public class Course {
    public int id;
    public String name;
    private Map<Integer, Integer> programs;

    public Course(int id, String name) {
        this.id = id;
        this.name = name;
        programs = new HashMap<>();
    }

    public void addProgram(int programId, int semester) {
        programs.put(programId, semester);
    }

    public int getProgramSemester(int programId) {
        return programs.get(programId);
    }
}
