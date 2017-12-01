package db;

class InvalidDatabase extends Exception {
    public InvalidDatabase(String message) {
        super(message);
    }
}

class StudyProgramAlreadyExist extends Exception {}

class StudyProgramUnknown extends Exception {}

class CourseAlreadyExist extends Exception {}

class CourseUnknown extends Exception {}

class CourseUnregistered extends Exception {}
