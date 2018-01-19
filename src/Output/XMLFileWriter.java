package Output;

import Logic.CourseLoader;
import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.util.List;


/*
 * @author: ucfBader
 * @date: 16/01/2018.
 * in this class we write the schedule as xml file in the following format: <Course "id" "name"><day><month><year>
 * the user will be able to open the final schedule as xml file containing all relevant data for chosen exam period.
 */
public class XMLFileWriter implements IFileWriter {

    /*
     * @param: filename: the wanted filename (output.xml) as default.
     * @lst: the schedule days, each day contains list of courses scheduled in that day.
     * @cL: courseLoader class instance, in order to get courseName.
     */
    @Override
    public void write(String fileName, List<Day> lst, CourseLoader cL) throws ErrorOpeningFile {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Schedule");
            doc.appendChild(rootElement);
            for (Day d:lst) {
                for (Integer key:d.getCoursesScheduledToTheDay()) {
                    LocalDate localDate = d.getDate();
                    Element course = doc.createElement("Course");
                    rootElement.appendChild(course);
                    course.setAttribute("ID", String.format("%02d",key));
                    course.setAttribute("Name", cL.getCourse(key).getCourseName());
                    Element day = doc.createElement("Day");
                    day.appendChild(doc.createTextNode(String.format("%02d",localDate.getDayOfMonth())));
                    course.appendChild(day);
                    Element month = doc.createElement("Month");
                    month.appendChild(doc.createTextNode(String.format("%02d",localDate.getMonthValue())));
                    course.appendChild(month);

                    // year elements
                    Element year = doc.createElement("Year");
                    year.appendChild(doc.createTextNode(Integer.toString(localDate.getYear())));
                    course.appendChild(year);
                }
            }

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);
        } catch (ParserConfigurationException |TransformerException e){
            throw new ErrorOpeningFile();
        }
    }
}
