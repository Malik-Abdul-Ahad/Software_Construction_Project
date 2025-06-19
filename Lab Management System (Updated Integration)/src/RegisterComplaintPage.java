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
        labComboBox.getItems().addAll("Lab 1", "Lab 2", "Lab 3"); // Populate dynamically in real implementation
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

        // Simulate validation (e.g., check computer exists in lab)
        if (!computerId.startsWith("PC")) {
            statusLabel.setText("The selected computer does not exist in the chosen lab. Please verify your selection.");
            return;
        }

        // Simulate duplicate check
        if (computerId.equalsIgnoreCase("PC101") && category.equals("Hardware Issue")) {
            statusLabel.setText("A similar complaint for this computer is already registered and pending. You can track it using Complaint ID C999.");
            return;
        }

        // Create the complaint details
        String complaintDetails = category + ": " + description;
        
        // In a real implementation, we would use the controller to register the complaint
        // controller.registerComplaint(user.getUserID(), Integer.parseInt(computerId.replace("PC", "")), complaintDetails);
        
        // Simulate database insert and ID generation
        String complaintId = "C" + (int)(Math.random() * 900 + 100);
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Complaint registered successfully. Your Complaint ID is " + complaintId + ".");

        // Reset form
        labComboBox.getSelectionModel().clearSelection();
        computerIdField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        descriptionArea.clear();
        attachmentLabel.setText("No file attached");
    }
}
