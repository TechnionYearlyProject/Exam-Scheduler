package Logic;

import db.ConstraintList;
import db.Course;
import db.Database;
import db.Semester;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseLoader {
    private Database db;
    private Map<Integer,Logic.Course> courses;//<courseID,Course>
    private Map<Integer,db.Course> dbCourses;
    private Map<String, Semester> semesters;
    public CourseLoader(Database database, ConstraintList cL){
        this.db = db;
        semesters = db.getSemesters();
        courses = new HashMap<>();
        dbCourses = db.getCourses();
        //building Logic CourseList.
        buildLogicCourses();
        //updating conflictList for each Course.
        setCoursesConflicts();
        //TODO: add constraints for eachCourse;
    }

    private void setCoursesConflicts() {
        for (Map.Entry<String, Semester> entry: semesters.entrySet()) {
            String program = entry.getKey();
            Semester s = entry.getValue();
            List<Course> semesterCourses = s.getCourseCollection();

            for (Course c:s.getCourseCollection()) {
                Logic.Course current = courses.get(c.id);
                current.addConflictCourses(semesterCourses);
                //reference no need to re-Put.
            }
        }
    }

    private void buildLogicCourses() {
        for (Map.Entry<Integer, Course> entry: dbCourses.entrySet()) {
            boolean isRequired = false;
            Course c = entry.getValue();
            if(c.studyProgramSize() > 0){
                isRequired = true;
            }
            Logic.Course current =  new Logic.Course(c.name,c.id,isRequired);
            courses.put(c.id,current);
        }
    }

    public Map<Integer,Logic.Course> getCourses(){
        return this.courses;
    }
}
