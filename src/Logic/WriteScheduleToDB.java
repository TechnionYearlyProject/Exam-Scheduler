package Logic;

import db.Constraint;
import db.Semester;
import db.exception.*;
import javafx.util.Pair;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WriteScheduleToDB {
    public void write(Semester s,List<Day> lst, CourseLoader cL,Schedule schedule) {
        Semester.Moed sm;
        if(schedule == null){
            sm = Semester.Moed.MOED_A;
        } else {
            sm = Semester.Moed.MOED_B;
        }

        List<Course> courses = cL.getSortedCourses();
        for(Course c:courses){
            updateDBCourse(c,s);
            for (Constraint con: c.getConstraints()) {
                try {
                    s.addConstraint(c.getCourseID(),sm,con.date,con.forbidden);
                } catch (UninitializedSchedule | DateOutOfSchedule | DuplicateConstraints | CourseUnknown uninitializedSchedule) {
                    uninitializedSchedule.printStackTrace();
                }
            }
        }

        for (Course c: cL.removedCourses) {
            s.removeCourse(c.getCourseID());
        }

        for (Day d : lst) {
            LocalDate date = d.getDate();
            for (Integer id : d.getCoursesScheduledToTheDay()) {
                try {
                    s.scheduleCourse(id, sm, date);
                } catch (CourseUnknown | DateOutOfSchedule | UninitializedSchedule courseUnknown) {
                    courseUnknown.printStackTrace();
                }
            }
        }
    }

    private void updateDBCourse(Course course, Semester s){
        db.Course c;
        try {
            c = s.getCourse(course.getCourseID());
        } catch (CourseUnknown courseUnknown) {
            try {
                s.addCourse(course.getCourseID(),course.getCourseName(),course.getCreditPoints(),
                        course.getDaysBefore(),course.isFirst(),course.isLast(),course.isRequired(),course.hasExam());

            } catch (CourseAlreadyExist | CourseFirstAndLast courseAlreadyExist) {
                courseAlreadyExist.printStackTrace();
            }
            return;
        }
        c.hasExam = course.hasExam();
        c.daysBefore = course.getDaysBefore();
        c.isRequired = course.isRequired();
        c.isFirst = course.isFirst();
        c.isLast = course.isLast();
        Map<String,Integer> m = new HashMap<>();
        Set<Pair<String,Integer>> tmp = course.getPrograms();
        for (Pair<String, Integer> p: tmp) {
            m.put(p.getKey(),p.getValue());
        }
        c.programs = m;
    }
}
