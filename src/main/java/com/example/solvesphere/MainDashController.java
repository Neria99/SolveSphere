package com.example.solvesphere;
import com.example.solvesphere.DataBaseUnit.ProblemDAO;
import com.example.solvesphere.DataBaseUnit.ProblemDAOImpl;
import com.example.solvesphere.DataBaseUnit.UserDAO;
import com.example.solvesphere.DataBaseUnit.UserDAOImpl;
import com.example.solvesphere.ServerUnit.ServerCommunicator;
import com.example.solvesphere.UserData.Problem;
import com.example.solvesphere.UserData.User;
import javafx.application.Platform;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class MainDashController {
    @FXML
    private ImageView profileImg;
    @FXML
    private VBox problemListContainer;
    @FXML
    private ComboBox<String> filterOptions;
    @FXML
    private TextField searchField;
    private ScheduledExecutorService scheduler;
    private User currentUser;  //connected user e.g current user

    public void initUserData(User user) {
        this.currentUser = user;
        buildImage(currentUser);
        fetchAndDisplayProblems();


    }
    private void envokeAllProblemsDisplay() {
        ServerCommunicator serverCommunicator = new ServerCommunicator();
        List<Problem> allProblems = serverCommunicator.sendFetchProblemsRequest("FETCH_PROBLEMS");
        Platform.runLater(() -> {
            System.out.println("Updating UI with new problem/s data.");
            displayProblems(allProblems);
        });
    }


    public void displayProblems(List<Problem> problems) {
            UserDAO userDAO = new UserDAOImpl();
            problemListContainer.getChildren().clear(); //clear existing problems
            for (Problem problem : problems) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProblemItem.fxml"));
                        VBox problemItem = loader.load();

                        ProblemItemController controller = loader.getController();
                        User problemUser = userDAO.getUserById(problem.getUserId()); ///fixed

                        //use the provided method to check if the current user posted this problem
                        boolean isCurrentUserThePublisher = checkCurrentUserAgainstPublisher(problemUser.getEmail(), currentUser.getEmail());
                        String problemPublisherName = isCurrentUserThePublisher ? "You" : problemUser.getUsername();

                        controller.setProblemData(problem,problemPublisherName,currentUser);
                        problemListContainer.getChildren().add(problemItem);
                    } catch (IOException e) {e.printStackTrace();}
                }
        }


    public void fetchAndDisplayProblems() {
        ServerCommunicator serverCommunicator = new ServerCommunicator();
        List<Problem> allProblems = serverCommunicator.sendFetchProblemsRequest("FETCH_PROBLEMS");
        Platform.runLater(() -> displayProblems(allProblems));  // ensure UI updates are done on the JavaFX Application Thread
    }

    public boolean checkCurrentUserAgainstPublisher(String emailA , String emailB){
        System.out.println(emailA+" " + emailB); //debug
        return emailB.equals(emailA);}

    @FXML
    private void handleFilterSelection() {
        String selectedFilter = filterOptions.getValue();
        System.out.println("Selected Filter: " + selectedFilter);
        applyFilter(selectedFilter);
    }

    private void applyFilter(String filter) {
        ProblemDAO problemDAO = new ProblemDAOImpl();
        UserDAO userDAO = new UserDAOImpl();;
        switch (filter) {
            case "No Filter":
                envokeAllProblemsDisplay();
                break;
            case "Interests Related":
                    List<Problem> problems = problemDAO.getProblemsByUserInterest(currentUser.getFieldsOfInterest());
                    displayProblems(problems);
                break;
            case "In Your Country":
                if (currentUser != null) {
                    List<Problem> problemsByCountry = problemDAO.getProblemsByCountry(currentUser.getCountry());
                    displayProblems(problemsByCountry);
                }
            case "Posted By You":
                assert currentUser != null;
                long userId = userDAO.getUserIdByUsernameAndEmail(currentUser.getUsername(),currentUser.getEmail());
                problems = problemDAO.getProblemsPostedByCurrentUser(userId);
                displayProblems(problems);
                break;
        }
    }

    public void addProblem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addProblem.fxml"));
            VBox addProblemRoot = loader.load();

            //new problem scene
            Scene scene = new Scene(addProblemRoot);
            Stage stage = new Stage();
            stage.setTitle("Add New Problem");
            stage.setScene(scene);
           AddProblemController controller = loader.getController();
           controller.setCurrentUser(currentUser);
            stage.setOnHidden(event -> fetchAndDisplayProblems());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchForInput() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            envokeAllProblemsDisplay();
            return;
        }
        ProblemDAO problemDAO = new ProblemDAOImpl();

        // searching for suitable problems via query
        List<Problem> matchingProblems = problemDAO.searchProblems(searchText);
        if (matchingProblems.isEmpty()) {
            System.out.println("No problems were found according to the search: " + searchText);
            problemListContainer.getChildren().clear(); //clearing the problem list

        } else {
            // presenting the appropriate problems
            displayProblems(matchingProblems);
        }
    }

    private void buildImage(User user){
        String profilePicturePath = user.getProfilePicture();

        if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
            try {
                //convert the string path to an Image
                Image image = new Image(profilePicturePath);
                profileImg.setImage(image);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid image path: " + profilePicturePath);
                profileImg.setImage(new Image("G:\\My Drive\\solveSphere\\userico.png"));
            }
        } else {
            //default image (null or empty)
            profileImg.setImage(new Image("G:\\My Drive\\solveSphere\\userico.png"));
        }
    }

    @FXML
    public void onHomeClick() {
    }

    @FXML
    public void onProfileClick() {
    }

    public void onLogoutClick(ActionEvent actionEvent) {
    }

    public void onSettingsClick(ActionEvent actionEvent) {
    }

    public void onFilterClick(ActionEvent actionEvent) {
    }

    @FXML
    private void onHoverEnter(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #555555; -fx-text-fill: white;-fx-font-size:15;");
    }

    @FXML
    private void onHoverExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;-fx-font-size:14;");
    }

}
