//package Logic;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.Date;
//import db.Course; //TODO: change to appropriate class
//import db.Database;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//import java.util.Set;
//
//public class Scheduler {
//    List<Course> courses;
//    List[] schedule; //need to think what is good structure for it
//    Map<Integer, Integer>[] constraints;
//    int examPeriodLength;
//    final int uniformity; //courses should be scheduled uniformely
//
//    @SuppressWarnings("uncheked")
//    public Scheduler(Date startDate, Date endDate, Database database){
//        this.courses = database.getCourses();
//        this.courses = courses.stream().sorted((course1, course2)->
//                course2.getConcurrentCourses().size() - course2.getConcurrentCourses().size()
//         ).collect(Collectors.toList());
//        this.examPeriodLength = (int)(TimeUnit.DAYS.convert(endDate.getTime() - startDate.getTime(),TimeUnit.MILLISECONDS));
//        this.uniformity = courses.size() / examPeriodLength;
//        this.schedule = new LinkedList[examPeriodLength];
//        this.constraints = new HashMap[examPeriodLength];
//    }
//
//
//    public List[] produceSchedule(){
//        for (Course course: courses){
//            Set<Course> courseConcurrent = course.getConcurrentCourses();
//            for (int i = 0; i < examPeriodLength; i++){
//                if (schedule[i].size() > uniformity){
//                    continue; //TODO: if in the end there is possibility to schedule only to this day, we should to shut our eyes on uniformity
//                }
//                boolean canSchedule = true;
//                for (Course concCourse: courseConcurrent){
//                    if (constraints[i].containsKey(concCourse.id)){
//                        int daysAfterExam = constraints[i].get(concCourse.id);
//                        if (daysAfterExam <= 0 || course.getPreparationInterval() > daysAfterExam){ //can't prepare to any of two courses
//                            canSchedule = false;
//                            break;
//                        }
//                    }
//                }
//                if (canSchedule){
//                    scheduleCourseToDay(i, course.id);
//                }
//            }
//        }
//        return this.schedule;
//    }
//
//    private void scheduleCourseToDay(int dayIndex, int courseId){
//        schedule[dayIndex].add(course); //TODO: change to separate schedule class?
//        int daysBeforeExam = course.getPreparationInterval();
//        while (dayIndex - daysBeforeExam < 0) { // get into array bounds
//            daysBeforeExam--;
//        }
//        while (dayIndex - daysBeforeExam < examPeriodLength){
//            constraints[dayIndex - daysBeforeExam].put(courseId, daysBeforeExam);
//            daysBeforeExam--;
//        }
//    }
//
//}
