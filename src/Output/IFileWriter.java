package Output;

import Logic.CourseLoader;
import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface IFileWriter {
    void write(String fileName, List<Day> lst, CourseLoader cl) throws ErrorOpeningFile;
}
