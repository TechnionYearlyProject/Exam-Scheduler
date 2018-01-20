package Output;
import Logic.Course;
import Logic.CourseLoader;
import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.util.List;

/*
 * @author: ucfBader
 * @date: 19/01/2018.
 * in this class we write the schedule as Excel file in the following format: Date, CourseID, CourseName.
 * the user will be able to open the final schedule as Excel file containing all relevant data.
 */
public class ExcelFileWriter implements IFileWriter {
    /*
     * @param lst: the schedule days, each day contains list of courses scheduled in that day.
     * calculates number of scheduled courses.
     */


    /*
     * @param: filename: the wanted filename (output.xml) as default.
     * @lst: the schedule days, each day contains list of courses scheduled in that day.
     * @cL: courseLoader class instance, in order to get courseName.
     */
    @Override
    public void write(String fileName, List<Day> lst, CourseLoader cL) throws ErrorOpeningFile {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        int numOfExams = getNumOfDaysScheduled(lst);
        String[][] exams = new String[numOfExams+1][3];
        exams[0][0] = "תאריך";
        exams[0][1] ="מספר הקורס";
        exams[0][2] = "שם הקורס";
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);


        int row = 1;
        int col = 0;
        for (Day d: lst) {
            StringBuilder sb = new StringBuilder();
            LocalDate localDate = d.getDate();
            sb.append(String.format("%02d",localDate.getDayOfMonth()));
            sb.append("/");
            sb.append(String.format("%02d",localDate.getMonthValue()));
            sb.append("/");
            sb.append(localDate.getYear());
            for (Integer key: d.getCoursesScheduledToTheDay()) {
                col = 0;
                exams[row][col++] = sb.toString();
                exams[row][col++] = String.format("%06d",key);
                Course c = cL.getCourse(key);
                exams[row++][col] = c.getCourseName();
            }
        }

        int rowNum = 0;
        for (String[] exam: exams) {
            Row r = sheet.createRow(rowNum);
            int colNum = 0;
            for (String field : exam) {
                Cell cell = r.createCell(colNum);
                if (field != null) {
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
}
