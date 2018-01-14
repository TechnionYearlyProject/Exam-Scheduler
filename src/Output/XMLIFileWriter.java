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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

public class XMLIFileWriter implements IFileWriter {

    @Override
    public void write(String fileName, List<Day> lst) throws ErrorOpeningFile {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
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


                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                }
            }
            DOMSource source = new DOMSource(doc);
            Writer outxml = new FileWriter(new File(fileName));
            OutputFormat format = new OutputFormat(doc);
            XMLSerializer serializer = new XMLSerializer(outxml,format);
            serializer.serialize(doc);
        } catch (ParserConfigurationException |TransformerException | IOException e){
            throw new ErrorOpeningFile();
        }
    }
}
