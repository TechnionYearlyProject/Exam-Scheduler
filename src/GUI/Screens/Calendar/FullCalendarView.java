package GUI.Screens.Calendar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;


public class FullCalendarView {

    private ArrayList<AnchorPaneNode> allCalendarDays = new ArrayList<>(35);
    private VBox view;
    private Text calendarTitle;
    private YearMonth currentYearMonth;

    /**
     * Create a calendar view
     * @param yearMonth year month to create the calendar of
     */
    public FullCalendarView(YearMonth yearMonth) {
        currentYearMonth = yearMonth;
        // Create the calendar grid pane
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600, 400);
        calendar.setGridLinesVisible(true);
        // Create rows and columns with anchor panes for the calendar
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
/*
                InnerNode node1 = new InnerNode();
                InnerNode node2 = new InnerNode();
                InnerNode node3 = new InnerNode();
*/

                //VBox vbox = new VBox(node1,node2,node3);
                StackPane pane1 = new StackPane();
                pane1.setPrefSize(85,28.33);
                StackPane pane2 = new StackPane();
                pane2.setPrefSize(85,28.33);
                StackPane pane3 = new StackPane();
                pane3.setPrefSize(85,28.33);
                VBox vbox = new VBox(pane1,pane2,pane3);
                vbox.setPrefSize(200,200);
                vbox.setVisible(true);
                AnchorPaneNode ap = new AnchorPaneNode(vbox);
                //AnchorPane.setBottomAnchor(vbox,5.0);
                ap.setPrefSize(200,200);
                calendar.add(ap,j,i);
                allCalendarDays.add(ap);
            }
        }
        // Days of the week labels
        Text[] dayNames = new Text[]{ new Text("Sunday"), new Text("Monday"), new Text("Tuesday"),
                                        new Text("Wednesday"), new Text("Thursday"), new Text("Friday"),
                                        new Text("Saturday") };
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        Integer col = 0;
        for (Text txt : dayNames) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200, 10);
            AnchorPane.setBottomAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            dayLabels.add(ap, col++, 0);
        }
        // Create calendarTitle and buttons to change current month
        calendarTitle = new Text();
        Button previousMonth = new Button("<<");
        previousMonth.setOnAction(e -> previousMonth());
        Button nextMonth = new Button(">>");
        nextMonth.setOnAction(e -> nextMonth());
        HBox titleBar = new HBox(previousMonth, calendarTitle, nextMonth);
        titleBar.setAlignment(Pos.BASELINE_CENTER);
        // Populate calendar with the appropriate day numbers
        populateCalendar(yearMonth);
        // Create the calendar view
        view = new VBox(titleBar, dayLabels, calendar);
    }

    /**
     * Set the days of the calendar to correspond to the appropriate date
     * @param yearMonth year and month of month to render
     */
    private void populateCalendar(YearMonth yearMonth) {
        // Get the date we want to start with on the calendar
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        // Dial back the day until it is SUNDAY (unless the month starts on a sunday)
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            calendarDate = calendarDate.minusDays(1);
        }
        // Populate the calendar with day numbers
        for (AnchorPaneNode ap : allCalendarDays) {
            if (ap.getChildren().size() != 0) {
                ap.getChildren().remove(0);
            }
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            //InnerNode nodeBot = new InnerNode(calendarDate);

            //VBox vbox = new VBox(new InnerNode(calendarDate),
            //        new InnerNode(calendarDate),new InnerNode(calendarDate));
            //AnchorPane.setBottomAnchor(vbox,5.0);
            AnchorPane.setTopAnchor(txt, 5.0);
            AnchorPane.setLeftAnchor(txt, 5.0);

            StackPane pane1 = new StackPane();
            pane1.setPrefSize(85,28.33);
            StackPane pane2 = new StackPane();
            pane2.setPrefSize(85,28.33);
            StackPane pane3 = new StackPane();
            pane3.setPrefSize(85,28.33);
            VBox vbox = new VBox(pane1,pane2,pane3);
            vbox.setPrefSize(85,56.66);
            vbox.setVisible(true);
            AnchorPane.setBottomAnchor(vbox,5.0);

            ap.getChildren().addAll(txt,vbox);
            //ap.addDates(calendarDate);
            //ap.getView().setVisible(true);
            //StackPane pane1 = (StackPane)ap.getView().getChildren().get(0);
            //StackPane pane2 = (StackPane)ap.getView().getChildren().get(1);
            //StackPane pane3 = (StackPane)ap.getView().getChildren().get(2);
/*            ap.setOnMouseClicked(e->{
                Rectangle rect1 = new Rectangle(ap.getLayoutX(), ap.getLayoutY(), 85, 28.33);
*//*        this.getScene().getRoot().get*//*
                rect1.setFill(Color.BLUE);
                rect1.setVisible(true);
                StackPane pane = (StackPane)ap.getView().getChildren().get(0);
                pane.setPrefSize(85,28.33);
                pane.setVisible(true);
                Text text = new Text("236363");
                text.setVisible(true);
                pane.getChildren().addAll(rect1,text);
                int i = 5;
                ap.setVisible(true);
                i++;
            });*/
/*            pane1.setOnMouseClicked(e->{
                rect1.setVisible(true);
                pane1.setVisible(true);
            });*/
            //pane2.getChildren().addAll(rect1,new Text("236363"));
            //pane3.getChildren().addAll(rect1,new Text("236363"));
            //(VBox)(ap.getChildren().get(0));
            //node1.addRect(ap.getLayoutX(),ap.getLayoutY());
            //node2.addRect(ap.getLayoutX(),ap.getLayoutY());
            //node3.addRect(ap.getLayoutX(),ap.getLayoutY());

            calendarDate = calendarDate.plusDays(1);
        }
        // Change the title of the calendar
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }

    /**
     * Move the month back by one. Repopulate the calendar with the correct dates.
     */
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    /**
     * Move the month forward by one. Repopulate the calendar with the correct dates.
     */
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    public VBox getView() {
        return view;
    }

    public ArrayList<AnchorPaneNode> getAllCalendarDays() {
        return allCalendarDays;
    }

    public void setAllCalendarDays(ArrayList<AnchorPaneNode> allCalendarDays) {
        this.allCalendarDays = allCalendarDays;
    }
}
