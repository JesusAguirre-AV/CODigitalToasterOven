    import javafx.animation.FadeTransition;
    import javafx.animation.FillTransition;
    import javafx.application.Application;
    import javafx.beans.binding.Bindings;
    import javafx.beans.property.SimpleIntegerProperty;
    import javafx.event.ActionEvent;
    import javafx.event.EventHandler;
    import javafx.geometry.Pos;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.effect.BlendMode;
    import javafx.scene.image.Image;
    import javafx.scene.layout.*;
    import javafx.scene.paint.*;
    import javafx.scene.shape.Circle;
    import javafx.scene.shape.Polygon;
    import javafx.scene.shape.Rectangle;
    import javafx.scene.text.Font;
    import javafx.scene.text.FontWeight;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;
    import javafx.util.Duration;
    import org.w3c.dom.css.Rect;

    import java.io.IOException;
    import java.io.ObjectInputStream;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.security.KeyStore;
    import java.util.Objects;
    import java.util.Set;

    public class FXdeviceSimulator extends Application {
        private int toasterHeight = 500;
        private int toasterMainSectionWidth = 700;
        private int toasterRightSectionWidth = 200;
        private int totalToasterWidth = 700;
        private boolean isPowerOn = false;
        private boolean isOnTemp = false;
        private boolean isOnTime = false;
        private boolean isTopHeaterOn = false;
        private boolean isBottomHeaterOn = false;
        private boolean isLightOn = false;
        private boolean doorStatus = false;
        private int timesPressed = 0;
        private Circle powerButton;
        private Circle lightButton;
        private SimpleIntegerProperty currentTimeMinutes = new SimpleIntegerProperty(0);
        private SimpleIntegerProperty currentTimeSeconds = new SimpleIntegerProperty(0);
        private SimpleIntegerProperty currentTempF = new SimpleIntegerProperty(0);

        Rectangle window = setupWindow();

        private enum setting{
            BAKE,
            BROIL,
            ROAST,
            NOSETT
        }
        private setting sett = setting.NOSETT;
        //Set the heaters
        private Rectangle [] heaters = setupHeaters();


        public static void main(String[] args) {
            launch(args);
        }

        private void setUpServerSocket(){
            int port = 1234;
//            System.out.println("Server start");
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server listening on port : " + port);

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
//                Object recievedObject = in.readObject();

                // make a thread to listen for and process messages

                processMessages(in);

            } catch (IOException e) {
                System.out.println("Socket Closed");;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Method to process certain messages
         * @param inputStream the input stream
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void processMessages(ObjectInputStream inputStream) throws IOException,
                ClassNotFoundException {
            while (inputStream != null){
                int currentMessage = (int) inputStream.readObject();
                switch (currentMessage){
                    // toggle power
                    case 1 -> pressPower(powerButton, isPowerOn, true);

                    // toggle top heater
                    case 2 -> {
                        if (!isTopHeaterOn){
                            toggleTopHeaterOn();
                        }
                        else{
                            toggleTopHeaterOff();
                        }
                    }
                    // toggle bottom heater
                    case 3 -> {
                        if (!isBottomHeaterOn){
                            toggleBottomHeaterOn(); // toggle bottom heater
                        }
                        else {
                            toggleTopHeaterOff();
                        }
                    }

                    case 4 -> {
                        System.out.println("Got a message to turn on the light ");
                        toggleLight(); // toggle light
                    }
                    case 5 -> toggleDoor(); // toggle door sensor
                    case 6 -> System.out.println("Got a message 6"); // temperature?
                    case 7 -> System.out.println("Got a message 7"); // time? maybe?
                }
            }
        }



        @Override
        public void start(Stage primaryStage) {
            new Thread(() -> setUpServerSocket()).start();

            primaryStage.setTitle("Simulator");

            BorderPane root = new BorderPane();
            root.setMaxWidth(1000);
            root.setMaxHeight(1000);

            //The main Pane
            Pane mainSection = setupMainSection();

            //Set up the Door.
            Rectangle door = setupDoor();

            //Set the handle
            Rectangle handle = setupHandle();

            //Set the tray
            Rectangle tray = setupTray();

            //Adds all the main section elements.
            mainSection.getChildren().addAll(door,window,handle,tray,heaters[0],heaters[1]);
            // this is just the right hand section in general
            VBox rightHandSection = setUpRightHandSection();

            // set up the display section
            VBox display = setUpDisplay();

            // set up the hbox for the buttons
            HBox bakeSettingButtons = setUpSettingButtons(heaters);

            // set up the button for the preheat
            HBox prebutton = preHeatButtonSetup();

            // set up the button for the stop/clear
            HBox stopButtonBox = stopButtonSetup();

            // set up the button for the start
            HBox startButtonBox = startButtonSetup(heaters);

            // set up the triangle increment and decrement
            VBox triangleButtonsBox = incrementDecrementButtonSetup();

            // now we need to set the power button
            VBox powerButtonBox = powerAndLightButtonSetup();

            // add everything to the rightHandSection
            rightHandSection.getChildren().addAll(display, bakeSettingButtons, prebutton, stopButtonBox,
                    startButtonBox, triangleButtonsBox, powerButtonBox);

            root.setRight(rightHandSection);
            root.setCenter(mainSection);

            // just setting the scene and the primary stage
            primaryStage.setScene(new Scene(root, 700, 500));
            primaryStage.show();
        }

        /**
         * This sets up the main area
         * @return Pane to hold on the features on the left side of the oven
         */
        private Pane setupMainSection(){
            Pane mainSection = new Pane();
            mainSection.setPrefHeight(toasterHeight);
            mainSection.setPrefWidth(toasterMainSectionWidth);
            mainSection.setBorder(Border.stroke(Color.BLACK));
            mainSection.setStyle("-fx-background-color: darkgray;");
            return mainSection;
        }

        /**
         * This houses the door using a rectangle
         * @return The rectangle that has the door features.
         */
        private Rectangle setupDoor(){
            // Set the door
            Rectangle door = new Rectangle(20,20,450,450);
            door.setStroke(Color.BLACK);
            door.setFill(Color.LIGHTGRAY);
            door.setArcWidth(50.0);
            door.setArcHeight(50.0);
            return door;
        }

        /**
         * Rectangle that has the window, can change color based on the settings on the oven.
         * @return the window rectangle with features.
         */
        private Rectangle setupWindow(){
            Rectangle window = new Rectangle(60,60,375,375);
            window.setStroke(Color.BLACK);
            window.setFill(Color.LIGHTGRAY);
            window.setArcHeight(50.0);
            window.setArcWidth(50.0);
            return window;
        }

        /**
         * Rectangle that deals with the handle
         * @return Handle and its features.
         */
        private Rectangle setupHandle(){
            Rectangle handle = new Rectangle(50,35,400,10);
            handle.setStroke(Color.BLACK);
            handle.setFill(Color.BLACK);
            handle.setArcHeight(5);
            handle.setArcWidth(5);
            setupHandleCLick(handle);
            return handle;
        }

        /**
         * Method to handle the handle click
         * @param handle the handle
         */
        private void setupHandleCLick(Rectangle handle){
            handle.setOnMouseClicked(event -> {
                if(doorStatus){
                    System.out.println("Door is now closed");
                    doorStatus = false;
                }else{
                    System.out.println("Door is now open");
                    doorStatus = true;
                }
            });
        }

        /**
         * Rectangle that holds the tray.
         * @return tray and its features
         */
        private Rectangle setupTray(){
            Rectangle tray = new Rectangle(60,325,375,20);
//            LinearGradient metallicGradient = new LinearGradient(
//
//            );
            Stop[] stops = new Stop[] {
                    new Stop(0, Color.BLACK),
                    new Stop(1, Color.BLACK),
                    new Stop(0.5, Color.DARKGRAY)
            };
            LinearGradient lg1 = new LinearGradient(60,325 , 385, 345,
                    false, CycleMethod.NO_CYCLE, stops);

            tray.setFill(lg1);
//            tray.setBlendMode(BlendMode.DARKEN);
         //  tray.setFill((Color.BLACK));
                //      tray.setStroke(Color.BLACK);
            return tray;
        }

        /**
         * Rectangles that have the heaters. Each one is different and can be manipulated sepreatly.
         * @return Array of rectangles that deal with the heaters.
         */
        private Rectangle[] setupHeaters(){
            Rectangle heater1 = new Rectangle(75,60,350,10);
            Rectangle heater2 = new Rectangle(75,425,350,10);
            heater1.setFill(Color.BLACK);
            heater1.setArcHeight(40.0);
            heater1.setArcWidth(15.0);
            heater2.setFill(Color.BLACK);
            heater2.setArcHeight(40.0);
            heater2.setArcWidth(15.0);
            Rectangle[] temp = {heater1,heater2};
            return temp;
        }
        /**
         * Method to set up the right hand side of the toaster
         * @return the vbox of the right hand side
         */
        private VBox setUpRightHandSection(){

            // this is just the right hand section
            VBox rightHandSection = new VBox(30);
            rightHandSection.setPrefWidth(toasterRightSectionWidth);
            rightHandSection.setPrefHeight(toasterHeight);
            // set the styles of the right hand side
            rightHandSection.setStyle("-fx-border-color: black;");
            rightHandSection.setStyle("-fx-background-color: darkGray;");

            return rightHandSection;
        }

        /**
         * This method defines the display section for our right hand side of the toaster.
         * @return the vbox for the right hand side
         */
        private VBox setUpDisplay(){
            // need to define a new hbox for the time/ temp
            VBox display = new VBox(5);
            HBox displayButtonSegment = new HBox(5);
            displayButtonSegment.setPrefHeight(30);
            displayButtonSegment.setPrefWidth(10);

            display.setStyle("-fx-border-color: black;");


            // adding the buttons to the display and we also needthe text
            Button timeButton = new Button("Time");
            timeButton.setStyle("-fx-font-family: 'Comic Sans MS'");
            timeButton.setStyle("-fx-border-color: black");

            Button tempButton = new Button("Temp");
            tempButton.setStyle("-fx-font-family: 'Comic Sans MS'");
            tempButton.setStyle("-fx-border-color: black");

            Label displayText = new Label();
            displayText.textProperty().bind(Bindings.concat(
                    currentTimeMinutes.asString("%02d"),
                    ":",currentTimeSeconds.asString("%02d"), "    ",
                    currentTempF.asString("%02d"), " F°"
            ));
            displayText.setStyle("-fx-font-family: 'Comic Sans MS'");
//            displayText.textProperty().bind(
//                    Bindings.concat(
//                    currentTimeMinutes.asString("%02d"),
//                    ":",currentTimeMinutes.asString("%02d")
//                    )
//            );

            // call the handlers for the buttons
            handleTempButtonClick(tempButton, timeButton);
            handleTimeButtonClick(timeButton, tempButton);

            // set the alignments to the center of the right hand sections
            displayButtonSegment.setAlignment(Pos.CENTER);
            display.setAlignment(Pos.CENTER);
            displayButtonSegment.getChildren().addAll(timeButton, tempButton);

            // add onto the vbox the Hbox for the buttons and the text
            display.getChildren().addAll(displayButtonSegment, displayText);

            return display;
        }


        /**
         * Method to set the time text when needed
         * @return the time text
         */
        private Label setTimeText(Label timeLabel){
            timeLabel.textProperty().bind(
                    Bindings.concat(
                            currentTimeMinutes.asString("%02d"),
                            ":",currentTimeSeconds.asString("%02d")
                    )
            );
            return timeLabel;
        }

        /**
         * Method to set the time text when needed
         * @return the time text
         */
        private Label setTempText(Label tempLabel){
            tempLabel.textProperty().bind(
                    Bindings.concat(
                            currentTempF.asString("%02d"), " F°"
                    )
            );
            return tempLabel;
        }


        /**
         * Method to handle the click on the time button on our simulation
         * @param timeButton the time button
         */
        private void handleTimeButtonClick(Button timeButton, Button tempButton){
            timeButton.setOnMouseClicked(event -> {
                System.out.println("Clicked on the time button");
                if (!isOnTime){
//                    setTimeText(displayText);
                    timeButton.setStyle("-fx-font-weight: bold;");
                    tempButton.setStyle("-fx-font-weight: normal;");
                    this.isOnTime = true;
                    this.isOnTemp = false;
                }
            });
        }

        /**
         * Method to handle the click on the temp button on our simulation
         * @param tempButton the time button
         */
        private void handleTempButtonClick(Button tempButton, Button timeButton){
            tempButton.setOnMouseClicked(event -> {
                System.out.println("Clicked on the temp button");
                if (!isOnTemp){
//                    setTempText(displayText);
                    tempButton.setStyle("-fx-font-weight: bold;");
                    timeButton.setStyle("-fx-font-weight: normal;");
                    this.isOnTemp = true;
                    this.isOnTime = false;
                }
            });
        }


        /**
         * This method sets up the bake broil and roast buttons for the simulation
         * @return the HBox of the buttons
         */
        private HBox setUpSettingButtons(Rectangle[] heaters){
            // in this section we are defining the bake broil and roast
            Button bakeButton = new Button("Bake");
            Button broilButton = new Button("Broil");
            Button roastButton = new Button("Roast");

            bakeButton.setStyle("-fx-font-family: 'Comic Sans MS'");
            bakeButton.setStyle("-fx-border-color: black");

            broilButton.setStyle("-fx-font-family: 'Comic Sans MS'");
            broilButton.setStyle("-fx-border-color: black");

            roastButton.setStyle("-fx-font-family: 'Comic Sans MS'");
            roastButton.setStyle("-fx-border-color: black");



            handleBakeButtonClick(bakeButton);
            handleBroilButtonClick(broilButton);
            handleRoastButtonClick(roastButton);

            // set up the hbox for the buttons
            HBox bakeSettingButtons = new HBox(5);
            bakeSettingButtons.getChildren().addAll(bakeButton, broilButton, roastButton);
            bakeSettingButtons.setAlignment(Pos.CENTER);
            bakeSettingButtons.setStyle("-fx-border-color: black;");

            return bakeSettingButtons;
        }

        /**
         * Method to handle the click on the bake button on our simulation
         * @param bakeButton the time button
         */
        private void handleBakeButtonClick(Button bakeButton){
            bakeButton.setOnMouseClicked(event -> {
                sett = setting.BAKE;
                System.out.println("Clicked on the bake button");

            });
        }

        /**
         * Method to handle the click on the broil button on our simulation
         * @param broilButton the time button
         */
        private void handleBroilButtonClick(Button broilButton){
            broilButton.setOnMouseClicked(event -> {
                sett = setting.BROIL;
                System.out.println("Clicked on the broil button");
            });
        }

        /**
         * Method to handle the click on the broil button on our simulation
         * @param roastButton the time button
         */
        private void handleRoastButtonClick(Button roastButton){
            roastButton.setOnMouseClicked(event -> {
                sett = setting.ROAST;
                System.out.println("Clicked on the roast button");
            });
        }

        /**
         * This method just sets up the pre-heat button for the simulation
         * @return the HBox with the button
         */
        private HBox preHeatButtonSetup(){
            // set up the button for the preheat function
            Button pizza = new Button("Pizza");
            Button nuggets= new Button("Nuggets");

            pizza.setStyle("-fx-font-family: 'Comic Sans MS'");
            pizza.setStyle("-fx-border-color: black");
            nuggets.setStyle("-fx-font-family: 'Comic Sans MS'");
            nuggets.setStyle("-fx-border-color: black");

            // handle the mouse clicks
            handlePreButtonClick(pizza,nuggets);
            HBox prebutton = new HBox(5);
            prebutton.getChildren().addAll(pizza,nuggets);
            prebutton.setAlignment(Pos.CENTER);
            prebutton.setStyle("-fx-border-color: black;");

            return prebutton;
        }

        /**
         * Method to handle the click on the pre-het button on our simulation
         * @param pizza and nuggets the time button
         */
        private void handlePreButtonClick(Button pizza,Button nuggets){
            pizza.setOnMouseClicked(event -> {
                sett = setting.BAKE;
                currentTimeMinutes.set(15);
                currentTempF.set(375);
            });
            nuggets.setOnMouseClicked(event -> {
                sett = setting.ROAST;
                currentTimeMinutes.set(10);
                currentTempF.set(400);
            });
        }

        /**
         * This method just sets up the stop button for the simulation
         * @return the HBox with the button
         */
        private HBox stopButtonSetup(){
            // set up the button for the stop/clear
            HBox stopButtonBox = new HBox();
            Button stopButton = new Button("Stop/Clear");

            stopButton.setStyle("-fx-font-family: 'Comic Sans MS'");
            stopButton.setStyle("-fx-border-color: black");

            // handle the mouse clicks
            handleStopButtonClick(stopButton);
            stopButtonBox.getChildren().add(stopButton);
            stopButtonBox.setAlignment(Pos.CENTER);
            stopButtonBox.setStyle("-fx-border-color: black;");
            return stopButtonBox;
        }

        /**
         * Method to handle the click on the stop button on our simulation
         * @param stopButton the time button
         */
        private void handleStopButtonClick(Button stopButton){
            stopButton.setOnMousePressed(event -> {
                timesPressed++;
                if(timesPressed == 1)
                {
                    System.out.println("Clicked on the stop button");
                }
                else if (timesPressed == 2)
                {
                    System.out.println("Clicked on the clear button");
                }
                //TODO: need to send info over the socket about this
                //TODO: first click is stop, and second click is clear
            });
        }

        /**
         * This method just sets up the stop button for the simulation
         * @return the HBox with the button
         */
        private HBox startButtonSetup(Rectangle [] heaters){
            // set up the button for the start
            HBox startButtonBox = new HBox();
            Button startButton = new Button("Start");

            startButton.setStyle("-fx-font-family: 'Comic Sans MS'");
            startButton.setStyle("-fx-border-color: black");
//            startButton.setStyle("-fx-background-color: silver");

            // handle the mouse clicks
            handleStartButtonClick(startButton);

            startButtonBox.getChildren().add(startButton);
            startButtonBox.setAlignment(Pos.CENTER);
            startButtonBox.setStyle("-fx-border-color: black;");

            return startButtonBox;
        }

        /**
         * Method to handle the click on the start button on our simulation
         * @param startButton the time button
         */
        private void handleStartButtonClick(Button startButton){
            startButton.setOnMouseClicked(event -> {
                switch (sett) {
                    case ROAST -> {
                        toggleTopHeaterOn();
                        toggleBottomHeaterOn();
                    }
                    case BAKE -> {
                        toggleTopHeaterOff();
                        toggleBottomHeaterOn();
                    }
                    case BROIL -> {
                        toggleTopHeaterOn();
                        toggleBottomHeaterOff();
                    }
                }
                System.out.println("Clicked on the start button");
            });
        }

        /**
         * Method to toggle the top heater on
         */
        private void toggleTopHeaterOn(){
           if(heaters[0].getFill() == Color.BLACK) {
               FillTransition ft = new FillTransition(Duration.seconds(6), heaters[0], Color.BLACK, Color.RED);
               ft.setCycleCount(0);
               ft.setAutoReverse(false);
               ft.play();
           }else{
               heaters[0].setFill(Color.RED);
           }
           isTopHeaterOn = true;
        }

        /**
         * Method to toggle the bottom heater on
         */
        private void toggleBottomHeaterOn(){

            if(heaters[1].getFill() == Color.BLACK) {
                FillTransition ft = new FillTransition(Duration.seconds(6), heaters[1], Color.BLACK, Color.RED);
                ft.setCycleCount(0);
                ft.setAutoReverse(false);
                ft.play();

            }else{
                heaters[1].setFill(Color.RED);
            }
            isBottomHeaterOn = true;
        }

        /**
         * Method to toggle the top heater off
         */
        private void toggleTopHeaterOff(){
            if(heaters[0].getFill() == Color.RED) {
                FillTransition ft = new FillTransition(Duration.seconds(6), heaters[0], Color.RED, Color.BLACK);
                ft.setCycleCount(0);
                ft.setAutoReverse(false);
                ft.play();
            }else{
                heaters[0].setFill(Color.BLACK);
            }
            isTopHeaterOn = false;
        }

        /**
         * Method to toggle the bottom heater off
         */
        private void toggleBottomHeaterOff(){
            if(heaters[1].getFill() == Color.RED) {
                FillTransition ft = new FillTransition(Duration.seconds(6), heaters[1], Color.RED, Color.BLACK);
                ft.setCycleCount(0);
                ft.setAutoReverse(false);
                ft.play();
            }else{
                heaters[1].setFill(Color.BLACK);
            }
            isBottomHeaterOn = false;
        }

        /**
         * This method just sets up the triangle increment/ decrement button for the simulation
         * @return the HBox with the button
         */
        private VBox incrementDecrementButtonSetup(){
            // set up the triangle increment and decrement
            VBox triangleButtonsBox = new VBox(5);
            Polygon triangleIncrement = new Polygon(0, 50, 50, 50, 25, 0);
            Polygon triangleDecrement = new Polygon(0, 0, 50, 0, 25, 50);

//            Image image = new Image(Objects.requireNonNull(getClass().getResource("add-icon3.PNG")).toExternalForm());
//            Image image2 = new Image(Objects.requireNonNull(getClass().getResource("subtract-icon1.PNG")).toExternalForm());

            triangleIncrement.setStyle("-fx-border-color: white");
            triangleDecrement.setStyle("-fx-border-color: white");

            triangleDecrement.setFill(Color.BLACK); //new ImagePattern(image2)
            triangleIncrement.setFill(Color.BLACK); //new ImagePattern(image)

            // handle the mouse clicks
            handleIncrementButtonClick(triangleIncrement);
            handleDecrementButtonClick(triangleDecrement);

            triangleButtonsBox.getChildren().addAll(triangleIncrement, triangleDecrement);
            triangleButtonsBox.setAlignment(Pos.CENTER);
            triangleButtonsBox.setStyle("-fx-border-color: black;");

            return triangleButtonsBox;
        }

        /**
         * Method to handle the click on the broil button on our simulation
         * @param incrementButton the time button
         */
        private void handleIncrementButtonClick(Polygon incrementButton){
            incrementButton.setOnMouseClicked(event -> {
                System.out.println("Clicked on the increment button");
                if (isOnTime) {
                    currentTimeMinutes.set(currentTimeMinutes.get() + 1);
                }
                else if(isOnTemp){
                    // the max temp is 500
                    if (currentTempF.get() < 500){
                        currentTempF.set(currentTempF.get() + 15);
                    }
                }
                timesPressed = 0;
            });
        }


        /**
         * Method to handle the click on the broil button on our simulation
         * @param decrementButton the time button
         */
        private void handleDecrementButtonClick(Polygon decrementButton){
            decrementButton.setOnMouseClicked(event -> {
                System.out.println("Clicked on the decrement button");
                if(isOnTime){
                    // the min is always 0
                    if (currentTimeMinutes.get() != 0){
                        currentTimeMinutes.set(currentTimeMinutes.get() - 1);
                    }
                }
                else if (isOnTemp){
                    // the min is always 0
                    if (currentTempF.get() != 0){
                        currentTempF.set(currentTempF.get() - 15);
                    }
                }
                timesPressed = 0;
            });
        }

        /**
         * This method just sets up the power button for the simulation
         * @return the HBox with the button
         */
        private VBox powerAndLightButtonSetup(){

            // now we need to set the power button
            VBox powerButtonBox = new VBox(5);

            // hboxes for the buttons
            HBox powerButtonThing = new HBox(5);
            HBox lightButtonThing = new HBox(11);

            // text for the power/ light button
            Text powerText = new Text("Power");
            Text lightText = new Text("Light");

            // circles for the buttons
            powerButton = new Circle(10);
            lightButton = new Circle(10);

            // handle the mouse clicks
            handlePowerButtonClicks(powerButton);
            handleLightButtonClicks(lightButton);

            // add the button and the
            powerButtonThing.getChildren().addAll(powerButton, powerText);
            lightButtonThing.getChildren().addAll(lightButton, lightText);

            // set the alignment of the HBoxes to the center
            powerButtonThing.setAlignment(Pos.CENTER);
            lightButtonThing.setAlignment(Pos.CENTER);

            powerButtonBox.setStyle("-fx-border-color: black;");
            powerButtonBox.getChildren().addAll(powerButtonThing, lightButtonThing);
            powerButtonBox.setAlignment(Pos.CENTER);

            return powerButtonBox;
        }

        /**
         * Method to handle the click on the power button on our simulation
         * @param powerButton the time button
         */
        private void handlePowerButtonClicks(Circle powerButton){
            powerButton.setOnMouseClicked(event -> {
                pressPower(powerButton, isPowerOn, true);
            });
        }

        /**
         * Method to handle the click on the power button on our simulation
         * @param lightButton the time button
         */
        private void handleLightButtonClicks(Circle lightButton){
            lightButton.setOnMouseClicked(event -> {
                if(!isLightOn)
                {
                    isLightOn = true;
                }else{
                    isLightOn = false;
                }
                pressLight(lightButton);
            });
        }

        /**
         * Method to press the power button
         * @param powerButton the power button
         */
        private void pressPower(Circle powerButton, Boolean isThingON , Boolean isPowerButton){
            // TODO: send some info through socket possibly
            if (isThingON){
                powerButton.setFill(Color.BLACK);

                if (isPowerButton){
                    isPowerOn = false;
                }
                else {
                    isLightOn = false;
                    toggleLight();
                }

                System.out.println("Button Off");
            }
            else {
                powerButton.setFill(Color.GREEN);

                if(isPowerButton){
                    isPowerOn = true;
                }
                else {
                    isLightOn = true;
                    toggleLight();
                }

                System.out.println("Button On");
            }
            timesPressed = 0;
        }

        /**
         * Method to simulate pressing the lightButton
         * @param lightButton the light button
         */
        private void pressLight(Circle lightButton){
            if(isLightOn){
                lightButton.setFill(Color.GREEN);
                window.setFill(Color.LIGHTYELLOW);
            }else
            {
                lightButton.setFill(Color.BLACK);
                window.setFill(Color.LIGHTGRAY);
            }
        }

        /**
         * Method to turn on/off the light depending on the message from socket.
         */
        private void toggleLight(){
            if (!isLightOn) {
                window.setFill(Color.LIGHTYELLOW);
                isLightOn = false;
            }else
            {
                window.setFill(Color.LIGHTGRAY);
                isLightOn = true;
            }
        }

        /**
         * Method to toggle the door
         */
        private void toggleDoor(){
            if(doorStatus){
                System.out.println("Door is now Closed");
                doorStatus = false;
            }else
            {
                System.out.println("Door is now Open");
                doorStatus = true;
            }
        }
    }