package ba.unsa.etf.rpr.tutorijal08;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private TextField searchTerm;
    @FXML
    private Button startSearch;
    @FXML
    private Button endSearch;
    @FXML
    private ListView<File> listOfItems;

    @FXML
    private ProgressIndicator progressIndicator;

    private final Explorer explorer = new Explorer();
    private final File searchRoot = new File(System.getProperty("user.home"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startSearch.setOnAction(this::startExploring);
        endSearch.setOnAction(this::endExploring);
        listOfItems.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                openSenderPopup(click);
            }
        });
    }

    private void startExploring(Event event) {
        this.listOfItems.getItems().clear();
        this.startSearch.setDisable(true);
        this.endSearch.setDisable(false);
        this.progressIndicator.setVisible(true);

        Thread searchThread = new Thread(explorer);
        searchThread.start();
    }

    void endExploring(Event event) {
        explorer.stop();

        this.startSearch.setDisable(false);
        this.endSearch.setDisable(true);
        this.progressIndicator.setVisible(false);
    }

    private void openSenderPopup(Event event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("mailSender.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;

        Stage popup = new Stage();
        popup.setTitle("Slanje pošte");
        popup.setScene(new Scene(root));
        popup.initOwner(((Node)event.getTarget()).getScene().getWindow());

        popup.show();
    }

    private class Explorer implements Runnable {
        private volatile boolean running = false;

        @Override
        public void run() {
            this.running = true;
            this.findAllIn(searchTerm.getText(), searchRoot);
        }

        void stop() {
            this.running = false;
        }

        private void findAllIn(String searchTerm, File parent) {
            File[] children = parent.listFiles();

            assert children != null;
            for (File child : children) {
                if (!this.running) {
                    return;
                }

                if (child.isDirectory()) {
                    findAllIn(searchTerm, child);
                } else if (child.getName().contains(searchTerm)) {
                    Platform.runLater(() -> listOfItems.getItems().add(child));
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (parent.equals(searchRoot)) {
                endExploring(null);
            }
        }
    }
}
