package db;

import db.exception.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Database {
    public String baseDirectory;
    public String sep;
    private Map<String, Semester> semesters;
    private DocumentBuilder builder;
    private Transformer transformer;
    private Map<String, Validator> validators;
    private String XSDFilesDir;
    private DateTimeFormatter formatter;

    public Database() {
        baseDirectory = System.getProperty("user.dir");
        semesters = new HashMap<>();
        validators = new HashMap<>();
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
        XSDFilesDir = baseDirectory + sep + "src" + sep + "db" + sep + "xsd";
        baseDirectory = baseDirectory + sep + "db";
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public Map<Integer,Course> getCourses() {
        Map<Integer,Course> courses = new HashMap<>();
        for (Map.Entry<String, Semester> entry : semesters.entrySet()) {
            for (Course c:entry.getValue().getCourseCollection()) {
                courses.put(c.courseID,new Course(c));
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

    private String getXSDFileFromXML(String filename) {
        if (filename.endsWith("A.xml") || filename.endsWith("B.xml")) {
            return filename.substring(0, filename.length() - 5) + ".xsd";
        }
        return filename.substring(0, filename.length() - 4) + ".xsd";
    }

    private Validator getValidator(String path) throws InvalidDatabase {
        if (!validators.containsKey(path)) {
            File XSDFile = new File(path);
            SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                Schema schema = sFactory.newSchema(XSDFile);
                Validator validator = schema.newValidator();
                validators.put(path, validator);
            } catch (SAXException e) {
                throw new InvalidDatabase(e.toString());
            }
        }
        return validators.get(path);
    }

    private File validateXMLFile(String filePath) throws InvalidDatabase {
        File XMLFile = new File(filePath);
        String XSDFilename = getXSDFileFromXML(XMLFile.getName());
        Validator v = getValidator(XSDFilesDir + sep + XSDFilename);
        try {
            v.validate(new StreamSource(XMLFile));
        } catch (SAXException e) {
            throw new InvalidXMLFile(e.toString());
        } catch (IOException e) {
            throw new InvalidDatabase(e.toString());
        }
        return XMLFile;
    }

    private Document loadXMLFile(String filePath) throws InvalidDatabase {
        File XMLFile = validateXMLFile(filePath);
        Document XMLTree;
        try {
            XMLTree = builder.parse(XMLFile);
        } catch (SAXException | IOException e) {
            throw new InvalidDatabase(e.toString());
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
        XMLFiles.put("conflicts", path + sep + "conflicts.xml");
        checkXMLFiles(XMLFiles.values());
        Semester semester = new Semester();
        parseStudyPrograms(XMLFiles.get("study_programs"), semester);
        parseCourses(XMLFiles.get("courses"), semester);
        parseSchedules(XMLFiles.get("scheduleA"), XMLFiles.get("scheduleB"), semester);
        parseConstraints(XMLFiles.get("constraintsA"), XMLFiles.get("constraintsB"), semester);
        parseConflicts(XMLFiles.get("conflicts"), semester);
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
                semester.addStudyProgram(programName);
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
                int courseID = Integer.parseInt(courseElement.getElementsByTagName("course_id").item(0).getTextContent().trim());
                double weight = Double.parseDouble(courseElement.getElementsByTagName("credit_points").item(0).getTextContent());
                String name = courseElement.getElementsByTagName("course_name").item(0).getTextContent();
                NodeList l = courseElement.getElementsByTagName("days_before");
                if (l.getLength() == 0) {
                    semester.addCourse(courseID, name, weight);
                } else {
                    int daysBefore = Integer.parseInt(l.item(0).getTextContent());
                    boolean isFirst = courseElement.getElementsByTagName("isFirst").getLength() == 1;
                    boolean isLast = courseElement.getElementsByTagName("isLast").getLength() == 1;
                    if (isFirst && isLast) {
                        throw new CourseFirstAndLast();
                    }
                    boolean isRequired = courseElement.getElementsByTagName("isRequired").getLength() == 1;
                    boolean hasExam = courseElement.getElementsByTagName("hasExam").getLength() == 1;
                    semester.addCourse(courseID, name, weight, daysBefore, isFirst, isLast, isRequired, hasExam);
                }
                NodeList programs = courseElement.getElementsByTagName("semester");
                for (int j = 0; j < programs.getLength(); j++) {
                    Node m = programs.item(j);
                    if (m.getNodeType() == Node.ELEMENT_NODE) {
                        Element programElement = (Element) m;
                        String program = programElement.getAttribute("program");
                        if (!programList.contains(program)) {
                            throw new StudyProgramUnknown();
                        }
                        int semesterNum = Integer.parseInt(programElement.getTextContent());
                        semester.registerCourse(courseID, program, semesterNum);
                    }
                }
            }
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr.equals("None")) {
            return null;
        }
        return LocalDate.parse(dateStr);
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
            LocalDate startDate, endDate;
            try {
                startDate = parseDate(startDateStr);
                endDate = parseDate(endDateStr);
            } catch (DateTimeParseException e) {
                throw new InvalidSchedule();
            }
            if (startDate != null) {
                semester.setStartDate(moed, startDate);
            }
            if (endDate != null) {
                semester.setEndDate(moed, endDate);
            }
            NodeList days = root.getElementsByTagName("day");
            for (int i = 0; i < days.getLength(); i++) {
                Node n = days.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element dayElement = (Element) n;
                    String dateStr = dayElement.getAttribute("date");
                    LocalDate date = LocalDate.parse(dateStr);
                    NodeList exams = dayElement.getElementsByTagName("exam");
                    for (int j = 0; j < exams.getLength(); j++) {
                        Node m = exams.item(j);
                        if (m.getNodeType() == Node.ELEMENT_NODE) {
                            Element examElement = (Element) m;
                            int courseId = Integer.parseInt(examElement.getTextContent());
                            semester.scheduleCourse(courseId, moed, date);
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
                    String dateStr = constraintElement.getElementsByTagName("date").item(0).getTextContent();
                    boolean forbidden = constraintElement.getElementsByTagName("forbidden").getLength() == 1;
                    LocalDate date = parseDate(dateStr);
                    semester.addConstraint(courseId, moed, date, forbidden);
                }
            }
        }
    }

    private void parseConflicts(String filePath, Semester semester) throws InvalidDatabase {
        Document XMLTree = loadXMLFile(filePath);
        Element root = XMLTree.getDocumentElement();
        NodeList courses = root.getChildNodes();
        for (int i = 0; i < courses.getLength(); i++) {
            Node n = courses.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element conflictList = (Element) n;
                int courseId = Integer.parseInt(conflictList.getAttribute("course_id"));
                if (!semester.courses.containsKey(courseId)) {
                    throw new CourseUnknown();
                }
                if (!semester.conflicts.containsKey(courseId)) {
                    semester.conflicts.put(courseId, new HashSet<>());
                }
                NodeList conflictCourses = conflictList.getElementsByTagName("course_id");
                for (int j = 0; j < conflictCourses.getLength(); j++) {
                    Node m = conflictCourses.item(j);
                    if (m.getNodeType() == Node.ELEMENT_NODE) {
                        Element conflictCourse = (Element) m;
                        int conflictCourseId = Integer.parseInt(conflictCourse.getTextContent());
                        if (!semester.courses.containsKey(conflictCourseId)) {
                            throw new CourseUnknown();
                        }
                        semester.conflicts.get(courseId).add(conflictCourseId);
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

    private Element createDateElement(Document doc, String name, LocalDate date) {
        Element element = doc.createElement(name);
        String str;
        if (date != null) {
            str = date.format(formatter);
        } else {
            str = "None";
        }
        Text text = doc.createTextNode(str);
        element.appendChild(text);
        return element;
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
        writeConflicts(path + sep + "conflicts.xml", semester);
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
            Text courseIdText = document.createTextNode(Integer.toString(course.courseID));
            courseIdElement.appendChild(courseIdText);
            courseElement.appendChild(courseIdElement);

            // Name node
            Element courseNameElement = document.createElement("course_name");
            Text courseNameText = document.createTextNode(course.courseName);
            courseNameElement.appendChild(courseNameText);
            courseElement.appendChild(courseNameElement);

            // creditPoint node
            Element courseWeightElement = document.createElement("credit_points");
            Text courseWeightText = document.createTextNode(Double.toString(course.creditPoints));
            courseWeightElement.appendChild(courseWeightText);
            courseElement.appendChild(courseWeightElement);

            // DaysBefore node
            Element daysBeforeElement = document.createElement("days_before");
            Text daysBeforeText = document.createTextNode(Integer.toString(course.daysBefore));
            daysBeforeElement.appendChild(daysBeforeText);
            courseElement.appendChild(daysBeforeElement);

            // Flags
            if (course.isFirst) {
                Element isFirstFlag = document.createElement("isFirst");
                courseElement.appendChild(isFirstFlag);
            }
            if (course.isLast) {
                Element isLastFlag = document.createElement("isLast");
                courseElement.appendChild(isLastFlag);
            }
            if (course.isRequired) {
                Element isRequiredFlag = document.createElement("isRequired");
                courseElement.appendChild(isRequiredFlag);
            }
            if (course.hasExam) {
                Element hasExamFlag = document.createElement("hasExam");
                courseElement.appendChild(hasExamFlag);
            }

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
            Map<String, List<Integer>> dates = new HashMap<>();
            for (Map.Entry<Integer, LocalDate> dateEntry: semester.schedules.get(moed).schedule.entrySet()){
                String dateStr = dateEntry.getValue().format(formatter);
                if (!dates.containsKey(dateStr)) {
                    dates.put(dateStr, new ArrayList<>());
                }
                dates.get(dateStr).add(dateEntry.getKey());
            }

            // Date nodes
            for (Map.Entry<String, List<Integer>> entry1: dates.entrySet()) {
                Element dateElement = document.createElement("day");
                dateElement.setAttribute("date", entry1.getKey());
                for(Integer courseId: entry1.getValue()) {
                    Element examElement = document.createElement("exam");
                    Text examText = document.createTextNode(Integer.toString(courseId));
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

                    Element startDate = createDateElement(document, "date", constraint.date);
                    constraintElement.appendChild(startDate);

                    if (constraint.forbidden) {
                        Element forbiddenFlag = document.createElement("forbidden");
                        constraintElement.appendChild(forbiddenFlag);
                    }

                    constraints.appendChild(constraintElement);
                }
            }
            document.appendChild(constraints);
            writeXMLFile(paths.get(moed), document);
        }
    }

    private void writeConflicts(String filePath, Semester semester) {
        Document document = builder.newDocument();
        Element conflicts = document.createElement("conflicts");
        for (Map.Entry<Integer, Set<Integer>> entry: semester.conflicts.entrySet()) {
            Element conflictListElement = document.createElement("conflict_list");
            conflictListElement.setAttribute("course_id", entry.getKey().toString());

            for (Integer conflictCourseId : entry.getValue()) {
                Element conflictCourseElement = document.createElement("course_id");
                Text conflictCourseText = document.createTextNode(conflictCourseId.toString());
                conflictCourseElement.appendChild(conflictCourseText);
                conflictListElement.appendChild(conflictCourseElement);
            }

            conflicts.appendChild(conflictListElement);
        }
        document.appendChild(conflicts);
        writeXMLFile(filePath, document);
    }

    public Semester createSemester(int year, String sem) throws SemesterAlreadyExist {
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
            boolean invalid = false;
            try {
                baseSemester = loadSemester(pathList.get(0));
            } catch (InvalidDatabase e) {
                invalid = true;
            } catch (SemesterNotFound | SemesterFileMissing ignored) {}
            List<String> programs;
            List<Course> courses;
            if (invalid) {
                // Last semester is invalid, not importing from him
                programs = new ArrayList<>();
                courses = new ArrayList<>();
            } else {
                assert baseSemester != null;
                programs = baseSemester.getStudyProgramCollection();
                courses = baseSemester.getCourseCollection();
            }
            for (String program: programs) {
                try {
                    semester.addStudyProgram(program);
                } catch (StudyProgramAlreadyExist ignored) {}
            }
            for (Course course: courses) {
                try {
                    semester.addCourse(course.courseID, course.courseName, course.creditPoints, course.daysBefore,
                            course.isFirst, course.isLast, course.isRequired, course.hasExam);
                    for (String program: programs) {
                        int programSemester = course.getStudyProgramSemester(program);
                        try {
                            if (programSemester > 0) {
                                semester.registerCourse(course.courseID, program, programSemester);
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
        int year = Integer.parseInt(dirSplit[0]);
        dirSplit = Arrays.copyOfRange(dirSplit, 1, dirSplit.length);
        String semester = String.join("_", dirSplit);
        return loadSemester(year, semester);
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
        String path = baseDirectory + sep + semesterDir;
        Semester semester = semesters.get(semesterDir);
        writeSemester(path, semester);
    }
}
