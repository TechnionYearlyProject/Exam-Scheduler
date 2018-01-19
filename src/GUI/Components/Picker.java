package GUI.Components;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Tal
 * @date 18/12/2017
 * Custom design for the DatePicker
 */

public class Picker extends HBox{
    LocalDate date;
    DatePicker picker;
    public Picker(String title) {
        this.setAlignment(Pos.TOP_RIGHT);
        Label label = new Label(title);
        label.setPrefWidth(140);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 13pt");
        picker = new DatePicker();
        picker.getStylesheets().add("/metro.css");
        picker.setConverter(new StringConverter<LocalDate>() {
            @Override public String toString(LocalDate date) {
                String pattern = "dd/MM/yyyy";
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
                {
                    picker.setPromptText(pattern.toLowerCase());
                }
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override public LocalDate fromString(String string) {
                String pattern = "d/M/yy";
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
                {
                    picker.setPromptText(pattern.toLowerCase());
                }
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        this.getChildren().addAll(picker,label);
    }

    public void disable(){
        picker.setDisable(true);
    }
    public void enable(){
        picker.setDisable(false);
    }
    public LocalDate getDate() {
        return date;
    }

    public DatePicker getPicker() {
        return picker;
    }

    public void setDate(LocalDate new_date) {
        date = new_date;
    }
}
