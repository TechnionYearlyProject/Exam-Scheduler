package Logic;
import db.*;
import db.Course;
import javafx.util.Pair;
import java.util.*;
import java.util.stream.Collectors;

public class CourseLoader {
    private Map<Integer,Logic.Course> courses;//<courseID,Course>
    private Map<Integer,db.Course> dbCourses;
    private Semester semester;
    private ArrayList<Logic.Course> sortedCoursesList;
    public CourseLoader(Semester semester, ConstraintList cL) {
        this.semester = semester;
        courses = new HashMap<>();
        sortedCoursesList = new ArrayList<>();
        dbCourses = semester.getCourseCollection().stream().filter(c->c.hasExam).collect(Collectors.toMap(x-> x.courseID, x -> x));

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

    /**
     * @author dorbartov talgelber
     * @date 19/01/2018
     * deep copy constructor for courseloader
     * @param copied the courseloader to be copied
     */
    public CourseLoader(CourseLoader copied) {
        this.courses = new HashMap<Integer,Logic.Course>(copied.courses);
        this.dbCourses = new HashMap<Integer,db.Course>(copied.dbCourses);
        this.semester = copied.semester;
        this.sortedCoursesList = new ArrayList<Logic.Course>(copied.sortedCoursesList);
    }

    /**
     * @author dorbartov talgelber
     * @date 19/01/2018
     * remove all tests that are false in the take test checkbox
     */
    public void removeNoTests() {
        this.courses.entrySet().removeIf(e -> !(e.getValue().hasExam()));
        this.dbCourses.entrySet().removeIf(e -> !(e.getValue().hasExam));
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
                                stream().map(a->new Pair<>(a.courseID,a.courseName)).collect(Collectors.toList());
                courses.get(course.courseID).addConflictCourses(l); //we iterate over db.Courses but need to add conflict
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

            Logic.Course current =  new Logic.Course(c.courseName,c.courseID,isRequired
                    ,c.creditPoints, c.getPrograms());

            courses.put(current.getCourseID(),current);
        }
    }

    public void removeCourse(Integer courseIDToRemove){
        Logic.Course tmp = new Logic.Course("",courseIDToRemove,false,0);
        sortedCoursesList.remove(tmp);
    }

    /*
     * Logic.Course map. all the relevant data to the algorithm is here.
     */
    public Map<Integer,Logic.Course> getCourses(){
        return this.courses;
    }

    public Logic.Course getCourse(Integer id){
        return courses.get(id);
    }

    public List<Logic.Course> getSortedCourses(){
        return this.sortedCoursesList;
    }

    public void sortCourses(){
        sortedCoursesList = new ArrayList<>();
        for (Map.Entry<Integer, Logic.Course> entry: courses.entrySet()) {
            sortedCoursesList.add(new Logic.Course(entry.getValue()));
        }
        Collections.sort(sortedCoursesList);
    }

    /**
     * @author dorbartov
     * @date 18/01/2018
     * this functions removes the course with courseID from all data types in courseloader.
     * @param courseID
     */
    public void removeCourseCompletely(Integer courseID) {
        for (Logic.Course other_course:courses.values()) {
            other_course.removeConflictCourse(courseID);
        }
        courses.remove(courseID);
        dbCourses.remove(courseID);
        sortCourses();
    }

    /**
     * @author dorbartov
     * @date 18/01/2018
     * add a new course to all the data structures in the courseloader
     * @param course
     */
    public void addNewCourse(Logic.Course course) {
        Integer id = course.getCourseID();
        String name = course.getCourseName();
        for (Pair<String,Integer> programs:course.getPrograms())
            for (Logic.Course other_course:courses.values())
                if (other_course.getPrograms().contains(programs)) {
                    other_course.addConflictCourse(id,name);
                    course.addConflictCourse(other_course.getCourseID(),other_course.getCourseName());
                }
        courses.put(id,course);
        db.Course dbcourse = new db.Course(id,name,course.getCreditPoints(),course.getDaysBefore(),course.isFirst(),course.isLast(),course.isRequired(),course.hasExam());
        dbCourses.put(id,dbcourse);
        sortCourses();
    }
    
}
