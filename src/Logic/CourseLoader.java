package Logic;

import db.*;
import db.Course;

import java.util.*;

public class CourseLoader {
    private Database db;
    private Map<Integer,Logic.Course> courses;//<courseID,Course>
    private Map<Integer,db.Course> dbCourses;
    private Map<String, Semester> semesters;
    private ArrayList<Logic.Course> sortedCoursesList;
    public CourseLoader(Database database, ConstraintList cL) {
        this.db = database;
        semesters = db.getSemesters();
        courses = new HashMap<>();
        sortedCoursesList = new ArrayList<>();
        dbCourses = db.getCourses();
        //building Logic CourseList.
        buildLogicCourses();
        //updating conflictList for each Course.
        setCoursesConflicts();
        //updating constraints for each course.
        setCoursesConstraints(cL);
        //sortCourses();
    }

    private void setCoursesConstraints(ConstraintList cL) {
        for (Map.Entry<Integer, List<Constraint>> entry : cL.constraints.entrySet()) {
            List<Constraint> ls = entry.getValue();

            Logic.Course course = courses.get(entry.getKey());
            course.addConstraint(ls);
        }
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

    /*
     * Logic.Course map. all the relevant data to the algorithm is here.
     */
    public Map<Integer,Logic.Course> getCourses(){
        return this.courses;
    }

//    public List<Logic.Course> getSortedCourses(){
//        return this.sortedCoursesList;
//    }
//
//    private void sortCourses(){
//        for (Map.Entry<Integer, Logic.Course> entry: courses.entrySet()) {
//            sortedCoursesList.add(new Logic.Course(entry.getValue()));
//        }
//        Collections.sort(sortedCoursesList);
//    }
}
