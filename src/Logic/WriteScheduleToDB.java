package Logic;

import db.Semester;
import db.exception.CourseUnknown;
import db.exception.DateOutOfSchedule;
import db.exception.UninitializedSchedule;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WriteScheduleToDB {
    public void write(Semester s,List<Day> lst, Semester.Moed sm) {
        for (Day d : lst) {
            Date date = Date.from(d.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            for (Integer id : d.getCoursesScheduledToTheDay()) {
                try {
                    s.scheduleCourse(id, sm, calendar);
                } catch (CourseUnknown | DateOutOfSchedule | UninitializedSchedule courseUnknown) {
                    courseUnknown.printStackTrace();
                }
            }
        }
    }
}
