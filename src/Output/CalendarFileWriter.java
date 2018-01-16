package Output;

import Logic.CourseLoader;
import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class CalendarFileWriter implements IFileWriter{
    private static final String COMMA_DELIMITER = ",";
    private static final String WEEK = "SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY";
    //private static final String WEEK_ = "ראשון,שני,שלישי,רביעי,חמישי,שישי,שבת";
    @Override
    public void write(String fileName, List<Day> lst, CourseLoader cL) throws ErrorOpeningFile {
        ArrayList<Day> tmpLst = new ArrayList<>(lst);
        fixDaysArray(tmpLst);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        writer.println(WEEK);
        int numOfDay = 0;
        int offset = 0;
        ArrayList<Day> weekDays = new ArrayList<>();
        for (Day d : lst) {
            LocalDate lD = d.getDate();
            int currentDayOffset = offsetFromStartOfWeek(lD);
            if (numOfDay == 0) {
                offset = currentDayOffset;
                appendNCommas(sb, offset);
            }
            weekDays.add(d);
            numOfDay++;
            if (lD.getDayOfWeek() == DayOfWeek.SATURDAY) {
                printDates(weekDays, offset, sb);
                printExamsOfWeek(sb, weekDays, offset, cL);
                weekDays.clear();
                offset = 0;
                sb.append(String.format("%n"));
            } else {
                if (numOfDay == lst.size()) {
                    printDates(weekDays, offset, sb);
                    printExamsOfWeek(sb, weekDays, offset, cL);
                }
            }
        }
        writer.print(sb.toString());
        writer.close();
    }


    private void fixDaysArray(List<Day> lst){
        long days = DAYS.between(lst.get(0).getDate(), lst.get(lst.size()-1).getDate());
        for(int i =0;i<days - 1;i++){
            LocalDate current = lst.get(i).getDate();
            LocalDate next = lst.get(i+1).getDate();
            long daysBetween = DAYS.between(current, next);
            for (int j = 1;j<daysBetween;j++){
                LocalDate toAdd = LocalDate.of(current.getYear(),current.getMonthValue(),current.getDayOfMonth());
                lst.add(i+j,new Day(toAdd.plusDays(1)));
            }
        }
    }


    private int offsetFromStartOfWeek(LocalDate localDate) {
        int weekStart = 0;
        switch (localDate.getDayOfWeek()) {
            case SUNDAY:
                break;
            case MONDAY:
                weekStart = 1;
                break;
            case TUESDAY:
                weekStart = 2;
                break;
            case WEDNESDAY:
                weekStart = 3;
                break;
            case THURSDAY:
                weekStart = 4;
                break;
            case FRIDAY:
                weekStart = 5;
                break;
            case SATURDAY:
                weekStart = 6;
            default:
                break;
        }
        return weekStart;
    }

    private void printExamsOfWeek(StringBuilder s, ArrayList<Day> weekDays,int weekStart,CourseLoader cL){

        int maxExams = 0;

        for (Day d:weekDays) {
            if(d.getCoursesScheduledToTheDay().size() > maxExams){
                maxExams = d.getCoursesScheduledToTheDay().size();
            }
        }
        for(int i=0;i<maxExams;i++){
            appendNCommas(s,weekStart);
            for (Day d:weekDays) {
                if(d.getNumOfCourses() > i){
                    int courseID =d.getCoursesScheduledToTheDay().get(i);
                    s.append(Integer.toString(courseID));
                    s.append("~");
                    s.append(cL.getCourse(courseID).getCourseName());
                }
                s.append(COMMA_DELIMITER);
            }
            s.append(String.format("%n"));
        }
    }

    private void printDates(ArrayList<Day> days, int n,StringBuilder sb){
        //appendNCommas(sb,n);
        for (Day d: days){
            sb.append(d.getDate().getDayOfMonth());
            sb.append("/");
            sb.append(d.getDate().getMonthValue());
            sb.append("/");
            sb.append(d.getDate().getYear());
            sb.append(COMMA_DELIMITER);
        }
        sb.append(String.format("%n"));
    }

    private void appendNCommas(StringBuilder s,int n){
        for(int i=0;i<n;i++){
            s.append(COMMA_DELIMITER);
        }
    }

    public void simpleTest(){
        Day d1 = new Day(LocalDate.of(2018, Month.JANUARY,17));
        Day d2 = new Day(LocalDate.of(2018, Month.JANUARY,18));
        Day d3 = new Day(LocalDate.of(2018, Month.JANUARY,19));
        Day d4 = new Day(LocalDate.of(2018, Month.JANUARY,21));
        //Day d5 = new Day(LocalDate.of(2018, Month.JANUARY,22));
        Day d6 = new Day(LocalDate.of(2018, Month.JANUARY,23));

        d1.insertCourse(234123,0);
        d1.insertCourse(234124,0);
        d1.insertCourse(234125,0);
        d1.insertCourse(234123,0);
        d2.insertCourse(236124,0);
        d2.insertCourse(236125,0);
        d3.insertCourse(236123,0);
        d3.insertCourse(238124,0);
        d3.insertCourse(238125,0);
        d4.insertCourse(238126,0);
        d6.insertCourse(238127,0);
        d6.insertCourse(238128,0);

        ArrayList<Day> days = new ArrayList<>();
        days.add(d1);
        days.add(d2);
        days.add(d3);
        days.add(d4);
        //days.add(d5);
        days.add(d6);
        try {
            write("outpu.csv",days,null);
        } catch (ErrorOpeningFile errorOpeningFile) {
            errorOpeningFile.printStackTrace();
        }

    }
}
