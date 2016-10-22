package assignment_one;
/**
 * This application allows the user to calculate range, max height and flight time for a projectile
 * following the entering of mass (currently not used), angle of launch, and initial speed.
 * There is error checking to ensure that suitable values are entered.
 */

/*
    Options :
        - Can only write integer or double in mass_textField
        - Button color with css.
        - Add chart
        - I add more layout to make a better view
        - Add a minimum size for the window.
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;


public class Projectile extends Application {

    private final String TITLE = "Projectile";
    private final String HUMAN = "Adult Human";
    private final String PIANO = "Piano";

    private Label resultLabel = new Label("Results : ");

    //Chart
    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();
    private LineChart<Number,Number> chart = new LineChart<Number,Number>(xAxis,yAxis);
    private XYChart.Series series = new XYChart.Series();

    //Layouts
    private HBox mainLayout = new HBox();
    private GridPane gp = new GridPane();
    private HBox hbox_speed = new HBox();
    private VBox results = new VBox();
    private GridPane result = new GridPane();

    //Projectile Type
    private Label projectile_type_label = new Label("Projectile Type");
    private ComboBox<String> projectile_type_combobox = new ComboBox<String>();

    // Mass
    private Label mass_label = new Label("Mass (kgs)");
    private TextField mass_textField = new TextField("80");
    private double mass = 80.0;

    //Angle
    private Label angle_label = new Label("Angle (degrees)");
    private Slider angle_slider = new Slider(0, 90, 45);
    private TextField angle_textField = new TextField("45.0");
    private double angle = 45.0;
    private double angle_rad;
    //Formating the values in the duration box
    DecimalFormat df = new DecimalFormat("###,###.###");

    //Initial Speed
    private Label initial_speed_label = new Label("Initial Speed (m/s)");
    private ToggleGroup initial_speed_toggleGroup = new ToggleGroup();
    private RadioButton initial_speed_slow = new RadioButton("slow");
    private RadioButton initial_speed_medium = new RadioButton("medium");
    private RadioButton initial_speed_fast = new RadioButton("fast");
    private TextField intitial_speed_textField = new TextField("100");
    private double initial_speed = 100.0;

    //Range
    private Label range_label = new Label("Range (m)");
    private TextField range_textField = new TextField("");
    private double range = 0.0;

    //Height
    private Label height_label = new Label("Max Height (m)");
    private TextField height_textField = new TextField("");
    private double height = 0.0;

    //Time
    private Label time_label = new Label("Time (s)");
    private TextField time_textField = new TextField("");
    private double time = 0.0;

    //Gravity
    private static final double gravitational_accelleration = 9.81; // m/s/s

    //Calculate
    private Button fire_button = new Button("Fire");
    private Button erase_button = new Button("Erase");

    @Override
    public void init() {
        //init combobox
        projectile_type_combobox.getItems().addAll(HUMAN, PIANO);
        projectile_type_combobox.getSelectionModel().select(0);
        projectile_type_combobox.setMaxWidth(Double.MAX_VALUE);

        //init radio buttons
        initial_speed_slow.setUserData("10");
        initial_speed_medium.setUserData("50");
        initial_speed_fast.setUserData("100");
        initial_speed_slow.setToggleGroup(initial_speed_toggleGroup);
        initial_speed_medium.setToggleGroup(initial_speed_toggleGroup);
        initial_speed_fast.setToggleGroup(initial_speed_toggleGroup);
        hbox_speed.getChildren().addAll(initial_speed_slow, initial_speed_medium, initial_speed_fast);

        //init slider
        angle_slider.setShowTickLabels(true);
        angle_slider.setShowTickMarks(true);

        //init textfields
        mass_textField.setEditable(true);
        angle_textField.setEditable(false);
        intitial_speed_textField.setEditable(false);
        range_textField.setEditable(false);
        height_textField.setEditable(false);
        time_textField.setEditable(false);

        //button color and sizes
        erase_button.setStyle("-fx-background-color: linear-gradient(#ff5940, #ff0c20);" + "-fx-text-fill: #530907;");
        erase_button.setMaxWidth(Double.MAX_VALUE);
        fire_button.setStyle("-fx-background-color: linear-gradient(#f0ff35, #a9ff00);" + "-fx-text-fill: #395306;");
        fire_button.setMaxWidth(Double.MAX_VALUE);

        /*
            INITIALISE LAYOUTS
         */

        //Set column constraints
        ColumnConstraints col1Constraints = new ColumnConstraints();
        col1Constraints.setPercentWidth(50);
        ColumnConstraints col2Constraints = new ColumnConstraints();
        col1Constraints.setPercentWidth(50);

        //Init GridPane for setting
        gp.addRow(0, projectile_type_label, projectile_type_combobox);
        gp.addRow(1, mass_label, mass_textField);
        gp.addRow(2, angle_label, angle_textField);
        gp.add(angle_slider, 0, 3, 2, 1);
        gp.addRow(4, initial_speed_label, intitial_speed_textField);
        gp.add(hbox_speed, 0, 5, 2, 1);
        gp.addRow(6, fire_button, erase_button);
        gp.getColumnConstraints().addAll(col1Constraints, col2Constraints);

        // init gridpane for results
        result.addRow(0, resultLabel);
        result.addRow(1, range_label, range_textField);
        result.addRow(2, height_label, height_textField);
        result.addRow(3, time_label, time_textField);
        result.getColumnConstraints().addAll(col1Constraints, col2Constraints);

        // init VBox for all results (texfields and chart)
        results.getChildren().addAll(result, chart);

        // init HBOX containing settings and results
        mainLayout.getChildren().addAll(gp, results);
        mainLayout.setSpacing(15);
        mainLayout.setPadding(new Insets(10, 10, 10, 10));

        //init chart
        chart.setCreateSymbols(false);  // To display a line rather than points
        chart.setTitle("Projectile's trajectory");
        chart.setLegendVisible(false);  // To remove the points legend
        chart.getData().addAll(series); // Add the series of values
        chart.setAnimated(false);   // Remove the animation : can be source of bugs
        xAxis.setLabel("Range (m)");
        yAxis.setLabel("Height (m)");
        xAxis.setAutoRanging(false);    // In this case, can be source of weird results so I remove it
        yAxis.setAutoRanging(false);    // In this case, can be source of weird results so I remove it
        xAxis.setLowerBound(0.0);
        yAxis.setLowerBound(0.0);


        initalizeControlValues();

        //  Listener for angle Slider to set angle TextTield and the angle variable
        angle_slider.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue){
                angle_textField.setText(String.valueOf(Math.round( (double)newValue * 100.0 ) / 100.0));
                fire();
                drawChart();
            }
        });

        // Listener for inital_speed ToggleGroup to set initital_speed TextField
        initial_speed_toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle,Toggle new_toggle) {
                intitial_speed_textField.setText(new_toggle.getProperties().values().toArray()[0].toString());
                fire();
                drawChart();
            }
        });

        // Listener to call the fire() method when the fire button is pressed
        fire_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fire();
                drawChart();
            }
        });

        // Listener to initialize control values if the projectile type is changed
        projectile_type_combobox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initalizeControlValues();
                eraseChart();
            }
        });

        // Listener to initialize control values if the erase button is pressed
        erase_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initalizeControlValues();
                eraseChart();
            }
        });

        //This listener allows you to write Double. Nothing else.
        mass_textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^([0-9]*[.]?[0-9]*)")) {
                    String txt = newValue.replaceAll("[^0-9.]", "");
                    if (newValue.indexOf(".") != newValue.lastIndexOf(".")){
                        int p = newValue.indexOf(".", newValue.indexOf(".")  + 1);
                        txt = newValue.substring(0, p);
                    }
                    mass_textField.setText(txt);
                }
            }
        });
    }

    // Overridden start method
    @Override
    public void start(Stage primaryStage) {
        // set a title on the window, set a scene, size, and show the window
        Scene scene = new Scene(mainLayout, 940, 550);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(570); // set a min height
        primaryStage.setMinWidth(960);  // set min width
        primaryStage.show();
    }

    // Overridden stop method add functionality to this if you wish.
    @Override
    public void stop() {

    }

    // Entry point to our program
    public static void main(String[] args) {
        launch(args);
    }

    // Method to harvest values from controls, perform calculation and display the results
    private void fire(){
        mass = Double.parseDouble(mass_textField.getText());
        angle = Double.parseDouble(angle_textField.getText());
        initial_speed = Double.parseDouble(intitial_speed_textField.getText());
        angle_rad = (angle * Math.PI) / 180.0;

        range = (Math.pow(initial_speed, 2) / gravitational_accelleration) * Math.sin(2 * angle_rad);
        time = ((2 * initial_speed) * Math.sin(angle_rad)) / gravitational_accelleration;
        height = (Math.pow(initial_speed, 2) * Math.pow(Math.sin(angle_rad), 2)) / (2 * gravitational_accelleration);

        range_textField.setText(String.valueOf(Math.round(range * 100.0 ) / 100.0));
        time_textField.setText(String.valueOf(Math.round(time * 100.0 ) / 100.0));
        height_textField.setText(String.valueOf(Math.round(height * 100.0 ) / 100.0));
    }

    // Method to initalize the controls based on the selection of the projectile type
    private void initalizeControlValues(){
        if (HUMAN.equals(projectile_type_combobox.getValue())){
            mass_textField.setText("80.0");
            angle_slider.setMin(0);
            angle_slider.setMax(90);
            angle_slider.setValue(45.0);
            angle_textField.setText("45.0");
            angle_slider.setMajorTickUnit(15.0);
            initial_speed_fast.setSelected(true);
            intitial_speed_textField.setText((String) this.initial_speed_fast.getUserData());
        } else if (PIANO.equals(projectile_type_combobox.getValue())){
            mass_textField.setText("400.0");
            angle_slider.setMin(0);
            angle_slider.setMax(40);
            angle_slider.setValue(20.0);
            angle_textField.setText("20.0");
            angle_slider.setMajorTickUnit(10.0);
            initial_speed_slow.setSelected(true);
            intitial_speed_textField.setText((String) this.initial_speed_slow.getUserData());
        }
        range_textField.setText("");
        time_textField.setText("");
        height_textField.setText("");
    }

    //This function erase the charts then redraw it.
    private void drawChart(){
        eraseChart();

        //This is because I remove the autoranging
        yAxis.setUpperBound(height);
        yAxis.setTickUnit((double)(int)(height / 10)); // Double cast to only get integer
        xAxis.setUpperBound(range);
        xAxis.setTickUnit((double)(int)(range / 10)); // Double cast to only get integer

        //Create all serie's points.
        double t = 0.0; // time
        double y = 0.0; // coord y
        double x = 0.0; // coord x
        while (y >= 0){
            y = t * initial_speed * Math.sin(angle_rad) - (gravitational_accelleration * Math.pow(t, 2) / 2);
            x = initial_speed * Math.cos(angle_rad) * t;
            series.getData().add(new XYChart.Data<>(x, y));
            t += 0.1;
        }
    }

    //This function erase datas in serie and by consequence erase the chart
    private void eraseChart(){
        series.getData().clear();
    }
}

