import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.LocalDate;

public class RequestSoftwareInstallationPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public void initialize(Stage stage) {
        // Title
        Label title = new Label("Request Software Installation");
        title.setFont(Font.font("Arial", 20));
        title.setStyle("-fx-text-fill:rgb(0, 0, 0);");
        title.setPadding(new Insets(10));

        // Software Name
        TextField softwareNameField = new TextField();
        softwareNameField.setPromptText("Software Name");
        softwareNameField.setMaxWidth(400);

        // Version
        TextField versionField = new TextField();
        versionField.setPromptText("Version (optional)");
        versionField.setMaxWidth(400);

        // Request Type (Radio Buttons)
        Label requestTypeLabel = new Label("Request Type:");
        requestTypeLabel.setFont(Font.font("Arial", 14));
        
        ToggleGroup requestTypeGroup = new ToggleGroup();
        RadioButton singleComputerBtn = new RadioButton("Single Computer");
        RadioButton entireLabBtn = new RadioButton("Entire Lab");
        singleComputerBtn.setToggleGroup(requestTypeGroup);
        entireLabBtn.setToggleGroup(requestTypeGroup);

        HBox requestTypeBox = new HBox(15, singleComputerBtn, entireLabBtn);
        requestTypeBox.setAlignment(Pos.CENTER);

        // Computer ID (for Single Computer)
        TextField computerIdField = new TextField();
        computerIdField.setPromptText("Computer ID");
        computerIdField.setMaxWidth(400);
        computerIdField.setDisable(true);

        // Lab Selection (for both cases)
        ComboBox<String> labComboBox = new ComboBox<>();
        labComboBox.setPromptText("Select Lab");
        labComboBox.getItems().addAll("Lab A", "Lab B", "Lab C", "Lab D", "Lab E");
        labComboBox.setMaxWidth(400);
        labComboBox.setDisable(true);

        // Justification (optional)
        TextArea justificationField = new TextArea();
        justificationField.setPromptText("Justification (optional)");
        justificationField.setWrapText(true);
        justificationField.setMaxWidth(500);
        justificationField.setPrefRowCount(4);

        // Submit Button
        Button submitBtn = new Button("Submit Request");
        submitBtn.setStyle(
                "-fx-background-color: #fdb31e;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 6 20 6 20;"
        );

        // Request Type Selection Logic
        singleComputerBtn.setOnAction(e -> {
            computerIdField.setDisable(false);
            labComboBox.setDisable(false);
        });

        entireLabBtn.setOnAction(e -> {
            computerIdField.setDisable(true);
            computerIdField.clear();
            labComboBox.setDisable(false);
        });

        // Submit Button Logic
        submitBtn.setOnAction(e -> {
            String softwareName = softwareNameField.getText().trim();
            String version = versionField.getText().trim();
            String computerId = computerIdField.getText().trim();
            String lab = labComboBox.getValue();
            String justification = justificationField.getText().trim();

            // Validation
            if (softwareName.isEmpty() || requestTypeGroup.getSelectedToggle() == null ||
                    (singleComputerBtn.isSelected() && (computerId.isEmpty() || lab == null)) ||
                    (entireLabBtn.isSelected() && lab == null)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Fields");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all required fields before submitting your request.");
                alert.showAndWait();
                return;
            }

            // Simulated success
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Request Submitted");
            success.setHeaderText(null);
            success.setContentText("Software installation request submitted successfully.");
            success.showAndWait();

            // Clear fields
            softwareNameField.clear();
            versionField.clear();
            computerIdField.clear();
            labComboBox.getSelectionModel().clearSelection();
            justificationField.clear();
            requestTypeGroup.selectToggle(null);
            computerIdField.setDisable(true);
            labComboBox.setDisable(true);
        });
        
        // Button to go back to dashboard
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #0077B6;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        backButton.setOnAction(e -> {
            if (user.getRole() == Role.ADMIN) {
                mainApp.showLabAssistantDashboard(user);
            } else {
                mainApp.showStudentTeacherDashboard(user);
            }
        });
        
        HBox bottomBox = new HBox(10, backButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));

        VBox formLayout = new VBox(15,
                title,
                softwareNameField,
                versionField,
                requestTypeLabel, requestTypeBox,
                computerIdField,
                labComboBox,
                justificationField,
                submitBtn,
                bottomBox
        );

        formLayout.setAlignment(Pos.TOP_CENTER);
        formLayout.setPadding(new Insets(30));
        formLayout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(formLayout,  900, 550);
        stage.setTitle("Request Software Installation");
        stage.setScene(scene);
        stage.show();
    }
}
