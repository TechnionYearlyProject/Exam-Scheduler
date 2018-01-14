package Logic;

import db.*;
import db.Course;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CourseLoader {
    private Database db;
    private Map<Integer,Logic.Course> courses;//<courseID,Course>
    private Map<Integer,db.Course> dbCourses;
    private Semester semester;
    private ArrayList<Logic.Course> sortedCoursesList;
    public CourseLoader(Semester semester, ConstraintList cL) {
        this.semester = semester;
        courses = new HashMap<>();
        sortedCoursesList = new ArrayList<>();
        dbCourses = semester.getCourseCollection().stream().collect(Collectors.toMap(x-> x.id, x -> x));

        //building Logic CourseList.
        buildLogicCourses();
        //updating conflictList for each Course.
        setCoursesConflicts();
        //updating constraints for each course.
        if (cL != null){
            setCoursesConstraints(cL);
        }
        sortCourses();
    }

    private void setCoursesConstraints(ConstraintList cL) {
        for (Map.Entry<Integer, List<Constraint>> entry : cL.constraints.entrySet()) {
            List<Constraint> ls = entry.getValue();

            Logic.Course course = courses.get(entry.getKey());
            course.addConstraint(ls);
        }
    }

    private void setCoursesConflicts() {
        for (db.Course course: dbCourses.values()) {
            Map<String, Integer> programsForSemester = course.getPrograms();
            for (String program: programsForSemester.keySet()){
                List<Pair<Integer,String>> l =
                        semester.getCoursesByProgramAndSemester(program, programsForSemester.get(program)).
                                stream().map(a->new Pair<>(a.id,a.name)).collect(Collectors.toList());
                courses.get(course.id).addConflictCourses(l); //we iterate over db.Courses but need to add conflict
                                                              //to Logic.Course
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
            Logic.Course current =  new Logic.Course(c.name,c.id,isRequired,c.weight, c.getPrograms());
            courses.put(current.getCourseID(),current);
        }
    }

    /*
     * Logic.Course map. all the relevant data to the algorithm is here.
     */
    public Map<Integer,Logic.Course> getCourses(){
        return this.courses;
    }

    public List<Logic.Course> getSortedCourses(){
        return this.sortedCoursesList;
    }

    private void sortCourses(){
        for (Map.Entry<Integer, Logic.Course> entry: courses.entrySet()) {
            sortedCoursesList.add(new Logic.Course(entry.getValue()));
        }
        Collections.sort(sortedCoursesList);
    }
}
