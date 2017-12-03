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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database {
    private String baseDirectory;
    private String separator;
    private Map<String, Semester> semesters;
    private DocumentBuilder parser;
    private SimpleDateFormat dateParser, hourParser;

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
        dateParser = new SimpleDateFormat("yyyy-MM-dd");
        hourParser = new SimpleDateFormat("HH:mm");
    }

    private String getSemesterDir(int year, String semester) {
        return Integer.toString(year) + '_' + semester;
    }

    private Document loadXMLFile(String filePath) throws InvalidDatabase {
        File XMLFile = new File(filePath);
        Document XMLTree;
        try {
            XMLTree = parser.parse(XMLFile);
        } catch (SAXException | IOException e) {
            throw new InvalidDatabase( e.toString());
        }
        assert XMLTree != null;
        return XMLTree;
    }

    private Semester parseSemester(String path) throws InvalidDatabase {
        Semester semester = new Semester();
        parseStudyPrograms(path + separator + "study_programs.xml", semester);
        parseCourses(path + separator + "courses.xml", semester);
        parseSchedules(path + separator + "scheduleA.xml", path + separator + "scheduleB.xml", semester);
        return semester;
    }

    private void parseStudyPrograms(String filePath, Semester semester) throws InvalidDatabase {
        Document XMLTree = loadXMLFile(filePath);
        Element root = XMLTree.getDocumentElement();
        NodeList programs = root.getChildNodes();
        for (int i = 0; i < programs.getLength(); i++) {
            Node n = programs.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element program = (Element) n;
                String programName = program.getTextContent();
                try {
                    semester.addStudyProgram(programName);
                } catch (StudyProgramAlreadyExist e) {
                    throw new InvalidDatabase("Duplicate study program] in database: '" + programName + "'");
                }
            }
        }
    }

    private void parseCourses(String filePath, Semester semester) throws InvalidDatabase {
        List<String> programList = semester.getStudyProgramCollection();
        Document XMLTree = loadXMLFile(filePath);
        Element root = XMLTree.getDocumentElement();
        NodeList courses = root.getChildNodes();
        for (int i = 0; i < courses.getLength(); i++) {
            Node n = courses.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element courseElement = (Element) n;
                int courseID = Integer.parseInt(courseElement.getElementsByTagName("course_id").item(0).getTextContent());
                String name = courseElement.getElementsByTagName("name").item(0).getTextContent();
                try {
                    semester.addCourse(courseID, name);
                } catch (CourseAlreadyExist e) {
                    throw new InvalidDatabase("Duplicate course in database: '" + name + "'");
                }
                NodeList programs = courseElement.getElementsByTagName("semester");
                for (int j = 0; j < programs.getLength(); j++) {
                    Node m = programs.item(j);
                    if (m.getNodeType() == Node.ELEMENT_NODE) {
                        Element programElement = (Element) m;
                        String program = programElement.getAttribute("program");
                        if (!programList.contains(program)) {
                            throw new InvalidDatabase("Course '" + name +
                                    "' contains unknown program study: '" + program + "'");
                        }
                        int semesterNum = Integer.parseInt(programElement.getTextContent());
                        try {
                            semester.registerCourse(courseID, program, semesterNum);
                        } catch (CourseUnknown | StudyProgramUnknown ignored) {} // should not happen
                    }
                }
            }
        }
    }

    private Calendar stringToDate(String dateStr, Semester.Moed moed) throws InvalidDatabase {
        if (dateStr.equals("None")) {
            return null;
        }
        try {
            Calendar date = Calendar.getInstance();
            date.setTime(dateParser.parse(dateStr));
            return date;
        } catch (ParseException e) {
            throw new InvalidDatabase("Schedule '" + moed.str + "' contains invalid date : '" + dateStr + "'");
        }
    }

    private void addHourToDate(String hourStr, Semester.Moed moed, Calendar date) throws InvalidDatabase {
        try {
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(hourParser.parse(hourStr));
            date.set(Calendar.HOUR, tmp.get(Calendar.HOUR_OF_DAY));
            date.set(Calendar.MINUTE, tmp.get(Calendar.MINUTE));
        } catch (ParseException e) {
            throw new InvalidDatabase("Schedule '" + moed.str + "' contains invalid hour : " + hourStr + "'");
        }
    }

    private void parseSchedules(String filePath1, String filePath2, Semester semester) throws InvalidDatabase {
        Map<Semester.Moed, Document> docs = new HashMap<>();
        docs.put(Semester.Moed.MOED_A, loadXMLFile(filePath1));
        docs.put(Semester.Moed.MOED_B, loadXMLFile(filePath2));
        for (Map.Entry<Semester.Moed, Document> entry: docs.entrySet()) {
            Semester.Moed moed = entry.getKey();
            Document XMLTree = entry.getValue();
            Element root = XMLTree.getDocumentElement();
            String startDateStr = root.getElementsByTagName("start_date").item(0).getTextContent();
            String endDateStr = root.getElementsByTagName("end_date").item(0).getTextContent();
            Calendar startDate = stringToDate(startDateStr, moed);
            Calendar endDate = stringToDate(endDateStr, moed);
            if (startDate != null && endDate != null) {
                try {
                    semester.setStartDate(moed, startDate);
                    semester.setEndDate(moed, endDate);
                } catch (InvalidSchedule e) {
                    throw new InvalidDatabase("Schedule '" + moed.str + "' end date is before start date");
                }
            } else {
                return;
            }
            NodeList days = root.getElementsByTagName("day");
            for (int i = 0; i < days.getLength(); i++) {
                Node n = days.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element dayElement = (Element) n;
                    String dateStr = dayElement.getAttribute("date");
                    Calendar date = stringToDate(dateStr, moed);
                    NodeList exams = dayElement.getElementsByTagName("exam");
                    for (int j = 0; j < exams.getLength(); j++) {
                        Node m = exams.item(j);
                        if (m.getNodeType() == Node.ELEMENT_NODE) {
                            Element examElement = (Element) m;
                            String hourStr = examElement.getAttribute("hour");
                            addHourToDate(hourStr, moed, date);
                            int courseId = Integer.parseInt(examElement.getTextContent());
                            try {
                                semester.scheduleCourse(courseId, moed, date);
                            } catch (UninitializedSchedule e) {
                                throw new InvalidDatabase("Start/End date missing in schedule " + moed.str);
                            } catch (CourseUnknown e) {
                                throw new InvalidDatabase("Schedule '" + moed.str + "' contain unknown course : '" +
                                        courseId + "'");
                            } catch (DateOutOfSchedule e) {
                                throw new InvalidDatabase("Course '" + courseId + "' has invalid schedule date : '" +
                                        dateStr + "' in schedule '" + moed.str + "'");
                            } catch (ScheduleDateAlreadyTaken e) {
                                throw new InvalidDatabase("Course '" + courseId + "' scheduled to an already taken date : '" +
                                        dateStr + " " + hourStr + "' in schedule '" + moed.str + "'");
                            }
                        }
                    }
                }
            }
        }
    }

    public Semester loadSemester(int year, String sem) throws InvalidDatabase {
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
