package Logic;

import db.Constraint;
import db.Semester;
import db.exception.*;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBNotifier {
    public void save(CourseLoader cL,Semester s){

        for (Course c:cL.getCourses().values()) {
            updateDBCourse(c,s);//update db.Course with changes.(add if not found).
        }

        for (Course c: cL.removedCourses) {
            s.removeCourse(c.getCourseID());
        }
    }

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
