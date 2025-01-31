
    import javafx.application.Application;
    import javafx.event.ActionEvent;
    import javafx.event.EventHandler;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.layout.StackPane;
    import javafx.scene.shape.Rectangle;
    import javafx.stage.Stage;
     
    public class FXdeviceSimulator extends Application {
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
            
            StackPane root = new StackPane();
//            root.getChildren().add(btn);

            Rectangle rectangle = new Rectangle(10 , 10);
            root.getChildren().add(rectangle);
            primaryStage.setScene(new Scene(root, 300, 250));
            primaryStage.show();
        }
    }





