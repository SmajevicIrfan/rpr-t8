package ba.unsa.etf.rpr.tutorijal08;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class MailSenderController implements Initializable {
    @FXML
    private TextField fullName;
    @FXML
    private TextField address;
    @FXML
    private TextField city;
    @FXML
    private TextField postcode;

    private String validationURL = "http://c9.etf.unsa.ba/proba/postanskiBroj.php";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fullName.focusedProperty().addListener(this.getNonEmptyValidator(fullName));
        address.focusedProperty().addListener(this.getNonEmptyValidator(address));
        city.focusedProperty().addListener(this.getNonEmptyValidator(city));

        postcode.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                postcode.getStyleClass().removeAll("invalidField");
                postcode.getStyleClass().removeAll("validField");
            } else {
                Thread validation = new Thread(() -> validatePostcode(postcode.getText()));
                validation.start();
            }
        });
    }

    private ChangeListener<Boolean> getNonEmptyValidator(TextField target) {
        return ((observable, oldValue, newValue) -> {
            if (newValue) {
                target.getStyleClass().removeAll("invalidField");
                target.getStyleClass().removeAll("validField");
            } else {
                validateNonEmpty(target);
            }
        });
    }

    private void validateNonEmpty(TextField target) {
        if (target.getText() != null && !target.getText().isEmpty()) {
            target.getStyleClass().add("validField");
        } else {
            target.getStyleClass().add("invalidField");
        }
    }

    private void validatePostcode(String postcode) {
        String queryText = String.format("%s?postanskiBroj=%s", validationURL, postcode);
        String result = "";
        try {
            URL query = new URL(queryText);
            BufferedReader input = new BufferedReader(new InputStreamReader(query.openStream(), StandardCharsets.UTF_8));
            result = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.equals("OK")) {
            Platform.runLater(() -> {
                this.postcode.getStyleClass().removeAll("invalidField");
                this.postcode.getStyleClass().add("validField");
            });
        } else {
            Platform.runLater(() -> {
                this.postcode.getStyleClass().removeAll("validField");
                this.postcode.getStyleClass().add("invalidField");
            });
        }
    }
}
