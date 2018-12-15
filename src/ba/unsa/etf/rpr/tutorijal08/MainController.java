package ba.unsa.etf.rpr.tutorijal08;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.io.File;
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
    }

    private void startExploring(Event event) {
        this.listOfItems.getItems().clear();
        this.startSearch.setDisable(true);
        this.endSearch.setDisable(false);
        progressIndicator.setVisible(true);

        Thread searchThread = new Thread(explorer);
        searchThread.start();
    }

    void endExploring(Event event) {
        explorer.stop();

        this.startSearch.setDisable(false);
        this.endSearch.setDisable(true);
        progressIndicator.setVisible(false);
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
                if (!running) {
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
