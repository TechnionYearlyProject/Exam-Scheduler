package db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private String baseDirectory;
    private Map<String, Semester> semesters;
    private DocumentBuilder parser;

    public Database() {
        baseDirectory = System.getProperty("user.dir");
        semesters = new HashMap<>();
        System.out.println(baseDirectory);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            parser = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private String getSemesterDir(int year, String semester) {
        return Integer.toString(year) + '_' + semester;
    }

    private Semester parseSemester(String path) {
        Semester semester = new Semester();
        parseCourses(path + "\\courses.xml", semester);
        return semester;
    }

    private void parseCourses(String filePath, Semester semester) {
        File XMLFile = new File(filePath);
        Document XMLTree = null;
        try {
            XMLTree = parser.parse(XMLFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element root = XMLTree.getDocumentElement();
        NodeList courses = root.getChildNodes();
        for (int i = 0; i < courses.getLength(); i++) {
            Node n = courses.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element courseElement = (Element) n;
                String courseID = courseElement.getElementsByTagName("course_id").item(0).getTextContent();
                String name = courseElement.getElementsByTagName("name").item(0).getTextContent();
                Course course = semester.addCourse(Integer.parseInt(courseID), name);

                NodeList programs = courseElement.getElementsByTagName("semester");
                for (int j = 0; j < programs.getLength(); j++) {
                    Node m = programs.item(j);
                    if (m.getNodeType() == Node.ELEMENT_NODE) {
                        Element programElement = (Element) m;
                        String programID = programElement.getAttribute("program_id");
                        String programSemester = programElement.getTextContent();
                        course.addProgram(Integer.parseInt(programID), Integer.parseInt(programSemester));
                    }
                }
            }
        }
    }

    public Semester loadSemester(int year, String sem) {
        String semesterDir = getSemesterDir(year, sem);
        String path = baseDirectory + "\\Database\\db\\" + semesterDir;
        Semester semester = parseSemester(path);
        semesters.put(semesterDir, semester);
        return semester;
    }

    public Semester getSemester(int year, String sem) {
        String semesterDir = getSemesterDir(year, sem);
        if (!semesters.keySet().contains(semesterDir)) {
            return null;
        }
        return semesters.get(semesterDir);
    }
}
