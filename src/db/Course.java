package db;

import java.util.HashMap;
import java.util.Map;

public class Course {
    public int id;
    public String name;
    public double weight;
    Map<String, Integer> programs;

    public Course(int id, String name, double weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
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

    public int studyProgramSize(){
        return programs.size();
    }

    public void removeStudyProgram(String program) {
        programs.remove(program);
    }

    public int getStudyProgramSemester(String program) {
        if (!programs.containsKey(program)) {
            return 0;
        }
        return programs.get(program);
    }
}
