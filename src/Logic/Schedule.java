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

    public void produceSchedule(Semester semester, ConstraintList cl, Schedule moedA) throws CanNotBeScheduledException{
        CourseLoader loader = new CourseLoader(semester, cl);
        //sort courses by number of conflicts
        List<Course> courses = loader.getSortedCourses();
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
        int index = (int)DAYS.between(moedAExamDate.plusDays(gap), moedBBeginDate);
        return index < 0 ? 0: index;
    }

    //this function assigns all courses listed in constraint list to associated days
    //If constraints of conflicted courses overlap: throws exception
    private void assignConstraints(ConstraintList constraintList, List<Course> courses) throws CanNotBeScheduledException{
        if (constraintList == null){
            return;
        }
        for (int courseId: constraintList.constraints.keySet()){
            Course course = courses.stream().filter(c -> c.getCourseID().equals(courseId)).findFirst().get();
            LocalDate dateToBeScheduled =
                    LocalDateTime.ofInstant
                            (constraintList.getConstraints(courseId).get(0).start.toInstant(),
                                    ZoneId.systemDefault()).toLocalDate();
            for (int i = 0; i < schedulable_days.size(); i++){
                Day day = schedulable_days.get(i);
                if (day.getDate().equals(dateToBeScheduled)){
                    boolean assigned = false;
                    for (int param = course.getDaysBefore()/2; param >= 0; param--){
                        try {
                            if(day.canBeAssigned(course)){
                                this.assignCourse(course, i);
                                assigned = true;
                            } else {
                                course.setDaysBefore(course.getDaysBefore() - 1);
                            }
                        } catch (IllegalDaysBefore e){
                            throw new CanNotBeScheduledException(courseId);
                        }
                        if (assigned){
                            break;
                        }
                    }
                    if (!assigned) {
                        System.out.println(course.getDaysBefore());
                        throw new CanNotBeScheduledException(courseId);
                    }
                }
            }
        }
    }

    //Produce schedule which has not overlapped conflicts
    private void produceLegalSchedule(List<Course> courses, Schedule moedA) throws CanNotBeScheduledException{
        if (this.schedulable_days.size() / 21.0 < 1){ //lul TODO: improve it
            for (Course course: courses){
                try{
                    course.setDaysBefore(course.getDaysBefore() - 1);
                } catch (IllegalDaysBefore e) {

                }

            }
        }
        for (Course course: courses){
            if (course.getGoodConstraints().size() != 0){//was getConstraints().
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

    private void optimizeSchedule(List<Course> courses, Schedule moedA){
        for (Course course: courses){
            if (course.getGoodConstraints().size() != 0){//was getConstraints().
                continue; //Courses with constraints have to be scheduled where is required by constraint
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
}
