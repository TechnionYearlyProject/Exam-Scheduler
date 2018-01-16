/*
package Output;

import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class CSVIFileWriter implements IFileWriter {
    private static final String COMMA_DELIMITER = ",";
    private static final String FORMAT = "Course,Date";


    public void write(String fileName, List<Day> array) throws ErrorOpeningFile {

        FileWriter f;
        try {
            f = new FileWriter(fileName);
            f.append(FORMAT);
            f.append(String.format("%n"));
            for (Day d: array) {
                LocalDate localDate = d.getDate();
                StringBuilder sb = new StringBuilder();
                sb.append(localDate.getDayOfMonth());
                sb.append("/");
                sb.append(localDate.getMonthValue());
                sb.append("/");
                sb.append(localDate.getYear());
                for (Integer key: d.getCoursesScheduledToTheDay()) {
                    f.append(Integer.toString(key));
                    f.append(COMMA_DELIMITER);
                    f.append(sb.toString());
                    f.append(String.format("%n"));
                }
            }
        } catch (IOException e) {
            throw new ErrorOpeningFile();
        }
        try {
            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void simpleTest(){
//        Day d1 = new Day(LocalDate.of(2018, Month.JANUARY,17));
//        Day d2 = new Day(LocalDate.of(2018, Month.JANUARY,18));
//        Day d3 = new Day(LocalDate.of(2018, Month.JANUARY,19));
//        d1.insertCourse(234123,0);
//        d1.insertCourse(234124,0);
//        d1.insertCourse(234125,0);
//        d1.insertCourse(234123,0);
//        d2.insertCourse(236124,0);
//        d2.insertCourse(236125,0);
//        d3.insertCourse(236123,0);
//        d3.insertCourse(238124,0);
//        d3.insertCourse(238125,0);
//        ArrayList<Day> days = new ArrayList<>();
//        days.add(d1);
//        days.add(d2);
//        days.add(d3);
//        try {
//            write("output.csv",days);
//        } catch (ErrorOpeningFile errorOpeningFile) {
//            errorOpeningFile.printStackTrace();
//        }
//
//    }
}
*/
package Output;

import Logic.Course;
import Logic.CourseLoader;
import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class CSVFileWriter implements IFileWriter {
    private static final String COMMA_DELIMITER = ",";
    private static final String FORMAT = "CourseID,CourseName,Date";


    public void write(String fileName, List<Day> array, CourseLoader cL) throws ErrorOpeningFile {
        OutputStreamWriter f =null;
        try {
            f = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //FileWriter f;
        try {
            //f = new FileWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
           // f = new FileWriter(new OutputStreamWriter());
            //f.
            f.append(FORMAT);
            f.append(String.format("%n"));
            for (Day d: array) {
                LocalDate localDate = d.getDate();
                StringBuilder sb = new StringBuilder();
                sb.append(localDate.getDayOfMonth());
                sb.append("/");
                sb.append(localDate.getMonthValue());
                sb.append("/");
                sb.append(localDate.getYear());
                for (Integer key: d.getCoursesScheduledToTheDay()) {
                    f.append(Integer.toString(key));
                    f.append(COMMA_DELIMITER);
                    Course c = cL.getCourse(key);
                    f.append(c.getCourseName());
                    f.append(COMMA_DELIMITER);
                    f.append(sb.toString());
                    f.append(String.format("%n"));
                }
            }
        } catch (IOException e) {
            throw new ErrorOpeningFile();
        }
        try {
            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simpleTest(){
        Day d1 = new Day(LocalDate.of(2018, Month.JANUARY,17));
        Day d2 = new Day(LocalDate.of(2018, Month.JANUARY,18));
        Day d3 = new Day(LocalDate.of(2018, Month.JANUARY,19));
        d1.insertCourse(234123,0);
        d1.insertCourse(234124,0);
        d1.insertCourse(234125,0);
        d1.insertCourse(234123,0);
        d2.insertCourse(236124,0);
        d2.insertCourse(236125,0);
        d3.insertCourse(236123,0);
        d3.insertCourse(238124,0);
        d3.insertCourse(238125,0);
        ArrayList<Day> days = new ArrayList<>();
        days.add(d1);
        days.add(d2);
        days.add(d3);
        try {
            write("output.csv",days,null);
        } catch (ErrorOpeningFile errorOpeningFile) {
            errorOpeningFile.printStackTrace();
        }

    }
}
