package com.example.solvesphere;

import com.example.solvesphere.AlertsUnit;
import com.example.solvesphere.DataBaseUnit.UserDAO;
import com.example.solvesphere.DataBaseUnit.UserDAOImpl;
import com.example.solvesphere.ServerCommunicator;
import com.example.solvesphere.UserData.User;
import com.example.solvesphere.ValidationsUnit.ValidateInputData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInUiController {
    @FXML
    public Button logInBt;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField userNameFld;
    @FXML
    private PasswordField userPassFld;
    @FXML
    private TextField userPassTextFld;
    @FXML
    private CheckBox showPasswordCheck;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private Hyperlink forgotDataLink;
    private final ServerCommunicator serverCommunicator;

    public LogInUiController() {
        serverCommunicator = new ServerCommunicator("localhost", 12345);
    }

    @FXML
    protected void onLogInButtonClick() {
        if (userNameFld.getText().isEmpty() || getPasswordTxt().isEmpty()) {
            AlertsUnit.showInvalidDataAlert();
            return;
        }

        String username = userNameFld.getText();
        String password = getPasswordTxt();
        Object response = serverCommunicator.sendLoginRequest(username, password);

        // Handle the response based on its type
        if (response instanceof User) {
            User user = (User) response;
            transitionToUserDashboard(user); // navigate to the dashboard with the user object
        } else if (response instanceof String) {
            String message = (String) response;
            responseStatus(message); //handle response messages
        } else {
            AlertsUnit.showErrorAlert("Unexpected response type received.");
        }
    }



    private void transitionToUserDashboard(User user) {
        try {
            // Close the current window
            Stage currentStage = (Stage) logInBt.getScene().getWindow();
            currentStage.close();

            // Load the dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainDashboard.fxml"));
            Parent root = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("User Dashboard");
            dashboardStage.setScene(new Scene(root));

            MainDashController controller = loader.getController();
            controller.initUserData(user); // Ensure you have this method in your MainDashboardController
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertsUnit.showErrorAlert(e.toString());
        }
    }

    public void responseStatus(String response) {
        if (response.contains("successful")) {
            AlertsUnit.showSuccessLogInAlert();
        } else if (response.contains("Invalid")) {
            AlertsUnit.showErrorAlert("Incorrect username or password.\nPlease try again.");
        } else {
            AlertsUnit.showErrorAlert("Unknown error occurred. Please try again.");
        }
    }
    @FXML
     protected void onRegisterClick(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Parent root = fxmlLoader.load();
            // new stage
            Stage registerStage = new Stage();
            registerStage.setTitle("Register");
            registerStage.setScene(new Scene(root));
            registerStage.show(); //show

        } catch (IOException e) {e.printStackTrace();}
    }

    @FXML
    protected void togglePassVisibility() {
        if (showPasswordCheck.isSelected()) {
            // show the plain text field and hide the PasswordField
            userPassTextFld.setText(userPassFld.getText());
            userPassTextFld.setVisible(true);
            userPassTextFld.setManaged(true);
            userPassFld.setVisible(false);
            userPassFld.setManaged(false);
        } else {
            // show the PasswordField and hide the plain text field
            userPassFld.setText(userPassTextFld.getText());
            userPassFld.setVisible(true);
            userPassFld.setManaged(true);
            userPassTextFld.setVisible(false);
            userPassTextFld.setManaged(false);
        }
    }

    public String getPasswordTxt() {
        if (userPassTextFld.isVisible()) {return userPassTextFld.getText();}
        else {return userPassFld.getText();}
    }
    @FXML
    protected void onForgotCradentialsClick(){

        ///todo if the user forgot password
    }
}