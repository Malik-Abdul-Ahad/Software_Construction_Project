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
       
        // Get available labs from controller
        labComboBox = new ComboBox<>();
        for (Lab lab : controller.getAllLabs()) {
            labComboBox.getItems().add(lab.getLabName());
        }
        // Add default labs if none exist
        if (labComboBox.getItems().isEmpty()) {
            controller.addLab("Lab 1");
            controller.addLab("Lab 2");
            controller.addLab("Lab 3");
            for (Lab lab : controller.getAllLabs()) {
                labComboBox.getItems().add(lab.getLabName());
            }
        }
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

        // Page title
        Label titleLabel = new Label("Register Complaint");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.DARKSLATEGRAY);
        
        VBox form = new VBox(10,
                titleLabel,
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

        Scene scene = new Scene(form, 900, 500);
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

        // Create the complaint details
        String complaintDetails = category + ": " + description;
        
        // Get lab ID from lab name
        int labId = -1;
        for (Lab labObj : controller.getAllLabs()) {
            if (labObj.getLabName().equals(lab)) {
                labId = labObj.getLabID();
                break;
            }
        }
        
        // Get computer ID (try to find it or create a dummy one for this demo)
        int computerIdNum;
        try {
            computerIdNum = Integer.parseInt(computerId.replace("PC", ""));
        } catch (NumberFormatException e) {
            computerIdNum = (int)(Math.random() * 900 + 100);
        }
        
        // Make sure computer exists in the lab
        boolean computerExists = false;
        for (Computer computer : controller.getComputersInLab(labId)) {
            if (computer.getComputerID() == computerIdNum) {
                computerExists = true;
                break;
            }
        }
        
        // If computer doesn't exist, add it to the lab
        if (!computerExists) {
            controller.addComputer(labId, "Default hardware for computer " + computerId);
        }
        
        // Register the complaint in the system
        controller.registerComplaint(user.getUserID(), computerIdNum, complaintDetails);
        
        // Show success message
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
