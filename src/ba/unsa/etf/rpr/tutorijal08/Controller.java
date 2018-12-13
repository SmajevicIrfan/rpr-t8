package ba.unsa.etf.rpr.tutorijal08;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
    private ListView<String> listOfItems;

    private File searchRoot = new File(System.getProperty("user.home"));

    public Controller() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startSearch.setOnAction(event -> {
            System.out.println("Pocinjem pretragu");
            this.listOfItems.getItems().clear();
            this.startSearch.setDisable(true);

            Thread searchThread = new Thread(new Explorer());
            searchThread.start();
        });
    }

    private class Explorer implements Runnable {
        @Override
        public void run() {
            this.findAllIn(searchTerm.getText(), searchRoot);
        }

        private void findAllIn(String searchTerm, File parent) {
            System.out.println(parent.getAbsolutePath());
            File[] children = parent.listFiles();

            assert children != null;
            for (File child : children) {
                if (child.isDirectory()) {
                    findAllIn(searchTerm, child);
                } else if (child.getName().contains(searchTerm)) {
                    listOfItems.getItems().add(child.getAbsolutePath());
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
