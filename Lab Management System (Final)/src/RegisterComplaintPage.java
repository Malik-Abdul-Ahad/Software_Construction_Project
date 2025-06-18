import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class RegisterComplaintPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    private TextField computerIdField;
    private ComboBox<String> labComboBox, categoryComboBox;
    private TextArea descriptionArea;
    private Label attachmentLabel, statusLabel;
    private File attachmentFile;
    
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
        labComboBox = new ComboBox<>();
        labComboBox.getItems().addAll("Lab A", "Lab B", "Lab C", "Lab D", "Lab E"); // Populate dynamically in real implementation
        labComboBox.setPromptText("Select Lab");

        computerIdField = new TextField();
        computerIdField.setPromptText("Enter Computer ID");

        categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Hardware Issue", "Software Issue", "Network Issue");
        categoryComboBox.setPromptText("Select Complaint Category");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter Complaint Description");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setWrapText(true);

        Button attachButton = new Button("Attach File");
        attachButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            attachmentFile = fileChooser.showOpenDialog(stage);
            if (attachmentFile != null) {
                attachmentLabel.setText("Attached: " + attachmentFile.getName());
            }
        });

        attachmentLabel = new Label("No file attached");
        attachmentLabel.setFont(Font.font("Arial", 12));
        attachmentLabel.setTextFill(Color.GRAY);

        Button submitButton = new Button("Submit Complaint");
        submitButton.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        submitButton.setOnAction(e -> submitComplaint());

        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setTextFill(Color.FIREBRICK);
        
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

        VBox form = new VBox(10,
                labComboBox,
                computerIdField,
                categoryComboBox,
                descriptionArea,
                new HBox(10, attachButton, attachmentLabel),
                submitButton,
                statusLabel,
                backButton
        );

        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(form,  900, 550);
        stage.setTitle("Register Complaint");
        stage.setScene(scene);
        stage.show();
    }

    private void submitComplaint() {
        String lab = labComboBox.getValue();
        String computerId = computerIdField.getText().trim();
        String category = categoryComboBox.getValue();
        String description = descriptionArea.getText().trim();

        if (lab == null || computerId.isEmpty() || category == null || description.isEmpty()) {
            statusLabel.setText("Please fill in all required fields before submitting your complaint.");
            return;
        }

        // Validate computer ID format
        if (!computerId.matches("PC\\d{3}")) {
            statusLabel.setText("The selected computer ID must be in the format PC001, PC002, etc. Please verify your selection.");
            return;
        }

        // Create the complaint details
        String complaintDetails = category + ": " + description;
        
        // Extract numeric part from computer ID (e.g., "PC001" -> 1)
        int computerNumericId;
        try {
            computerNumericId = Integer.parseInt(computerId.replace("PC", ""));
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid computer ID format.");
            return;
        }
        
        // Get next complaint ID from database
        int nextComplaintId = controller.getNextComplaintId();
        String formattedComplaintId = "C" + String.format("%03d", nextComplaintId);
        
        // Register the complaint using the controller
        boolean success = controller.registerFormattedComplaint(
            formattedComplaintId,
            category,
            "Pending",
            user.getRole().toString(),
            complaintDetails
        );
        
        if (success) {
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Complaint registered successfully. Your Complaint ID is " + formattedComplaintId + ".");
            
            // Reset form
            labComboBox.getSelectionModel().clearSelection();
            computerIdField.clear();
            categoryComboBox.getSelectionModel().clearSelection();
            descriptionArea.clear();
            attachmentLabel.setText("No file attached");
        } else {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Failed to register complaint. Please try again later.");
        }
    }
}
