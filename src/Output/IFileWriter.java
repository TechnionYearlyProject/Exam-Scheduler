package Output;

import Logic.CourseLoader;
import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/*
 * @author: ucfBader.
 * each file writer can implement this interface which supplies two default methods, for writing file formats.
 */
public interface IFileWriter {
    /*
     * each file writer should implement this method, defining file format.
     */
    public void write(String fileName, List<Day> lst, CourseLoader cL) throws ErrorOpeningFile;
    default int getNumOfDaysScheduled(List<Day> lst){
        int exams = 0;
        for (Day d: lst) {
            exams+=d.getNumOfCourses();
        }
        return exams;
    }
    default void writeFile(String fileName, XSSFWorkbook workbook) {
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
