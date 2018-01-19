package db.exception;

public class InvalidDatabase extends Exception {
    public InvalidDatabase() {}

    public InvalidDatabase(String message) {
        super(message);
    }
}
