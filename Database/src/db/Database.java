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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private String baseDirectory;
    private String separator;
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
        if (System.getProperty("os.name").contains("Windows")) {
            separator = "\\";
        } else {
            separator = "/";
        }
    }

    private String getSemesterDir(int year, String semester) {
        return Integer.toString(year) + '_' + semester;
    }

    private Document loadXMLFile(String filePath) {
        File XMLFile = new File(filePath);
        Document XMLTree = null;
        try {
            XMLTree = parser.parse(XMLFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            // TODO Raise something
        }
        assert XMLTree != null;
        return XMLTree;
    }

    private Semester parseSemester(String path) {
        Semester semester = new Semester();
        parseStudyPrograms(path + separator + "study_programs.xml", semester);
        parseCourses(path + separator + "courses.xml", semester);
        return semester;
    }

    private void parseStudyPrograms(String filePath, Semester semester) {
        Document XMLTree = loadXMLFile(filePath);
        Element root = XMLTree.getDocumentElement();
        NodeList programs = root.getChildNodes();
        for (int i = 0; i < programs.getLength(); i++) {
            Node n = programs.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element program = (Element) n;
                String programName = program.getTextContent();
                semester.addStudyProgram(programName);
            }
        }
    }

    private void parseCourses(String filePath, Semester semester) {
        List<String> programList = semester.getStudyProgramCollection();
        Document XMLTree = loadXMLFile(filePath);
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
                        String program = programElement.getAttribute("program");
                        if (!programList.contains(program)) {
                            // TODO Invalid database, raise something
                        }
                        String programSemester = programElement.getTextContent();
                        course.setStudyProgram(program, Integer.parseInt(programSemester));
                    }
                }
            }
        }
    }

    public Semester loadSemester(int year, String sem) {
        String semesterDir = getSemesterDir(year, sem);
        String path = baseDirectory + separator + "Database" + separator + "db" + separator + semesterDir;
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
