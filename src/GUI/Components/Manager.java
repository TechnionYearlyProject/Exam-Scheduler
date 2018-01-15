package GUI.Components;

import Logic.CourseLoader;
import db.ConstraintList;
import db.Database;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.util.HashSet;

public class Manager extends HBox {
    Moed A;
    Moed B;
    CoursesTable coursetable;
    CourseLoader courseloader;
    Database db;
    ConstraintList constraintlistA;
    ConstraintList constraintlistB;
    HashSet<LocalDate> occupiedA;
    HashSet<LocalDate> occupiedB;
    LocalDate Astart;
    LocalDate Aend;
    LocalDate Bstart;
    LocalDate Bend;
    Boolean flag;
    public Manager() {
        try {
            db = new Database();
            courseloader = new CourseLoader(db.loadSemester(2017, "winter_test"),null);
            constraintlistA = new ConstraintList();
            constraintlistB = new ConstraintList();
            occupiedA = new HashSet<LocalDate>();
            occupiedB = new HashSet<LocalDate>();
        }
        catch (Exception e) {
            //handle exceptions
        }
        flag = true;
        coursetable = new CoursesTable();
        Astart = LocalDate.now();
        Aend = LocalDate.now().plusDays(35);
        Bstart = LocalDate.now().plusDays(36);
        Bend = LocalDate.now().plusDays(72);
        A = new Moed("מועד א'",Astart,Aend);
        B = new Moed("מועד ב'",Bstart,Bend);
        this.getChildren().addAll(B,A,coursetable);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
        A.picker1.getPicker().setOnAction(event -> {
            if (flag == false)
                return;
            LocalDate curr = A.picker1.getPicker().getValue();
            if (datesOkay(curr,Aend,Bstart,Bend)) {
                Astart = curr;
                A.getChildren().remove(3);
                A.schedule = new Schedule(Astart, Aend);
                A.getChildren().add(A.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                flag = false;
                A.picker1.getPicker().setValue(Astart);
                flag = true;
            }
        });
        A.picker2.getPicker().setOnAction(event -> {
            if (flag == false)
                return;
            LocalDate curr = A.picker2.getPicker().getValue();
            if (datesOkay(Astart,curr,Bstart,Bend)) {
                Aend = curr;
                A.getChildren().remove(3);
                A.schedule = new Schedule(Astart, Aend);
                A.getChildren().add(A.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                flag = false;
                A.picker2.getPicker().setValue(Aend);
                flag = true;
            }
        });
        B.picker1.getPicker().setOnAction(event -> {
            if (flag == false)
                return;
            LocalDate curr = B.picker1.getPicker().getValue();
            if (datesOkay(Astart,Aend,curr,Bend)) {
                Bstart = curr;
                B.getChildren().remove(3);
                B.schedule = new Schedule(Bstart, Bend);
                B.getChildren().add(B.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                flag = false;
                B.picker1.getPicker().setValue(Bstart);
                flag = true;
            }
        });
        B.picker2.getPicker().setOnAction(event -> {
            if (flag == false)
                return;
            LocalDate curr = B.picker2.getPicker().getValue();
            if (datesOkay(Astart,Aend,Bstart,curr)) {
                Bend = curr;
                B.getChildren().remove(3);
                B.schedule = new Schedule(Bstart, Bend);
                B.getChildren().add(B.schedule);
            }
            else {
                new AlertBox(AlertType.ERROR, "טווח תאריכים לא חוקי. אנא הזינו תאריכים מחדש." ,null);
                flag = false;
                B.picker2.getPicker().setValue(Bend);
                flag = true;
            }
        });


    }

    public void cleanData() {
        this.getChildren().remove(0);
        B = new Moed("מועד ב'",LocalDate.now(),LocalDate.now().plusDays(35));
        this.getChildren().add(0, B);
        this.getChildren().remove(1);
        A = new Moed("מועד א'",LocalDate.now().plusDays(36),LocalDate.now().plusDays(72));
        this.getChildren().add(1, A);
    }

    public boolean datesOkay(LocalDate Astart,LocalDate Aend, LocalDate Bstart, LocalDate Bend) {
        return ((Astart.isBefore(Aend))&&(Aend.isBefore(Bstart))&&(Bstart.isBefore(Bend)));
    }


}
