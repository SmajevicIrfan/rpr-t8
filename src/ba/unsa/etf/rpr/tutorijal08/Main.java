package ba.unsa.etf.rpr.tutorijal08;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Pretraga datoteka");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Controller controller = loader.getController();
        primaryStage.setOnCloseRequest(event -> controller.endExploring());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
