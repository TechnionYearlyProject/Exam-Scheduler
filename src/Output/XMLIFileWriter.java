package Output;

import Logic.Day;
import Output.Exceptions.ErrorOpeningFile;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Attr;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class XMLIFileWriter implements IFileWriter {

    @Override
    public void write(String fileName, List<Day> lst) throws ErrorOpeningFile {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Schedule");
            doc.appendChild(rootElement);
            for (Day d:lst) {
                for (Integer key:d.getCoursesScheduledToTheDay()) {
                    LocalDate localDate = d.getDate();
                    Element course = doc.createElement("Course");
                    rootElement.appendChild(course);
                    // set attribute to staff element
                    Attr attr = doc.createAttribute("ID");
                    attr.setValue(Integer.toString(key));
                    course.setAttributeNode(attr);

                    Element day = doc.createElement("Day");
                    day.appendChild(doc.createTextNode(Integer.toString(localDate.getDayOfMonth())));
                    course.appendChild(day);
                    Element month = doc.createElement("Month");
                    month.appendChild(doc.createTextNode(Integer.toString(localDate.getMonthValue())));
                    course.appendChild(month);

                    // year elements
                    Element year = doc.createElement("Year");
                    year.appendChild(doc.createTextNode(Integer.toString(localDate.getYear())));
                    course.appendChild(year);

//                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                    //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                }
            }
            //DOMSource source = new DOMSource(doc);

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);
        } catch (ParserConfigurationException |TransformerException e){
            throw new ErrorOpeningFile();
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
//            write("output.xml",days);
//        } catch (ErrorOpeningFile errorOpeningFile) {
//            errorOpeningFile.printStackTrace();
//        }
//
//    }
}
