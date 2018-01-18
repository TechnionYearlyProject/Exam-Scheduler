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

public class Schedule {
    ArrayList<Day> schedulable_days;
    private int gap; //This parameter is relevant only for moed B schedule, and shows how many days should be between exams on same course
    public class CanNotBeScheduledException extends Exception{
        public CanNotBeScheduledException(int courseId){
            super(String.valueOf(courseId));
        }
    };
    private ScheduleHeuristic heuristic;
    private class ScheduleHeuristic {
        private HashMap<Pair<String, Integer>, ArrayList<Integer>> specializationMapping;
        public ScheduleHeuristic(){
            specializationMapping = new HashMap<>();
        }
        public int findIndexOfBestDayForScheduling(Course course, int beginFrom){
            int bestIndex = -1;
            int heuristicValue = schedulable_days.size() * (course.getPrograms().size() + 1);
            int currentValue = getHeuristicValue(course);
            for (int i = beginFrom; i < schedulable_days.size(); i++){
                if (!schedulable_days.get(i).canBeAssigned(course)){
                    continue;
                }
                int newHeuristicValue = currentValue;
                for (Pair<String, Integer> program: course.getPrograms()){
                    ArrayList<Integer> days = specializationMapping.get(program);
                    if (days == null){
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
                if (newHeuristicValue < heuristicValue) {
                    bestIndex = i;
                    heuristicValue = newHeuristicValue;
                }
            }
            return bestIndex;
        }

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
    public Schedule(LocalDate begin, LocalDate end, HashSet<LocalDate> occupied) throws IllegalRange {
        if (occupied == null){
            occupied = new HashSet<LocalDate>();
        }
        if (!end.isAfter(begin))
            throw new IllegalRange();
        schedulable_days = new ArrayList<Day>();
        LocalDate curr=begin;
        while (!curr.isAfter(end)) {
            if (!(curr.getDayOfWeek()==DayOfWeek.SATURDAY) && !(occupied.contains(curr)))
                schedulable_days.add(new Day(curr));
            curr=curr.plusDays(1);
        }
        this.heuristic = new ScheduleHeuristic();
    }

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

    public void assignCourse(Course course, int index) {
        int course_id = course.getCourseID();
        (schedulable_days.get(index)).insertCourse(course_id,0);
        int days_before = -1*course.getDaysBefore();
        index = index + days_before;
        if (index < 0){
            days_before += (-index);
            index = 0;
        }
        while (index < schedulable_days.size()){
            schedulable_days.get(index).insertCourse(course_id, days_before);
            days_before++;
            index++;
        }
    }

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

    public void produceSchedule(CourseLoader courseloader, ConstraintList cl, Schedule moedA) throws CanNotBeScheduledException{
        //sort courses by number of conflicts
        List<Course> courses = courseloader.getSortedCourses();
        //First need to schedule courses with constraints
        this.assignConstraints(cl, courses);
        //Now, try to produce a legal (maybe not optimized) schedule
        produceLegalSchedule(courses, moedA, cl);
        //try to optimize schedule in such way, that every day will be ~ same number of exams (do we really want it?)
        optimizeSchedule(courses, moedA, cl);
    }

    /*The function finds day where exam should be scheduled and assign the exam to the day
      Params:
        course - course to be scheduled
        uniformity - max number of exams in one day
        beginFrom - first index of schedulable_days array, where the exam can be put
    */
    private void scheduleExamFor(Course course, Integer uniformity, int beginFrom) throws CanNotBeScheduledException{
        boolean scheduled = false;
        for (int i = beginFrom; i < schedulable_days.size(); i++){
            Day day = schedulable_days.get(i);
            if (uniformity != null && day.getNumOfCourses() > uniformity){
                continue;
            }
            if (day.canBeAssigned(course)){
                unassignCourse(course);
                assignCourse(course, i);
                scheduled = true;
                break;
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
    //If constraints of conflicted courses overlap: throws exception
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

    //Produce schedule which has not overlapped conflicts
    private void produceLegalSchedule(List<Course> courses, Schedule moedA, ConstraintList cl) throws CanNotBeScheduledException{
        if (this.schedulable_days.size() / 21.0 < 1){ //lul TODO: improve it
            for (Course course: courses){
                try{
                    course.setDaysBefore(course.getDaysBefore() - 1);
                } catch (IllegalDaysBefore e) {

                }

            }
        }
        for (Course course: courses){
            if (findDateToScheduleConstraint(cl.getConstraints(course.getCourseID())) != null){
                continue;
            }
            int indexOfDayToSchedule = heuristic.findIndexOfBestDayForScheduling(course, getFirstIndexOfDayWhenCanBeScheduled(moedA, course.getCourseID()));
            if (indexOfDayToSchedule == -1){
                try {
                    course.setDaysBefore(course.getDaysBefore() - 1);
                    indexOfDayToSchedule = heuristic.findIndexOfBestDayForScheduling(course, getFirstIndexOfDayWhenCanBeScheduled(moedA, course.getCourseID()));
                } catch (IllegalDaysBefore e){
                    throw new CanNotBeScheduledException(course.getCourseID());
                }
                if (indexOfDayToSchedule == -1){
                    throw new CanNotBeScheduledException(course.getCourseID());
                }
            }
            assignCourse(course, indexOfDayToSchedule);
            heuristic.updateHeuristic(course, indexOfDayToSchedule);
        }
    }

    private void optimizeSchedule(List<Course> courses, Schedule moedA, ConstraintList cl){
        for (Course course: courses){
            if (findDateToScheduleConstraint(cl.getConstraints(course.getCourseID())) != null){
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
        for (Integer other_course:other_courses) {
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

    private LocalDate findDateToScheduleConstraint(List<Constraint> constraints){
        for (Constraint constraint: constraints){
            if (!constraint.forbidden){
                return constraint.date;
            }
        }
        return null;
    }

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
}
