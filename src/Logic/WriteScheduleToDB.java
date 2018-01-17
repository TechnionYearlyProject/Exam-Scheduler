package Logic;

import db.Semester;
import db.exception.CourseUnknown;
import db.exception.DateOutOfSchedule;
import db.exception.UninitializedSchedule;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WriteScheduleToDB {
    public void write(Semester s,List<Day> lst, Semester.Moed sm) {
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
}
