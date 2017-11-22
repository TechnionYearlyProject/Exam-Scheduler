package db;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private String baseDirectory;
    private Map<String, Semester> semesters;

    public Database() {
        baseDirectory = System.getProperty("user.dir");
        semesters = new HashMap<>();
        System.out.println(baseDirectory);
    }

    private String getSemesterDir(int year, String semester) {
        return year + '_' + semester;
    }

    public Semester loadSemester(int year, String sem) {
        String semesterDir = getSemesterDir(year, sem);
        String path = baseDirectory + '\\' + semesterDir;
        Semester semester = new Semester();

        // TODO Parse XML file

        semesters.put(semesterDir, semester);
        return semester;
    }

    public Semester getSemester(int year, String sem) {
        String semesterDir = getSemesterDir(year, sem);
        if (!semesters.keySet().contains(semesterDir)) {
            return null;
        }
        return semesters.get(semesterDir);
    }
}
