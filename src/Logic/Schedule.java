package Logic;
import Logic.Exceptions.IllegalDaysBefore;
import Logic.Exceptions.IllegalRange;
import db.Constraint;
import db.ConstraintList;
import db.Database;
import db.Semester;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;
/*
* The class is intent for producing optimized schedule.
*/

public class Schedule {
    ArrayList<Day> schedulable_days;
    private int gap; //This parameter is relevant only for moed B schedule, and shows how many days should be between exams on same course
    public class CanNotBeScheduledException extends Exception{
        public CanNotBeScheduledException(int courseId){
            super(String.valueOf(courseId));
        }
    };
    private ScheduleHeuristic heuristic;
    /*This inner class intented for finding day for course to be assigned when legal schedule is going to be produced.
    * The search for the day is based on heuristic, which tries to maximize remaining space for all specializations*/
    private class ScheduleHeuristic {
        //In this field contained mapping between pair (specialization, semester) and list of days
        //List of days show what days are free for scheduling for the pair
        private HashMap<Pair<String, Integer>, ArrayList<Integer>> specializationMapping;

        public ScheduleHeuristic(){
            specializationMapping = new HashMap<>();
        }

        /*Find and return best index for exams of the course
        * Params:
        *   course - course to be scheduled
        *   beginFrom - index of schedulable_days that search should start from
        * Returns index of the recommended day or -1 in case there no possibility to schedule the course*/
        public int findIndexOfBestDayForScheduling(Course course, int beginFrom){
            int bestIndex = -1;
            //Initial value is maximum available (all days for all programs of the course are busy)
            int heuristicValue = schedulable_days.size() * (course.getPrograms().size() + 1);
            //Current value is heuristic value of the course as for now.
            int currentValue = getHeuristicValue(course);
            for (int i = beginFrom; i < schedulable_days.size(); i++){
                if (!schedulable_days.get(i).canBeAssigned(course)){
                    continue;
                }
                //For calculation of new heuristic value, we get current value and add num of days that are going to
                //be added to busy days, in case we assign the exam to the day
                int newHeuristicValue = currentValue;
                for (Pair<String, Integer> program: course.getPrograms()){
                    ArrayList<Integer> days = specializationMapping.get(program);
                    if (days == null){ //lazy initialization
                        days = new ArrayList<>(schedulable_days.size());
                        for (int k = 0; k < schedulable_days.size(); k++){
                            days.add(null);
                        }
                        specializationMapping.put(program, days);
                    }
                    for(int j = 0; j < course.getDaysBefore(); j++){
                        if (i - j < 0){
                            break;
                        } else {
                            if (days.get(i - j) == null) {
                                newHeuristicValue++;
                            }
                        }
                    }
                }
                //Check if heuristic value in the day is better than all previous
                if (newHeuristicValue < heuristicValue) {
                    bestIndex = i;
                    heuristicValue = newHeuristicValue;
                }
            }
            return bestIndex;
        }

        /*Calculates current heuristic value for the course*/
        private int getHeuristicValue(Course course){
            int value = 0;
            for (Pair<String, Integer> program: course.getPrograms()) {
                ArrayList<Integer> days = specializationMapping.get(program);
                if(days != null){
                    for (int i = 0; i < schedulable_days.size(); i++){
                        if (days.get(i) != null){
                            value++;
                        }
                    }
                }
            }
            return value;
        }

        /*Updates heuristic value after course is assigned.
        * Params:
        *   course - course that was assigned
        *   index - index of day where exam was assigned*/
        public void updateHeuristic(Course course, int index){
            for (Pair<String, Integer> program: course.getPrograms()) {
                ArrayList<Integer> days = specializationMapping.get(program);
                if(days == null) {
                    days = new ArrayList<>(schedulable_days.size());
                }
                for (int i = 0; i < course.getDaysBefore(); i++){
                    if (index - i < 0){
                        break;
                    }
                    days.add(index - i, 1);
                }
                specializationMapping.put(program, days);
            }
        }
    }

    /*Params:
    *   begin - first day of exams period
    *   end - last day of exams period
    *   occupied - days when impossible to schedule.
    *       All Saturdays excluded from scheduling automatically, no need to worry about them*/
    public Schedule(LocalDate begin, LocalDate end, HashSet<LocalDate> occupied) throws IllegalRange {
        if (occupied == null){
            occupied = new HashSet<>();
        }
        if (!end.isAfter(begin))
            throw new IllegalRange();
        schedulable_days = new ArrayList<>();
        LocalDate curr=begin;
        while (!curr.isAfter(end)) {
            if (!(curr.getDayOfWeek()==DayOfWeek.SATURDAY) && !(occupied.contains(curr)))
                schedulable_days.add(new Day(curr));
            curr=curr.plusDays(1);
        }
        this.heuristic = new ScheduleHeuristic();
    }

    /*Constructor for moed B schedule.
    * begin - first day of exams period
    * end - last day of exams period
    * occupied - days when impossible to schedule.
    *     All Saturdays excluded from scheduling automatically, no need to worry about them*
    * gap - number of days that should be between two exams for same course (moed A and moed B). By default: 20*/
    public Schedule(LocalDate begin, LocalDate end, HashSet<LocalDate> occupied, int gap) throws IllegalRange {
        this(begin, end, occupied);
        this.gap = gap;
    }

    public int getSize() {
        return schedulable_days.size();
    }

    public ArrayList<Day> getSchedulableDays() {
        return schedulable_days;
    }

    /*The function assigns course to day (in index of schedulable_days) and updates other days to know how much distance is between a day
    * and the day, when the course exam is scheduled*/
    public void assignCourse(Course course, int index) {
        int course_id = course.getCourseID();
        (schedulable_days.get(index)).insertCourse(course_id,0);
        int days_before = -1*course.getDaysBefore();
        index = index + days_before;
        if (index < 0){
            days_before += (-index); //We are not interested in days before exam period
            index = 0;
        }
        while (index < schedulable_days.size()){
            schedulable_days.get(index).insertCourse(course_id, days_before);
            days_before++;
            index++;
        }
    }

    /*The method returns day when exam for the course with courseId is scheduled or null if the exam isn't scheduled*/
    public Day getDayWhenScheduled(int courseId){
        for (int i = 0; i < schedulable_days.size();){
            Integer distance = schedulable_days.get(i).getDistance(courseId);
            if ( distance == null){
                i++;
            } else if (distance == 0) {
                return schedulable_days.get(i);
            } else {
                i += (-distance);
            }
        }
        return null;
    }

    /*The method schedules exams for courses in course loader.
    * Params:
    *   courseLoader - course loader for the exam period
    *   cl - constraint list for the exam period
    *   moedA - complete schedule for moed A (in case we want produce schedule for moed B). null otherwise*/
    public void produceSchedule(CourseLoader courseloader, ConstraintList cl, Schedule moedA) throws CanNotBeScheduledException{
        //sort courses by number of conflicts
        List<Course> courses = courseloader.getSortedCourses();
        //First need to schedule courses with constraints
        this.assignConstraints(cl, courses);
        //Now, try to produce a legal (maybe not optimized) schedule
        produceLegalSchedule(courses, moedA);
        //try to optimize schedule in such way, that every day will be ~ same number of exams (do we really want it?)
        optimizeSchedule(courses, moedA);
    }

    /*The function finds day where exam should be scheduled and assign the exam to the day
      Params:
        course - course to be scheduled
        uniformity - max number of exams in one day
        beginFrom - first index of schedulable_days array, where the exam can be put
    */
    private void scheduleExamFor(Course course, Integer uniformity, int beginFrom) throws CanNotBeScheduledException{
        boolean scheduled = false;
        if (!course.isLast()){
            for (int i = beginFrom; i < schedulable_days.size(); i++){
                scheduled = tryToScheduleCourseInto(i, uniformity, course);
                if(scheduled){
                    break;
                }
            }
        } else {
            for (int i = schedulable_days.size() - 1; i >= beginFrom; i--){
                scheduled = tryToScheduleCourseInto(i, uniformity, course);
                if(scheduled){
                    break;
                }
            }
        }
        if (!scheduled){
            throw new CanNotBeScheduledException(course.getCourseID());
        }
    }

    //This function finds first index of day, when course can be scheduled
    //It is useful when we want to be sure that gap between exams in two moeds is sufficient
    //if moedA schedule isn't provided, returns 0
    private int getFirstIndexOfDayWhenCanBeScheduled(Schedule moedA, int courseId){
        if (moedA == null){
            return 0;
        }
        LocalDate moedAExamDate = moedA.getDayWhenScheduled(courseId).getDate();
        LocalDate moedBBeginDate = schedulable_days.get(0).getDate();
        int index = (int)DAYS.between(moedBBeginDate, moedAExamDate.plusDays(gap));
        return index < 0 ? 0: index;
    }

    //this function assigns all courses listed in constraint list to associated days
    //If constraints of conflicted courses overlap: ignore it (user can do whatever he wants)
    private void assignConstraints(ConstraintList constraintList, List<Course> courses) {
        if (constraintList == null){
            return;
        }
        for (int courseId: constraintList.constraints.keySet()){
            Course course = courses.stream().filter(c -> c.getCourseID().equals(courseId)).findFirst().get();
            LocalDate dateToBeScheduled = findDateToScheduleConstraint(constraintList.getConstraints(courseId));
            for (int i = 0; i < schedulable_days.size(); i++){
                Day day = schedulable_days.get(i);
                if (day.getDate().equals(dateToBeScheduled)){
                    this.assignCourse(course, i);
                }
            }
        }
    }

    /*Produce schedule which has not overlapped conflicts
    * Params:
    *   courses: courses which are being scheduled
    *   moedA: complete schedule for moed A in case moed B schedule is being produced; null otherwise*/
    private void produceLegalSchedule(List<Course> courses, Schedule moedA) throws CanNotBeScheduledException{
        if (this.schedulable_days.size() / 21.0 < 1){ //lul TODO: improve it
            for (Course course: courses){
                try{
                    course.setDaysBefore(course.getDaysBefore() - 1);
                } catch (IllegalDaysBefore e) {
                    continue;
                }
            }
        }
        for (Course course: courses){
            if (findDateToScheduleConstraint(course.getConstraints()) != null){ //we can't change exam date for manually assigned courses
                continue;
            }
            int beginSearchingFromIndex = getFirstIndexOfDayWhenCanBeScheduled(moedA, course.getCourseID());
            int indexOfDayToSchedule = heuristic.findIndexOfBestDayForScheduling(course, beginSearchingFromIndex);
            while (indexOfDayToSchedule == -1){
                try {
                    course.setDaysBefore(course.getDaysBefore() - 1);
                    if (course.getDaysBefore() == 0 && course.isRequired()){ //we can't allow 2 exams of recommend course choice in same day
                        throw new CanNotBeScheduledException(course.getCourseID());
                    }
                    indexOfDayToSchedule = heuristic.findIndexOfBestDayForScheduling(course, beginSearchingFromIndex);
                } catch (IllegalDaysBefore e){
                    throw new CanNotBeScheduledException(course.getCourseID());
                }
            }
            assignCourse(course, indexOfDayToSchedule);
            heuristic.updateHeuristic(course, indexOfDayToSchedule);
        }
    }

    /*The method optimizes current schedule by uniforming num of exams in a day
    * Params:
    *   courses: courses which are being scheduled
    *   moedA: complete schedule for moed A in case moed B schedule is being produced; null otherwise*/
    private void optimizeSchedule(List<Course> courses, Schedule moedA){
        for (Course course: courses){
            if (findDateToScheduleConstraint(course.getConstraints()) != null){
                continue;
            }
            boolean scheduled = false;
            int uniformity = 1;
            int currMaxNumberOfCoursesInADay = 0;
            for (int i = 0; i < schedulable_days.size(); i++){
                int numOfCourses = schedulable_days.get(i).getNumOfCourses();
                if ( numOfCourses > currMaxNumberOfCoursesInADay){
                    currMaxNumberOfCoursesInADay = numOfCourses;
                }
            }
            int beginFrom = getFirstIndexOfDayWhenCanBeScheduled(moedA, course.getCourseID());
            while (!scheduled){
                try {
                    scheduleExamFor(course, uniformity, beginFrom);
                    scheduled = true;
                } catch (CanNotBeScheduledException e){
                    uniformity++;
                }
                if(uniformity > currMaxNumberOfCoursesInADay){ //there is no point to increase number of courses in a day
                    break;
                }
            }
        }
    }

    public void unassignCourse(Course course){
        for (int i = 0; i < schedulable_days.size(); i++){
            schedulable_days.get(i).deleteCourse(course.getCourseID());
        }
    }

    /*The method checks if it is possible to move exam for a course to another date and moves it if possible
    * Params:
    *   course: course to move
    *   new_date: date to which move the exam
    *   courseLoader: course loader for the exam period*/
    public Boolean isMovePossible(Course course, LocalDate new_date, CourseLoader courseLoader) {
        Integer days_before = course.getDaysBefore();
        Integer index = 0;
        Set<Integer> other_courses = null;
        Day day = null;
        for (Day curr_day:schedulable_days) {
            if (curr_day.date.isEqual(new_date)) {
                day = curr_day;
                other_courses = day.courses.keySet();
                break;
            }
            index++;
        }
        for (Integer other_course: other_courses) { //check that there is no conflicts collision
            Integer other_days_before = day.courses.get(other_course);
            if (other_days_before <= 0) {
                if (course.getConflictCourses().get(other_course) != null)
                    if (courseLoader.getCourse(other_course).getDaysBefore() != (-1*other_days_before))
                        return false;
            }
            else {
                if ((course.getConflictCourses().get(other_course) != null) && (other_days_before<days_before))
                    return false;
            }
        }
        this.unassignCourse(course);
        this.assignCourse(course,index);
        return true;
    }

    /*The method find date to which a course must be scheduled. Returns the date or null, if there is no such constraintr*/
    private LocalDate findDateToScheduleConstraint(List<Constraint> constraints){
        for (Constraint constraint: constraints){
            if (!constraint.forbidden){
                return constraint.date;
            }
        }
        return null;
    }

    /*Returns number of schedulable days between 2 dates*/
    public int daysBetween(LocalDate date1, LocalDate date2) {
        int index1 = -1;
        int index2 = -1;
        int index = 0;
        for (Day day:schedulable_days) {
            if (day.date.isEqual(date1))
                index1=index;
            if (day.date.isEqual(date2))
                index2=index;
            index++;
        }
        if (index1<index2)
            return index2-index1;
        return index1-index2;
    }

    private boolean tryToScheduleCourseInto(int index, Integer uniformity, Course course) {
        Day day = schedulable_days.get(index);
        if (uniformity != null && day.getNumOfCourses() > uniformity){
            return false;
        }
        if (day.canBeAssigned(course)){
            unassignCourse(course);
            assignCourse(course, index);
            return true;
        } else {
            return false;
        }
    }
}
