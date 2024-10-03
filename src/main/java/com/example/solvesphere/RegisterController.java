package com.example.solvesphere;

import com.example.solvesphere.UserData.User;
import com.example.solvesphere.UserData.UserFactory;
import com.example.solvesphere.ValidationsUnit.ValidateInputData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RegisterController {
    @FXML
    private TextField TxtUsername;
    @FXML
    private TextField TxtMail;
    @FXML
    private PasswordField TxtPass;
    @FXML
    private DatePicker DateOfBirthVal;
    @FXML
    private ComboBox<String> CountryInput;
    @FXML
    private TextField fieldOfInterestInput;

    @FXML
    private CheckBox showPassCheck;
    @FXML
    private TextField TxtPassVisible;
    @FXML
    private ImageView profileImageView;

    @FXML
    private Hyperlink btChooseImage;

    private String imagePath;
    private final ServerCommunicator serverCommunicator;

    public RegisterController() {
        serverCommunicator = new ServerCommunicator("localhost", 12345);  // Initialize server communicator
    }
    @FXML
    public void closeCurrentStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @FXML
    public void registerUser() {
        // Collect user data
        String username = TxtUsername.getText();
        String email = TxtMail.getText();
        String password = TxtPass.getText();
        LocalDate dateOfBirth = DateOfBirthVal.getValue();
        String country = CountryInput.getValue();

        String dateOfBirthString = (dateOfBirth != null) ? dateOfBirth.toString() : "";

        // Validate data before sending to the server
        String[] userDataArr = {username, email, password, dateOfBirthString, country, getWordsFromFieldOfInterest().toString()};
        if (!ValidateInputData.validData(userDataArr) || !ValidateInputData.validEmail(email)) {
            AlertsUnit.showInvalidDataAlert();
            return;
        }

        // create user ob , using user factory
        User newUser = UserFactory.createUser(username, email, password, dateOfBirthString, country, getWordsFromFieldOfInterest(),LocalDate.now(),getProfileImagePath());

        // send the registration request using ServerCommunicator
        String response = serverCommunicator.sendRegistrationRequest(newUser);
        System.out.println("Server response: " + response);
    }


    public void togglePassVisibility(ActionEvent actionEvent) {
        if (showPassCheck.isSelected()) {
            // show
            TxtPassVisible.setText(TxtPass.getText());
            TxtPassVisible.setVisible(true);
            TxtPassVisible.setManaged(true);
            TxtPass.setVisible(false);
            TxtPass.setManaged(false);
        } else {
            // mask (UI level)
            TxtPass.setText(TxtPassVisible.getText());
            TxtPass.setVisible(true);
            TxtPass.setManaged(true);
            TxtPassVisible.setVisible(false);
            TxtPassVisible.setManaged(false);
        }
    }

    public Map<String, Integer> getWordsFromFieldOfInterest() {
        final int DEFAULT_PRIORITY = 1;

        String inputText = fieldOfInterestInput.getText();
        Map<String, Integer> wordMap = new HashMap<>();

        String[] words = inputText.split("[ ,]+"); //regex splits on spaces and commas

        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                wordMap.put(word, DEFAULT_PRIORITY);
            }
        }
        return wordMap;
    }

    @FXML
    private void chooseProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(btChooseImage.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            profileImageView.setImage(image);
        }
    }

    public String getProfileImagePath() {
        return imagePath;
    }
}
