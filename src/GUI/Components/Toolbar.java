package GUI.Components;
import Logic.CourseLoader;
import Logic.DBNotifier;
import Logic.Exceptions.IllegalRange;
import Logic.Schedule;
import Logic.WriteScheduleToDB;
import Output.CalendarFileWriter;
import Output.ExcelFileWriter;
import Output.IFileWriter;
import Output.XMLFileWriter;
import db.Semester;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author dorbartov,roeyashkenazy
 * @date 09/01/2018
 * This class creates the title bar appearing at the top of our window, including the different buttons
 * appearing in it, used to interact with the application.
 */
public class Toolbar extends HBox{
    private Wrapper wrapper;
    private CustomButton export_button;

    /**
     * @author dorbartov
     * @date 09/01/2018
     * @param parent used to access the containing wrapper and the entire system.
     */
    Toolbar(Wrapper parent) {
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

    /**
     * @author talgelber
     */
    private void cleanFunction() {
        new AlertBox(AlertType.CONFIRM, "האם ברצונך לנקות את התוכנית?", () -> wrapper.manager.cleanData());
        for (Day day : wrapper.manager.A.schedule.days.values()){
            day.enableBlocking();
        }
        for (Day day : wrapper.manager.B.schedule.days.values()){
            day.enableBlocking();
        }
        wrapper.manager.scheduleA = null;
        wrapper.manager.scheduleB = null;
    }

    /**
     * @author dorbartov
     * @date 15/01/2018
     * the function called when pressing the guide button. opens the pdf guide written by us.
     */
    private void guideFunction() {
        Path curr = Paths.get("");
        String s = curr.toAbsolutePath().toString() + "\\documentation\\Manual.pdf";
        File file = new File(s);
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e){
            new AlertBox(AlertType.ERROR,"שגיאה בפתיחת הקובץ.",null);
        }
    }
    /**
     * @author roeyashkenazy
     * @date 15/1/2018
     * sets the stage to display the new export selection screen.
     * @param stage the stage to display
     * @param scene the scene to display
     */
    private void setStageForExport(Stage stage, Scene scene){
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(wrapper.getScene().getWindow().getX()+189);
        stage.setY(wrapper.getScene().getWindow().getY()+89);
        stage.getIcons().add(new Image("/app_icon.png"));
        stage.focusedProperty().addListener(event2 -> {
            if (!stage.isFocused()) {
                stage.close();
            }
        });
        stage.show();
    }

    /**
     * @author roeyashkenazy
     * @date 15/1/2018
     * the main export functions. Defines different types of export buttons for
     * the various formats and displays the buttons.
     */
    private void exportFunction() {
        export_button.setOnMouseClicked(event->{
            if(!wrapper.manager.been_scheduled){
                new AlertBox(AlertType.ERROR, "לא ניתן לייצא לפני שיבוץ.", null);
                return;
            }
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            CustomButton CSVButton = buildExportOption("יצא בתור Excel","excel",
                    "xlsx",new ExcelFileWriter(), stage);
            CustomButton XMLButton = buildExportOption("יצא בתור XML","xml",
                    "xml",new XMLFileWriter(), stage);
            CustomButton CalendarButton = buildExportOption("יצא בתור Calendar","calendar",
                    "xlsx",new CalendarFileWriter(), stage);
            CustomButton ImageButton = buildExportImage("יצא בתור PNG",stage);
            VBox vbox = new VBox(CSVButton,XMLButton,CalendarButton, ImageButton);
            vbox.setSpacing(1);
            vbox.setStyle("-fx-background-color: transparent;");
            Scene scene = new Scene(vbox);
            scene.setFill(javafx.scene.paint.Color.color(0,0,0,0));
            setStageForExport(stage,scene);
        });
    }

    /**
     * @author roeyashkenazy
     * @date 17/01/2018
     * generic function to build a button for a new export format (CSV,XML etc)
     * @param msg the message to be displayed on the button
     * @param fileType the type of the created file
     * @param fileFormat the format of the created file
     * @param writer the writer used by the Logic team to export
     * @param stage the stage that needs to be closed
     * @return the new export button
     */
    private CustomButton buildExportOption(String msg, String fileType, String fileFormat, IFileWriter writer,Stage stage){
        CustomButton button = new CustomButton(msg,null, ()->{
            try {
                writer.write(System.getProperty("user.home") + "\\Desktop\\" + fileType + "_Moed_A_output."+fileFormat,wrapper.manager.scheduleA.getSchedulableDays(),wrapper.manager.courseloader);
                writer.write(System.getProperty("user.home") + "\\Desktop\\" + fileType + "_Moed_B_output."+fileFormat,wrapper.manager.scheduleB.getSchedulableDays(),wrapper.manager.courseloader);
                File file_A = new File(System.getProperty("user.home") + "\\Desktop\\" + fileType + "_Moed_A_output."+fileFormat);
                File file_B = new File(System.getProperty("user.home") + "\\Desktop\\" + fileType + "_Moed_B_output."+fileFormat);
                Desktop.getDesktop().open(file_A);
                Desktop.getDesktop().open(file_B);
                stage.close();
            } catch (Exception e) {
                new AlertBox(AlertType.ERROR,"שגיאה בפתיחת הקובץ.",null);
            }
        }, 40,160);
        button.setCircular();
        return button;
    }

    /**
     * @author dorbartov
     * @date 17/01/2018
     * takes a snapshot of both moed A and B, and saves it to the desktop.
     * @param msg the message to be displayed on the button
     * @param stage the current stage
     * @return the export button
     */
    private CustomButton buildExportImage(String msg, Stage stage){
        CustomButton button = new CustomButton(msg,null, ()->{
            try {
                Image imageA = wrapper.manager.A.snapshot(new SnapshotParameters(), null);
                Image imageB = wrapper.manager.B.snapshot(new SnapshotParameters(), null);
                File fileA = new File(System.getProperty("user.home") + "\\Desktop\\MoedA_output.png");
                File fileB = new File(System.getProperty("user.home") + "\\Desktop\\MoedB_output.png");
                ImageIO.write(SwingFXUtils.fromFXImage(imageA, null),"png",fileA);
                ImageIO.write(SwingFXUtils.fromFXImage(imageB, null),"png",fileB);
                try {
                    Desktop.getDesktop().open(fileB);
                    Desktop.getDesktop().open(fileA);
                } catch (Exception e){
                    new AlertBox(AlertType.ERROR,"שגיאה בפתיחת הקובץ.",null);
                }
                stage.close();
            } catch (Exception e) {
                new AlertBox(AlertType.ERROR, "בעיה ביצירת הקובץ. אנא בדקו שהקובץ אינו פתוח.", null);
            }
        }, 40,160);
        button.setCircular();
        return button;

    }

    /**
     * @author dorbartov, roeyashkenazy
     * @date 15/1/2018
     * disables various features after the schedule button was pressed:
     * - locking the calendar (both calendars)
     * - choosing different dates for the calendar
     * - showing lock icon on days
     */
    private void shutdownScheduling(){
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
        for(Day current:wrapper.manager.A.schedule.days.values())
            current.lock_label.setVisible(false);
        for(Day current:wrapper.manager.B.schedule.days.values())
            current.lock_label.setVisible(false);
    }
    /**
     * @author dorbartov,roeyashkenazy
     * @date 14/01/2018
     * this function calls the algorithm to schedule the tests and creates the data types necessary for it.
     */
    private void scheduleFunction(){
        if (wrapper.manager.been_scheduled) {
            new AlertBox(AlertType.INFO,"לא ניתן לשבץ על לוח קיים. לחצו ניקוי ונסו שוב.",null);
            return;
        }
        new LoadingBox(()-> {
            try {
                wrapper.manager.scheduleA = new Schedule(wrapper.manager.Astart,
                        wrapper.manager.Aend, wrapper.manager.occupiedA);
                wrapper.manager.scheduleB = new Schedule(wrapper.manager.Bstart,
                        wrapper.manager.Bend, wrapper.manager.occupiedB);
            } catch (IllegalRange illegalRange) {
                new AlertBox(AlertType.ERROR, "טווח התאריכים אינו חוקי.", null);
                return;
            }
            CourseLoader loader = new CourseLoader(wrapper.manager.courseloader);
            loader.removeNoTests();
            try {
                wrapper.manager.scheduleA.produceSchedule(loader, wrapper.manager.constraintlistA,
                        null);
                wrapper.manager.scheduleB.produceSchedule(loader, wrapper.manager.constraintlistB,
                        wrapper.manager.scheduleA);
            } catch (Logic.Schedule.CanNotBeScheduledException e) {
                new AlertBox(AlertType.ERROR, "השיבוץ נכשל. נסו להסיר העדפות או להגדיל את טווח התאריכים.",
                        null);
                return;
            }
            wrapper.manager.been_scheduled = true;
            wrapper.updateSchdule(wrapper.manager.scheduleA, wrapper.manager.scheduleB);
            wrapper.manager.coursetable.setScheduled(true);
            shutdownScheduling();
        });
    }
    
}
