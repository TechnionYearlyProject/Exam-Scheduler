package Output;

import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface IFileWriter {
    public void write(String fileName, List<Day> lst) throws ErrorOpeningFile;
}
