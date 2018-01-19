package Output;

import Logic.CourseLoader;
import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/*
 * @author: ucfBader
 * @date: 16/01/2018.
 * in this class we write the schedule as Calendar like file(csv format) in the following format: dayOfWeek
 *                                                                                                dateOfDay
 *                                                                                                COURSES scheduled to this day
 * the user will be able to open the final schedule as Calendar file containing all relevant data for chosen exam period.
 */
public class CalendarFileWriter implements IFileWriter{
    private static final String COMMA_DELIMITER = ",";
    private static final String[] WEEK = {"שבת","שישי","חמישי","רביעי","שלישי","שני","ראשון"};
    private String[][] examsCalendar;
    /*
     * @param: filename: the wanted filename (output.xml) as default.
     * @lst: the schedule days, each day contains list of courses scheduled in that day.
     * @cL: courseLoader class instance, in order to get courseName.
     */
    @Override
    public void write(String fileName, List<Day> lst, CourseLoader cL) throws ErrorOpeningFile {

        ArrayList<Day> fixedArray = new ArrayList<>(lst);
        fixDaysArray(fixedArray);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("מועדי א");
        LocalDate firstExam = lst.get(0).getDate();
        LocalDate lastExam = lst.get(lst.size()-1).getDate();
        long daysBetween = DAYS.between(firstExam,lastExam);
        int weeks = (int)daysBetween/6 + 1;
        examsCalendar = new String[weeks*5][7];
        examsCalendar[0] = WEEK;



        int numOfDay = 0, offset = 0;
        int row = 1;
        ArrayList<Day> weekDays = new ArrayList<>();

        for (Day d : fixedArray) {
            LocalDate lD = d.getDate();
            weekDays.add(d);
            numOfDay++;
            if (lD.getDayOfWeek() == DayOfWeek.SATURDAY) {
                printDates(weekDays, 6 - offset,row);
                row++;
                row=printExamsOfWeek(weekDays, 6 - offset, cL,row);
                row++;
                weekDays.clear();
                offset = 0;
            }
        }

        int rowNum=0;

        for (String[] week: examsCalendar){
            Row r = sheet.createRow(rowNum);
            int colNum = 0;
            for (String field: week){
                Cell cell = r.createCell(colNum);
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.RIGHT);
                if (field != null) {
                    if(rowNum == 0){
                        Font font = workbook.createFont();
                        font.setColor(IndexedColors.CORAL.getIndex());
                        style.setFont(font);
                    } else {
                        if(rowNum % 5 == 1){
                            Font font = workbook.createFont();
                            font.setColor(IndexedColors.BLUE.getIndex());
                            style.setFont(font);
                        }
                    }

                    cell.setCellValue(field);
                    style.setAlignment(HorizontalAlignment.RIGHT);//setting each cell alignment.
                    cell.setCellStyle(style);
                }
                colNum++;
            }
            rowNum++;
        }
        writeFile(fileName, workbook);
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

        while(lst.get(0).getDate().getDayOfWeek() != DayOfWeek.SUNDAY){
            LocalDate current = lst.get(0).getDate();
            LocalDate prevDay = LocalDate.of(current.getYear(),current.getMonthValue(),current.getDayOfMonth());
            lst.add(0,new Day(prevDay.minusDays(1)));
        }
        while(lst.get(lst.size()-1).getDate().getDayOfWeek() != DayOfWeek.SATURDAY){
            LocalDate current = lst.get(lst.size()-1).getDate();
            LocalDate nextDay = LocalDate.of(current.getYear(),current.getMonthValue(),current.getDayOfMonth());
            lst.add(new Day(nextDay.plusDays(1)));
        }
    }
    private int printExamsOfWeek(ArrayList<Day> weekDays,int weekStart,CourseLoader cL, int row) {

        int maxExams = 0;
        int newRow = row;
        for (Day d : weekDays) {
            if (d.getCoursesScheduledToTheDay().size() > maxExams) {
                maxExams = d.getCoursesScheduledToTheDay().size();
            }
        }
        for (int i = 0; i < maxExams; i++) {
            int col = weekStart;
            for (Day d : weekDays) {
                StringBuilder s = new StringBuilder();
                if (d.getNumOfCourses() > i) {
                    int courseID = d.getCoursesScheduledToTheDay().get(i);

                    s.append(String.format("%06d", courseID));
                    s.append(" ");
                    s.append(cL.getCourse(courseID).getCourseName());

                    examsCalendar[newRow][col] = s.toString();
                }
                col--;
            }
            newRow++;
        }
        return newRow;
    }

    private void printDates(ArrayList<Day> days,int col,int row){

        for (Day d: days){
            String sb = String.format("%02d", d.getDate().getDayOfMonth()) +
                    "/" +
                    String.format("%02d", d.getDate().getMonthValue()) +
                    "/" +
                    d.getDate().getYear();
            examsCalendar[row][col--] = sb;
        }
    }
}
