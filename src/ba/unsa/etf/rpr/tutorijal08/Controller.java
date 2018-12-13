package ba.unsa.etf.rpr.tutorijal08;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextField searchTerm;
    @FXML
    private Button startSearch;
    @FXML
    private Button endSearch;
    @FXML
    private ListView<String> listOfItems;

    @FXML
    private ProgressIndicator progressIndicator;

    private Explorer explorer;
    private File searchRoot = new File(System.getProperty("user.home"));

    public Controller() {
        explorer = new Explorer();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startSearch.setOnAction(event -> this.startExploring());

        endSearch.setOnAction(event -> this.endExploring());
    }

    private void startExploring() {
        this.listOfItems.getItems().clear();
        this.startSearch.setDisable(true);
        this.endSearch.setDisable(false);
        progressIndicator.setVisible(true);

        Thread searchThread = new Thread(explorer);
        searchThread.start();
    }

    void endExploring() {
        this.startSearch.setDisable(false);
        this.endSearch.setDisable(true);
        progressIndicator.setVisible(false);

        explorer.stop();
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
            if (!running) {
                return;
            }

            File[] children = parent.listFiles();

            assert children != null;
            for (File child : children) {
                if (child.isDirectory()) {
                    findAllIn(searchTerm, child);
                } else if (child.getName().contains(searchTerm)) {
                    Platform.runLater(() -> listOfItems.getItems().add(child.getAbsolutePath()));
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (parent.equals(searchRoot)) {
                endExploring();
                this.stop();
            }
        }
    }
}
