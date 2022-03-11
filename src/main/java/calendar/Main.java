package calendar;

import gui.Launcher;

import holiday.Holiday;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * Created on 7/21/18.
 * extends Application
 */
public class Main extends Application{

    // *******************************
    // * CONFIGURE YOUR MANAGER HERE *
    // *******************************


    private static HolidayManager myManager() {
        // Change sample or add custom holidays:
        //List<Holiday> holidays = new ArrayList<>(HolidayStock.sampleC()); //fills the calendar with unofficial and funny holidays
        List<Holiday> holidays = new ArrayList<>(HolidayStock.sampleB()); //fills the calendar only with official holidays
        return new HolidayManager(holidays);
    }



    // *****************************************
    // * NO NEED TO CHANGE ANYTHING BELOW HERE *
    // *****************************************

    public void start(Stage primaryStage) {
            init(myManager()).show(primaryStage);
       }


    private static Launcher init(HolidayManager manager) {
        return new Launcher(month -> manager.calendarSheet(month).stream()
                .collect(groupingBy(CalendarEntry::getDate, mapping(entry -> entry.getHoliday().getName(), toList()))));
    }

    public static void main(String[] args) {
            /*
            Bei folgendem Fehler: "Error: JavaFX runtime components are missing, and are required to run this application",
            muss der nachfolgende Code in den Run-Configurations VM Optionen hinzugefügt werden.
            --module-path src/main/resources/lib --add-modules=javafx.controls,javafx.fxml
             */
            launch(args);
    }


}