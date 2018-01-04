package GUI.Components;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Test extends Label{
    static ArrayList<String> colors = new ArrayList<String>(Arrays.asList("#D32F2F","#7B1FA2","#303F9F","#0288D1","#00796B","#689F38","#FBC02D","#F57C00"));
    static Random randomizer = new Random();
    public Test(String name) {
        this.setTextFill(Paint.valueOf("white"));
        this.setText(name);
        this.setPadding(new Insets(0,3,0,0));
        this.setAlignment(Pos.CENTER_RIGHT);
        //this.setPadding(new Insets(2,0,0,2));
        this.setPrefWidth(73);
        this.setPrefHeight(16);
        this.setStyle("-fx-background-radius: 3 3 3 3; -fx-background-color: "+colors.get(randomizer.nextInt(colors.size())));
    }
}
