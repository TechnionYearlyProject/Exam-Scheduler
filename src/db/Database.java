package db;

import db.exception.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Database {
    public String baseDirectory;
    public String sep;
    private Map<String, Semester> semesters;
    private DocumentBuilder builder;
    private Transformer transformer;
    private SimpleDateFormat dateParser, hourParser;

    public Database() {
        baseDirectory = System.getProperty("user.dir");
        semesters = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        TransformerFactory tFactory = TransformerFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
            transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (System.getProperty("os.name").contains("Windows")) {
            sep = "\\";
        } else {
            sep = "/";
        }
        baseDirectory = baseDirectory + sep + "db";
        dateParser = new SimpleDateFormat("yyyy-MM-dd");
        hourParser = new SimpleDateFormat("HH:mm");
    }

    public Map<Integer,Course> getCourses() {
        Map<Integer,Course> courses = new HashMap<>();
        for (Map.Entry<String, Semester> entry : semesters.entrySet()) {
            for (Course c:entry.getValue().getCourseCollection()) {
                courses.put(c.id,new Course(c));
            }
        }
        return courses;
    }

    //added by ucf.
    //mainly used for loading courses. (class CourseLoader)
    public Map<String, Semester> getSemesters(){
        return new HashMap<>(semesters);//TODO:CHECK

    }

    private String getSemesterDir(int year, String semester) {
        return Integer.toString(year) + '_' + semester;
    }

    private Document loadXMLFile(String filePath) throws InvalidDatabase {
        File XMLFile = new File(filePath);
        Document XMLTree;
        try {
            XMLTree = builder.parse(XMLFile);
        } catch (SAXException | IOException e) {
            throw new InvalidDatabase( e.toString());
        }
        assert XMLTree != null;
        return XMLTree;
    }

    private void writeXMLFile(String filePath, Document document) {
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void checkXMLFiles(Collection<String> filePaths) throws SemesterFileMissing {
        for (String filePath: filePaths) {
            File f = new File(filePath);
            if (!f.isFile()) {
                throw new SemesterFileMissing("Missing file in semester: " + filePath);
            }
        }
    }

    private Semester parseSemester(String path) throws InvalidDatabase, SemesterNotFound, SemesterFileMissing {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new SemesterNotFound();
        }
        Map<String, String> XMLFiles = new HashMap<>();
        XMLFiles.put("study_programs", path + sep + "study_programs.xml");
        XMLFiles.put("courses", path + sep + "courses.xml");
        XMLFiles.put("scheduleA", path + sep + "scheduleA.xml");
        XMLFiles.put("scheduleB", path + sep + "scheduleB.xml");
        XMLFiles.put("constraintsA", path + sep + "constraintsA.xml");
        XMLFiles.put("constraintsB", path + sep + "constraintsB.xml");
        checkXMLFiles(XMLFiles.values());
        Semester semester = new Semester();
        parseStudyPrograms(XMLFiles.get("study_programs"), semester);
        parseCourses(XMLFiles.get("courses"), semester);
        parseSchedules(XMLFiles.get("scheduleA"), XMLFiles.get("scheduleB"), semester);
        parseConstraints(XMLFiles.get("constraintsA"), XMLFiles.get("constraintsB"), semester);
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
                double weight = Double.parseDouble(courseElement.getElementsByTagName("weight").item(0).getTextContent());
                String name = courseElement.getElementsByTagName("name").item(0).getTextContent();
                try {
                    semester.addCourse(courseID, name, weight);
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
                continue;
            }
            NodeList days = root.getElementsByTagName("day");
            for (int i = 0; i < days.getLength(); i++) {
                Node n = days.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element dayElement = (Element) n;
                    String dateStr = dayElement.getAttribute("date");
                    Calendar date = stringToDate(dateStr, moed);
                    assert date != null;
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

    private void parseConstraints(String filePath1, String filePath2, Semester semester) throws InvalidDatabase {
        Map<Semester.Moed, Document> docs = new HashMap<>();
        docs.put(Semester.Moed.MOED_A, loadXMLFile(filePath1));
        docs.put(Semester.Moed.MOED_B, loadXMLFile(filePath2));
        for (Map.Entry<Semester.Moed, Document> entry: docs.entrySet()) {
            Semester.Moed moed = entry.getKey();
            Document XMLTree = entry.getValue();
            Element root = XMLTree.getDocumentElement();
            NodeList constraints = root.getElementsByTagName("constraint");
            for (int i = 0; i < constraints.getLength(); i++) {
                Node n = constraints.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element constraintElement = (Element) n;
                    int courseId = Integer.parseInt(constraintElement.getElementsByTagName("course_id").item(0).getTextContent());
                    String startDateStr = constraintElement.getElementsByTagName("start_date").item(0).getTextContent();
                    String endDateStr = constraintElement.getElementsByTagName("end_date").item(0).getTextContent();
                    Calendar startDate = stringToDate(startDateStr, moed);
                    Calendar endDate = stringToDate(endDateStr, moed);
                    try {
                        semester.addConstraint(courseId, moed, startDate, endDate);
                    } catch (InvalidConstraint e) {
                        throw new InvalidDatabase("Course '" + courseId + "' has invalid constraint date : '" +
                                startDateStr + "/" + endDateStr + "' in schedule '" + moed.str + "'");
                    } catch (UninitializedSchedule e) {
                        throw new InvalidDatabase("Start/End date missing in schedule " + moed.str);
                    } catch (CourseUnknown e) {
                        throw new InvalidDatabase("Constraint List '" + moed.str + "' contain unknown course : '" +
                                courseId + "'");
                    } catch (DateOutOfSchedule e) {
                        throw new InvalidDatabase("Course '" + courseId + "' constraint is out of the schedule dates : '" +
                                startDateStr + "/" + endDateStr + "' in schedule '" + moed.str + "'");
                    } catch (OverlappingConstraints overlappingConstraints) {
                        throw new InvalidDatabase("Course '" + courseId + "' has overlapping constraint : '" +
                                startDateStr + " - " + endDateStr + "' in schedule '" + moed.str + "'");
                    }
                }
            }
        }
    }

    private int compareDirs(String dir1, String dir2) {
        // Compare semesters so that most recent semester will be first after ordering
        String[] dir1Attr = dir1.split("_");
        String[] dir2Attr = dir2.split("_");
        // If same year
        if (dir1Attr[0].equals(dir2Attr[0])) {
            if (dir1Attr[1].equals(dir2Attr[1])) {
                return 0;
            }
            return dir1Attr[1].compareTo(dir2Attr[1]);
        }
        return -(dir1Attr[0].compareTo(dir2Attr[0]));
    }

    private void writeSemester(String path, Semester semester) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        } else if (!directory.isDirectory()) {
            // TODO Decide what to do
            System.exit(1);
        }
        writeStudyPrograms(path + sep + "study_programs.xml", semester);
        writeCourses(path + sep + "courses.xml", semester);
        writeSchedules(path + sep + "scheduleA.xml", path + sep + "scheduleB.xml", semester);
        writeConstraints(path + sep + "constraintsA.xml", path + sep + "constraintsB.xml", semester);
    }

    private void writeStudyPrograms(String filePath, Semester semester) {
        Document document = builder.newDocument();
        Element programs = document.createElement("programs");
        for (String program: semester.programs) {
            Element programElement = document.createElement("program");
            Text programText = document.createTextNode(program);
            programElement.appendChild(programText);
            programs.appendChild(programElement);
        }
        document.appendChild(programs);
        writeXMLFile(filePath, document);
    }

    private void writeCourses(String filePath, Semester semester) {
        Document document = builder.newDocument();
        Element courses = document.createElement("courses");
        for (Course course: semester.courses.values())  {
            Element courseElement = document.createElement("course");

            // Course ID node
            Element courseIdElement = document.createElement("course_id");
            Text courseIdText = document.createTextNode(Integer.toString(course.id));
            courseIdElement.appendChild(courseIdText);
            courseElement.appendChild(courseIdElement);

            // Name node
            Element courseNameElement = document.createElement("name");
            Text courseNameText = document.createTextNode(course.name);
            courseNameElement.appendChild(courseNameText);
            courseElement.appendChild(courseNameElement);

            // Weight node
            Element courseWeightElement = document.createElement("weight");
            Text courseWeightText = document.createTextNode(Double.toString(course.weight));
            courseWeightElement.appendChild(courseWeightText);
            courseElement.appendChild(courseWeightElement);

            // Study program nodes
            for (Map.Entry<String, Integer> entry: course.programs.entrySet()) {
                Element studyProgramElement = document.createElement("semester");
                studyProgramElement.setAttribute("program", entry.getKey());
                Text studyProgramText = document.createTextNode(Integer.toString(entry.getValue()));
                studyProgramElement.appendChild(studyProgramText);
                courseElement.appendChild(studyProgramElement);
            }
            courses.appendChild(courseElement);
        }
        document.appendChild(courses);
        writeXMLFile(filePath, document);
    }

    private Element createDateElement(Document doc, String name, Calendar date) {
        Element element = doc.createElement(name);
        String str;
        if (date != null) {
            str = dateParser.format(date.getTime());
        } else {
            str = "None";
        }
        Text text = doc.createTextNode(str);
        element.appendChild(text);
        return element;
    }

    private void writeSchedules(String filePath1, String filePath2, Semester semester) {
        Map<Semester.Moed, Document> docs = new HashMap<>();
        Map<Semester.Moed, String> paths = new HashMap<>();
        docs.put(Semester.Moed.MOED_A, builder.newDocument());
        docs.put(Semester.Moed.MOED_B, builder.newDocument());
        paths.put(Semester.Moed.MOED_A, filePath1);
        paths.put(Semester.Moed.MOED_B, filePath2);
        for (Map.Entry<Semester.Moed, Document> entry: docs.entrySet()) {
            Semester.Moed moed = entry.getKey();
            Document document = entry.getValue();
            Element schedule = document.createElement("schedule");

            // Start date node
            Element startDate = createDateElement(document, "start_date", semester.schedules.get(moed).start);
            schedule.appendChild(startDate);

            // End date node
            Element endDate = createDateElement(document, "end_date", semester.schedules.get(moed).end);
            schedule.appendChild(endDate);

            // Date ordering
            Map<String, Map<String, Integer>> dates = new HashMap<>();
            for (Map.Entry<Integer, Calendar> dateEntry: semester.schedules.get(moed).schedule.entrySet()){
                String dateStr = dateParser.format(dateEntry.getValue().getTime());
                String hourStr = hourParser.format(dateEntry.getValue().getTime());
                if (!dates.containsKey(dateStr)) {
                    dates.put(dateStr, new HashMap<>());
                }
                dates.get(dateStr).put(hourStr, dateEntry.getKey());
            }

            // Date nodes
            for (Map.Entry<String, Map<String, Integer>> entry1: dates.entrySet()) {
                Element dateElement = document.createElement("day");
                dateElement.setAttribute("date", entry1.getKey());
                for(Map.Entry<String, Integer> entry2: entry1.getValue().entrySet()) {
                    Element examElement = document.createElement("exam");
                    examElement.setAttribute("hour", entry2.getKey());
                    Text examText = document.createTextNode(Integer.toString(entry2.getValue()));
                    examElement.appendChild(examText);
                    dateElement.appendChild(examElement);
                }
                schedule.appendChild(dateElement);
            }
            document.appendChild(schedule);
            writeXMLFile(paths.get(moed), document);
        }
    }

    private void writeConstraints(String filePath1, String filePath2, Semester semester) {
        Map<Semester.Moed, Document> docs = new HashMap<>();
        Map<Semester.Moed, String> paths = new HashMap<>();
        docs.put(Semester.Moed.MOED_A, builder.newDocument());
        docs.put(Semester.Moed.MOED_B, builder.newDocument());
        paths.put(Semester.Moed.MOED_A, filePath1);
        paths.put(Semester.Moed.MOED_B, filePath2);
        for (Map.Entry<Semester.Moed, Document> entry: docs.entrySet()) {
            Semester.Moed moed = entry.getKey();
            Document document = entry.getValue();
            Element constraints = document.createElement("constraints");

            for (int courseId: semester.constraints.get(moed).constraints.keySet()) {
                for (Constraint constraint: semester.constraints.get(moed).constraints.get(courseId)) {
                    Element constraintElement = document.createElement("constraint");

                    Element courseIdElement = document.createElement("course_id");
                    Text courseIdText = document.createTextNode(Integer.toString(courseId));
                    courseIdElement.appendChild(courseIdText);
                    constraintElement.appendChild(courseIdElement);

                    Element startDate = createDateElement(document, "start_date", constraint.start);
                    constraintElement.appendChild(startDate);

                    Element endDate = createDateElement(document, "end_date", constraint.end);
                    constraintElement.appendChild(endDate);

                    constraints.appendChild(constraintElement);
                }
            }
            document.appendChild(constraints);
            writeXMLFile(paths.get(moed), document);
        }
    }

    public Semester createSemester(int year, String sem) throws SemesterAlreadyExist, InvalidDatabase {
        String semesterName = year + "_" + sem;
        if (semesters.containsKey(semesterName)) {
            throw new SemesterAlreadyExist();
        }
        String[] directories = new File(baseDirectory).list();
        assert directories != null;
        List<String> pathList = Arrays.stream(directories)
                .filter(dir -> new File(baseDirectory, dir).isDirectory())
                .sorted(this::compareDirs)
                .collect(Collectors.toList());
        if (pathList.contains(semesterName)) {
            throw new SemesterAlreadyExist();
        }
        Semester semester = new Semester();
        if (pathList.size() > 0) {
            Semester baseSemester = null;
            try {
                baseSemester = loadSemester(pathList.get(0));
            } catch (SemesterNotFound | SemesterFileMissing ignored) {}
            assert baseSemester != null; // Must exist since pathList contains at least one element
            List<String> programs = baseSemester.getStudyProgramCollection();
            List<Course> courses = baseSemester.getCourseCollection();
            for (String program: programs) {
                try {
                    semester.addStudyProgram(program);
                } catch (StudyProgramAlreadyExist ignored) {}
            }
            for (Course course: courses) {
                try {
                    semester.addCourse(course.id, course.name, course.weight);
                    for (String program: programs) {
                        int programSemester = course.getStudyProgramSemester(program);
                        try {
                            if (programSemester > 0) {
                                semester.registerCourse(course.id, program, programSemester);
                            }
                        } catch (StudyProgramUnknown | CourseUnknown ignored) {}
                    }
                } catch (CourseAlreadyExist ignored) {}
            }
        }
        semesters.put(semesterName, semester);
        return semester;
    }

    private Semester loadSemester(String directory) throws InvalidDatabase, SemesterNotFound, SemesterFileMissing {
        String[] dirSplit = directory.split("_");
        return loadSemester(Integer.parseInt(dirSplit[0]), dirSplit[1]);
    }

    public Semester loadSemester(int year, String sem) throws InvalidDatabase, SemesterNotFound, SemesterFileMissing {
        String semesterDir = getSemesterDir(year, sem);
        String path = baseDirectory + sep + semesterDir;
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

    public void saveSemester(int year, String sem) {
        String semesterDir = getSemesterDir(year, sem);
        String path = baseDirectory + sep + "db" + sep + semesterDir;
        Semester semester = semesters.get(semesterDir);
        writeSemester(path, semester);
    }
}
