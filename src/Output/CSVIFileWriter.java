package Output;

import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVIFileWriter implements IFileWriter {
    private static final String COMMA_DELIMITER = ",";
    //private static final String FORMAT = "Course,Date";
    //private static final int INDEX = FORMAT.indexOf('D');

    public void write(String fileName, List<Day> array) throws ErrorOpeningFile {

        FileWriter f;
        try {
            f = new FileWriter(fileName);
//            f.append(FORMAT);
//            f.append(String.format("%n"));
            for (Day d: array) {
                LocalDate localDate = d.getDate();
                StringBuilder sb = new StringBuilder();
                sb.append(localDate.getDayOfMonth());
                sb.append("-");
                sb.append(localDate.getMonthValue());
                sb.append("-");
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
}
