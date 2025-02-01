
    import javafx.application.Application;
    import javafx.event.ActionEvent;
    import javafx.event.EventHandler;
    import javafx.geometry.Pos;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.layout.*;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.Circle;
    import javafx.scene.shape.Polygon;
    import javafx.scene.shape.Rectangle;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;
    import org.w3c.dom.css.Rect;

    public class FXdeviceSimulator extends Application {
        private int toasterHeight = 500;
        private int toasterMainSectionWidth = 700;
        private int toasterRightSectionWidth = 200;
        private int totalToasterWidth = 700;
        public static void main(String[] args) {
            launch(args);
        }




        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Simulator");
//            Button btn = new Button();
//            btn.setText("Say 'Hello World'");
//            btn.setOnAction(new EventHandler<ActionEvent>() {
//
//                @Override
//                public void handle(ActionEvent event) {
//                    System.out.println("Hello World!");
//                }
//            });

            //
            BorderPane root = new BorderPane();
            root.setMaxWidth(1000);
            root.setMaxHeight(1000);


            //StackPane mainSection = new StackPane();
            Pane mainSection = new Pane();
            mainSection.setPrefHeight(toasterHeight);
            mainSection.setPrefWidth(toasterMainSectionWidth);
            mainSection.setBorder(Border.stroke(Color.BLACK));
            mainSection.setStyle("-fx-background-color: darkgray;");
            // Set the door
            Rectangle door = new Rectangle(20,20,450,450);
            door.setStroke(Color.BLACK);
            door.setFill(Color.LIGHTGRAY);
            door.setArcWidth(50.0);
            door.setArcHeight(50.0);
            // Set the window
            Rectangle window = new Rectangle(60,60,375,375);

//          window.setStyle("-fx-border-color: black;");
            window.setStroke(Color.BLACK);
            window.setFill(Color.LIGHTYELLOW);
            window.setArcHeight(50.0);
            window.setArcWidth(50.0);

            //set the handle
            Rectangle handle = new Rectangle(50,35,400,10);
            handle.setStroke(Color.BLACK);
            handle.setFill(Color.BLACK);
            handle.setArcHeight(5);
            handle.setArcWidth(5);
            //mainSection.getChildren().addAll(door,window,handle);
            //set the try
            Rectangle tray = new Rectangle(60,325,375,20);
            tray.setFill((Color.SILVER));
            tray.setStroke(Color.BLACK);
            //set the heater
            Rectangle heater1 = new Rectangle(75,60,350,10);
            Rectangle heater2 = new Rectangle(75,425,350,10);
            heater1.setFill(Color.RED);
            heater1.setArcHeight(40.0);
            heater1.setArcWidth(15.0);
            heater2.setFill(Color.RED);
            heater2.setArcHeight(40.0);
            heater2.setArcWidth(15.0);
            mainSection.getChildren().addAll(door,window,handle,tray,heater1,heater2);




            // this is just the right hand section
            VBox rightHandSection = new VBox(30);
            rightHandSection.setPrefWidth(toasterRightSectionWidth);
            rightHandSection.setPrefHeight(toasterHeight);
            rightHandSection.setStyle("-fx-border-color: red;");


            // need to define a new hbox for the time/ temp
            VBox display = new VBox(5);
            HBox displayButtonSegment = new HBox(5);
            displayButtonSegment.setPrefHeight(30);
            displayButtonSegment.setPrefWidth(10);
            display.setStyle("-fx-border-color: red;");

            // adding the buttons to the display and we also needthe text
            Button timeButton = new Button("Time");
            Button tempButton = new Button("Temp");
            Text displayText = new Text("Temp text for display");

            // set the alignments to the center of the right hand sections
            displayButtonSegment.setAlignment(Pos.CENTER);
            display.setAlignment(Pos.CENTER);
            displayButtonSegment.getChildren().addAll(timeButton, tempButton);

            // add onto the vbox the Hbox for the buttons and the text
            display.getChildren().addAll(displayButtonSegment, displayText);

            // in this section we are defining the bake broil and roast
            Button bakeButton = new Button("Bake");
            Button broilButton = new Button("Broil");
            Button roastButton = new Button("Roast");

            // set up the hbox for the buttons
            HBox bakeSettingButtons = new HBox(5);
            bakeSettingButtons.getChildren().addAll(bakeButton, broilButton, roastButton);
            bakeSettingButtons.setAlignment(Pos.CENTER);
            bakeSettingButtons.setStyle("-fx-border-color: red;");

            // set up the button for the preheat
            HBox prebutton = new HBox();
            Button preheatButton = new Button("Pre");
            prebutton.getChildren().add(preheatButton);
            prebutton.setAlignment(Pos.CENTER);
            prebutton.setStyle("-fx-border-color: red;");

            // set up the button for the stop/clear
            HBox stopButtonBox = new HBox();
            Button stopButton = new Button("Stop/Clear");
            stopButtonBox.getChildren().add(stopButton);
            stopButtonBox.setAlignment(Pos.CENTER);
            stopButtonBox.setStyle("-fx-border-color: red;");

            // set up the button for the start
            HBox startButtonBox = new HBox();
            Button startButton = new Button("Start");
            startButtonBox.getChildren().add(startButton);
            startButtonBox.setAlignment(Pos.CENTER);
            startButtonBox.setStyle("-fx-border-color: red;");

            // set up the triangle increment and decrement
            VBox triangleButtonsBox = new VBox(5);
            Polygon triangleIncrement = new Polygon(0, 50, 50, 50, 25, 0);
            Polygon triangleDecrement = new Polygon(0, 0, 50, 0, 25, 50);
            triangleButtonsBox.getChildren().addAll(triangleIncrement, triangleDecrement);
            triangleButtonsBox.setAlignment(Pos.CENTER);
            triangleButtonsBox.setStyle("-fx-border-color: red;");

            // now we need to set the power button
            HBox powerButtonBox = new HBox();
            Circle powerButton = new Circle(10);
            powerButtonBox.setStyle("-fx-border-color: red;");
            powerButtonBox.getChildren().add(powerButton);
            powerButtonBox.setAlignment(Pos.BASELINE_RIGHT);

            // add everything to the rightHandSection
            rightHandSection.getChildren().addAll(display, bakeSettingButtons, prebutton, stopButtonBox,
                    startButtonBox, triangleButtonsBox, powerButtonBox);

            root.setRight(rightHandSection);
            root.setCenter(mainSection);

            // just setting the scene and the primary stage
            primaryStage.setScene(new Scene(root, 700, 500));
            primaryStage.show();
        }
    }





