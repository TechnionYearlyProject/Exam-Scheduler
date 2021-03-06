package GUI.Components;
import Logic.CourseLoader;
import Logic.DBNotifier;
import db.ConstraintList;
import db.Database;
import db.Semester;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.util.HashSet;

/**
 * @author Tal
 * @date 01/01/2018
 * Connects between all GUI and Logic classes. saves all relevant data for the project.
 */

public class Manager extends HBox {
    Moed A;
    Moed B;
    CoursesTable coursetable;
    CourseLoader courseloader;
    Semester semester;
    Database db;
    ConstraintList constraintlistA;
    ConstraintList constraintlistB;
    HashSet<LocalDate> occupiedA;
    HashSet<LocalDate> occupiedB;
    LocalDate Astart;
    LocalDate Aend;
    LocalDate Bstart;
    LocalDate Bend;
    private Boolean picker_error;
    Boolean been_scheduled;
    private Wrapper wrapper;
    Integer semesterYear;
    String semesterName;
    Logic.Schedule scheduleA;
    Logic.Schedule scheduleB;
    DBNotifier dbnotifier;
    Manager(Wrapper parent) {
        try {
            db = new Database();
            semesterYear = 2017;
            semesterName = "winter_test";
            semester = db.loadSemester(semesterYear, semesterName);
            courseloader = new CourseLoader(semester,null);
            constraintlistA = new ConstraintList();
            constraintlistB = new ConstraintList();
            occupiedA = new HashSet<>();
            occupiedB = new HashSet<>();
            dbnotifier = new DBNotifier();
        }
        catch (Exception e) {
            new AlertBox(AlertType.ERROR,"שגיאה בקריאה ממסד הנתונים. התוכנית תיסגר.",null,true);
            System.exit(1);
        }
        wrapper = parent;
        been_scheduled = false;
        picker_error = true;
        coursetable = new CoursesTable(this);
        Astart = LocalDate.now();
        Aend = LocalDate.now().plusDays(35);
        Bstart = LocalDate.now().plusDays(36);
        Bend = LocalDate.now().plusDays(72);
        A = new Moed(this,"מועד א'",Astart,Aend);
        B = new Moed(this,"מועד ב'",Bstart,Bend);
        this.getChildren().addAll(B,A,coursetable);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
        A.picker1.getPicker().setOnAction(event -> {
            if (!picker_error)
                return;
            LocalDate curr = A.picker1.getPicker().getValue();
            if (datesOkay(curr,Aend,Bstart,Bend)) {
                Astart = curr;
                A.getChildren().remove(3);
                A.schedule = new Schedule(A,Astart, Aend);
                A.getChildren().add(A.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                picker_error = false;
                A.picker1.getPicker().setValue(Astart);
                picker_error = true;
            }
        });
        A.picker2.getPicker().setOnAction(event -> {
            if (!picker_error)
                return;
            LocalDate curr = A.picker2.getPicker().getValue();
            if (datesOkay(Astart,curr,Bstart,Bend)) {
                Aend = curr;
                A.getChildren().remove(3);
                A.schedule = new Schedule(A,Astart, Aend);
                A.getChildren().add(A.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                picker_error = false;
                A.picker2.getPicker().setValue(Aend);
                picker_error = true;
            }
        });
        B.picker1.getPicker().setOnAction(event -> {
            if (!picker_error)
                return;
            LocalDate curr = B.picker1.getPicker().getValue();
            if (datesOkay(Astart,Aend,curr,Bend)) {
                Bstart = curr;
                B.getChildren().remove(3);
                B.schedule = new Schedule(B,Bstart, Bend);
                B.getChildren().add(B.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                picker_error = false;
                B.picker1.getPicker().setValue(Bstart);
                picker_error = true;
            }
        });
        B.picker2.getPicker().setOnAction(event -> {
            if (!picker_error)
                return;
            LocalDate curr = B.picker2.getPicker().getValue();
            if (datesOkay(Astart,Aend,Bstart,curr)) {
                Bend = curr;
                B.getChildren().remove(3);
                B.schedule = new Schedule(B,Bstart, Bend);
                B.getChildren().add(B.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                picker_error = false;
                B.picker2.getPicker().setValue(Bend);
                picker_error = true;
            }
        });


    }

    public void cleanData() {
        been_scheduled = false;
        Astart = LocalDate.now();
        Aend = LocalDate.now().plusDays(35);
        Bstart = LocalDate.now().plusDays(36);
        Bend = LocalDate.now().plusDays(72);
        A.cleanData(Astart, Aend);
        B.cleanData(Bstart, Bend);
        constraintlistA = new ConstraintList();
        constraintlistB = new ConstraintList();
        occupiedA = new HashSet<>();
        occupiedB = new HashSet<>();
        wrapper.manager.coursetable.setScheduled(false);
        A.picker1.enable();
        A.picker2.enable();
        B.picker1.enable();
        B.picker2.enable();
    }

    private boolean datesOkay(LocalDate Astart, LocalDate Aend, LocalDate Bstart, LocalDate Bend) {
        return ((Astart.isBefore(Aend))&&(Aend.isBefore(Bstart))&&(Bstart.isBefore(Bend)));
    }

    public void blockDay(LocalDate date) {
        if (!date.isBefore(Astart) && !date.isAfter(Aend)) {
            occupiedA.add(date);
        }
        else {
            occupiedB.add(date);
        }
    }

    public void unblockDay(LocalDate date) {
        if (!date.isBefore(Astart) && !date.isAfter(Aend)) {
            occupiedA.remove(date);
        }
        else {
            occupiedB.remove(date);
        }
    }
}
