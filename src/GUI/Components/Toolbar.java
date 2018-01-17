package GUI.Components;
import Logic.Exceptions.IllegalRange;
import Logic.Schedule;
import Output.CSVFileWriter;
import Output.CalendarFileWriter;
import Output.Exceptions.ErrorOpeningFile;
import Output.IFileWriter;
import Output.XMLFileWriter;
import db.Semester;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Toolbar extends HBox{
    Wrapper wrapper;
    CustomButton export_button;
    public Toolbar(Wrapper parent) {
        wrapper = parent;
        Text main_title = new Text("מערכת שיבוץ לוח מבחנים");
        main_title.setTextAlignment(TextAlignment.RIGHT);
        main_title.setStyle("-fx-font-size: 34pt; -fx-font-weight: bold");
        Text sub_title = new Text("הפקולטה למדעי המחשב בטכניון");
        sub_title.setTextAlignment(TextAlignment.RIGHT);
        sub_title.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold");
        VBox title_box = new VBox();
        title_box.setAlignment(Pos.TOP_RIGHT);
        title_box.getChildren().addAll(main_title, sub_title);
        title_box.setPrefWidth(956);
        CustomButton schedule_button = new CustomButton("שיבוץ", "/schedule_icon.png", this::scheduleFunction,40,160);
        schedule_button.setCircular();
        CustomButton clean_button = new CustomButton("ניקוי", "/clean_icon.png", this::cleanFunction,40,160);
        clean_button.setCircular();
        CustomButton guide_button = new CustomButton("מדריך", "/guide_icon.png", this::guideFunction,40,160);
        guide_button.setCircular();
        export_button = new CustomButton("ייצוא", "/export_icon.png", this::exportFunction,40,160);
        export_button.setCircular();
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        this.getChildren().addAll(guide_button, export_button, clean_button, schedule_button, title_box);
    }
    public void cleanFunction() {
        new AlertBox(AlertType.CONFIRM, "האם ברצונך לנקות את התוכנית?", () -> wrapper.manager.cleanData());
        for (Day day : wrapper.manager.A.schedule.days.values()){
            day.enableBlocking();
        }
        for (Day day : wrapper.manager.B.schedule.days.values()){
            day.enableBlocking();
        }
        wrapper.manager.A.picker1.enable();
        wrapper.manager.A.picker2.enable();
        wrapper.manager.B.picker1.enable();
        wrapper.manager.B.picker2.enable();
    }

    public void saveFunction() {
        // AlertBox alert = new AlertBox(AlertType.CONFIRM, "האם ברצונך לשמור את המצב הנוכחי?", ()->parent.saveAllData());
        Semester to_write = wrapper.manager.semester;
        wrapper.manager.db.saveSemester(wrapper.manager.semesterYear, wrapper.manager.semesterName);
    }

    public void guideFunction() {
        Path curr = Paths.get("");
        String s = curr.toAbsolutePath().toString() + "\\documentation\\Manual.docx";
        File file = new File(s);
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e){
            System.out.print(e);
        }
    }

    public void exportFunction() {
        export_button.setOnMouseClicked(event->{
            if(!wrapper.manager.been_scheduled){
                new AlertBox(AlertType.ERROR, "לא ניתן לייצא לפני שיבוץ.", null);
                return;
            }
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            CustomButton CSVButton = buildExportOption("יצא בתור CSV","csv",
                    "csv",new CSVFileWriter(), stage);
            CustomButton XMLButton = buildExportOption("יצא בתור XML","xml",
                    "xml",new XMLFileWriter(), stage);
            CustomButton CalendarButton = buildExportOption("יצא בתור Calendar","calendar",
                    "csv",new CalendarFileWriter(), stage);
            VBox vbox = new VBox(CSVButton,XMLButton,CalendarButton);
            vbox.setSpacing(1);
            //vbox.setStyle("-fx-background-color: transparent;");
            Scene scene = new Scene(vbox);
            stage.setScene(scene);
            stage.setX(wrapper.getScene().getWindow().getX()+195);
            stage.setY(wrapper.getScene().getWindow().getY()+95);
            stage.getIcons().add(new Image("/app_icon.png"));
            stage.focusedProperty().addListener(event2 -> {
                if (!stage.isFocused()) {
                    stage.close();
                }
            });

            stage.show();
        });
    }
    private CustomButton buildExportOption(String msg, String fileType,
                                           String fileFormat, IFileWriter writer,Stage stage){
        CustomButton button = new CustomButton(msg,null, ()->{
            try {
                writer.write(fileType + "_output."+fileFormat,wrapper.manager.scheduleA.getSchedulableDays(),wrapper.manager.courseloader);
                stage.close();
            } catch (ErrorOpeningFile errorOpeningFile) {
                new AlertBox(AlertType.ERROR, "בעיה ביצירת הקובץ - אנא בדקו שהקובץ אינו פתוח", null);
            }
        }, 40,160);
        button.setCircular();
        return button;
    }

    public void scheduleFunction(){
        if (wrapper.manager.been_scheduled) {
            new AlertBox(AlertType.INFO,"לא ניתן לשבץ על לוח קיים. לחצו ניקוי ונסו שוב.",null);
            return;
        }
        wrapper.manager.been_scheduled = true;
        new LoadingBox(()-> {
            try {
                wrapper.manager.scheduleA = new Schedule(wrapper.manager.Astart, wrapper.manager.Aend, wrapper.manager.occupiedA);
                wrapper.manager.scheduleB = new Schedule(wrapper.manager.Bstart, wrapper.manager.Bend, wrapper.manager.occupiedB);
            } catch (IllegalRange illegalRange) {
                new AlertBox(AlertType.ERROR, "טווח התאריכים אינו חוקי", null);
                return;
            }
            try {
                wrapper.manager.scheduleA.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistA, null);
                wrapper.manager.scheduleB.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistB, null);
            } catch (Logic.Schedule.CanNotBeScheduledException e) {
                new AlertBox(AlertType.ERROR, "השיבוץ נכשל. נסו להסיר העדפות " +
                        "או להגדיל את טווח התאריכים.", null);
                return;
            }
            wrapper.updateSchdule(wrapper.manager.scheduleA, wrapper.manager.scheduleB);
            wrapper.manager.coursetable.setScheduled(true);
            for (Day day : wrapper.manager.A.schedule.days.values()) {
                day.disableBlocking();
            }
            for (Day day : wrapper.manager.B.schedule.days.values()) {
                day.disableBlocking();
            }
            wrapper.manager.A.picker1.disable();
            wrapper.manager.A.picker2.disable();
            wrapper.manager.B.picker1.disable();
            wrapper.manager.B.picker2.disable();
        });
    }
}
