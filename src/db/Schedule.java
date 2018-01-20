package db;

import db.exception.DateOutOfSchedule;
import db.exception.InvalidSchedule;
import db.exception.UninitializedSchedule;

import java.time.LocalDate;
import java.util.*;

/**
 * This class represent the output of the scheduling algorithm. It ensure no course has
 * duplicate exam date, and that exams aren't schedule outside of exam period.
 * @author Rephael Azoulay
 * @date 19/01/2018
 */
public class Schedule {
    public LocalDate start;
    public LocalDate end;
    Map<Integer, LocalDate> schedule;

    public Schedule() {
        this.start = null;
        this.end = null;
        this.schedule = new HashMap<>();
    }

    /**
     * Check if the schedule was properly initialized (and thus exams can be scheduled)
     * @return True if schedule has start and end date, false else.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public boolean undefinedStartOrEnd() {
        return start == null || end == null;
    }

    private void updateSchedules() {
        if (start != null) {
            schedule.entrySet().removeIf(entry -> entry.getValue().isBefore(start));
        }
        if (end != null) {
            schedule.entrySet().removeIf(entry -> entry.getValue().isAfter(end));
        }
    }

    /**
     * Define the starting day of the exam period. If exam were scheduled before this date, they
     * will be removed.
     * @param start The starting date of the exam period
     * @throws InvalidSchedule If the end date of the exam period is before the parameter date.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setStartDate(LocalDate start) throws InvalidSchedule {
        if (end != null && start.isAfter(end)) {
            throw new InvalidSchedule();
        }
        this.start = start;
        updateSchedules();
    }

    /**
     * Define the end day of the exam period. If exam were scheduled after this date, they
     * will be removed.
     * @param end The end date of the exam period
     * @throws InvalidSchedule If the starting date of the exam period is after the parameter date.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void setEndDate(LocalDate end) throws InvalidSchedule {
        if (start != null && end.isBefore(start)) {
            throw new InvalidSchedule();
        }
        this.end = end;
        updateSchedules();
    }

    /**
     * Set the exam day of a course.
     * @param courseId The ID of the course.
     * @param date The date at which the course will have its exam.
     * @throws DateOutOfSchedule If the date is before the starting date or after the end date of the exam period.
     * @throws UninitializedSchedule If starting or end date was not updated.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void scheduleCourse(int courseId, LocalDate date) throws DateOutOfSchedule, UninitializedSchedule {
        if (undefinedStartOrEnd()) {
            throw new UninitializedSchedule();
        }
        if (date.isBefore(start) || date.isAfter(end)) {
            throw new DateOutOfSchedule();
        }
        schedule.put(courseId, date);
    }

    /**
     * Remove the exam day of a course. Has no effect if no exam day was scheduled for this course.
     * @param id The ID of the course.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public void unscheduleCourse(int id) {
        schedule.remove(id);
    }

    /**
     * Return the exam date of a course.
     * @param courseId the ID of the course.
     * @return a LocalDate containing the date of the exam if it was defined, null else.
     * @author Rephael Azoulay
     * @date 19/01/2018
     */
    public LocalDate getCourseSchedule(int courseId) {
        if (!schedule.containsKey(courseId)) {
            return null;
        }
        return schedule.get(courseId);
    }
}
