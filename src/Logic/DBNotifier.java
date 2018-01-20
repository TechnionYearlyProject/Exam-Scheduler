package Logic;

import db.Semester;
import db.exception.CourseAlreadyExist;
import db.exception.CourseFirstAndLast;
import db.exception.CourseUnknown;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author ucfBader
 * this class is responsible for saving data to db.
 * one of our features is to allow manual changes, we need to save all changes in case the user re loads the system.
 */
public class DBNotifier {
    public void save(CourseLoader cL,Semester s){

        for (Course c:cL.getCourses().values()) {
            updateDBCourse(c,s);//update db.Course with changes.(add if not found).
        }

        for (Course c: cL.getCourses().values()) {
            Set<Integer> courseConflicts =  s.conflicts.get(c.getCourseID());
            if(courseConflicts != null) {
                courseConflicts.addAll(c.getConflictCourses().keySet());
            } else {
                if(c.getConflictCourses().size() > 0){
                    courseConflicts = new HashSet<>(c.getConflictCourses().keySet());
                    s.conflicts.put(c.getCourseID(),courseConflicts);
                }
            }
            if(courseConflicts != null) {
                for (Course re : cL.removedCourses) {
                    if(courseConflicts.contains(re.getCourseID())){
                        courseConflicts.remove(re.getCourseID());
                    }
                }
            }
        }
        for (Course c: cL.removedCourses) {
            s.removeCourse(c.getCourseID());
            s.conflicts.remove(c.getCourseID());
        }
    }

    /**
     * updating db courses according to the new data (user changes).
     * @param course the updated course, containing all up to date data.
     * @param s semester to fetch data from and update it accordingly.
     */
    private void updateDBCourse(Course course, Semester s){
        db.Course c;
        Map<String,Integer> m = new HashMap<>();
        try {
            c = s.getCourse(course.getCourseID());
        } catch (CourseUnknown courseUnknown) {
            try {//if the course does not exist
                s.addCourse(course.getCourseID(),course.getCourseName(),course.getCreditPoints(),
                        course.getDaysBefore(),course.isFirst(),course.isLast(),course.isRequired(),course.hasExam());
                try {
                    c = s.getCourse(course.getCourseID());
                } catch (CourseUnknown courseUnknown1) {
                    //will not reach here
                    c=null;
                    courseUnknown1.printStackTrace();
                }
                Set<Pair<String,Integer>> tmp = course.getPrograms();
                for (Pair<String, Integer> p: tmp) {
                    m.put(p.getKey(),p.getValue());
                }
                if (c != null) {//always
                    c.programs = m;
                }
            } catch (CourseAlreadyExist | CourseFirstAndLast courseAlreadyExist) {
                courseAlreadyExist.printStackTrace();
            }
            return;
        }
        //update db.Course.
        c.hasExam = course.hasExam();
        c.daysBefore = course.getDaysBefore();
        c.isRequired = course.isRequired();
        c.isFirst = course.isFirst();
        c.isLast = course.isLast();
        Set<Pair<String,Integer>> tmp = course.getPrograms();
        for (Pair<String, Integer> p: tmp) {
            m.put(p.getKey(),p.getValue());
        }
        c.programs = m;
    }
}
