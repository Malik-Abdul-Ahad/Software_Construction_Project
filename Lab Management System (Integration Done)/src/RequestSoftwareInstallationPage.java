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
    private ComboBox<String> labComboBox, computerComboBox;
    private TextField softwareNameField, versionField;
    private TextArea reasonField;
    private Label statusLabel;
    
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
        // Page title
        Label titleLabel = new Label("Request Software Installation");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.DARKSLATEGRAY);
        
        // Form elements
        Label labLabel = new Label("Select Lab:");
        labComboBox = new ComboBox<>();
        
        // Get available labs from controller
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
        
        Label computerLabel = new Label("Select Computer:");
        computerComboBox = new ComboBox<>();
        computerComboBox.getItems().addAll("PC101", "PC102", "PC103", "PC104", "PC105");
        
        // Update computer list when lab is selected
        labComboBox.setOnAction(e -> updateComputerList());
        
        Label softwareLabel = new Label("Software Name:");
        softwareNameField = new TextField();
        
        Label versionLabel = new Label("Version:");
        versionField = new TextField();
        
        Label reasonLabel = new Label("Reason for Request:");
        reasonField = new TextArea();
        reasonField.setPrefRowCount(4);
        reasonField.setWrapText(true);
        
        // Submit button
        Button submitButton = new Button("Submit Request");
        submitButton.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        submitButton.setOnAction(e -> submitRequest());
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #0077B6;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        backButton.setOnAction(e -> {
            if (user.getRole() == Role.ADMIN) {
                mainApp.showLabAssistantDashboard(user);
            } else {
                mainApp.showStudentTeacherDashboard(user);
            }
        });
        
        // Status label for feedback
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setTextFill(Color.FIREBRICK);
        
        // Create grid for form layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        // Add form elements to grid
        int row = 0;
        formGrid.add(labLabel, 0, row);
        formGrid.add(labComboBox, 1, row);
        
        row++;
        formGrid.add(computerLabel, 0, row);
        formGrid.add(computerComboBox, 1, row);
        
        row++;
        formGrid.add(softwareLabel, 0, row);
        formGrid.add(softwareNameField, 1, row);
        
        row++;
        formGrid.add(versionLabel, 0, row);
        formGrid.add(versionField, 1, row);
        
        row++;
        formGrid.add(reasonLabel, 0, row);
        formGrid.add(reasonField, 1, row);
        
        // Set column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70);
        formGrid.getColumnConstraints().addAll(column1, column2);
        
        // Button bar
        HBox buttonBar = new HBox(10, submitButton, backButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10));
        
        // Main layout
        VBox mainLayout = new VBox(10, titleLabel, formGrid, statusLabel, buttonBar);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: white;");
        
        Scene scene = new Scene(mainLayout, 900, 500);
        stage.setTitle("Request Software Installation");
        stage.setScene(scene);
        stage.show();
    }

    private void updateComputerList() {
        String selectedLab = labComboBox.getValue();
        if (selectedLab == null) return;
        
        computerComboBox.getItems().clear();
        
        // Find lab ID from lab name
        int labId = -1;
        for (Lab lab : controller.getAllLabs()) {
            if (lab.getLabName().equals(selectedLab)) {
                labId = lab.getLabID();
                break;
            }
        }
        
        // Get computers for selected lab
        if (labId != -1) {
            for (Computer computer : controller.getComputersInLab(labId)) {
                computerComboBox.getItems().add("PC" + computer.getComputerID());
            }
        }
        
        // Add dummy computers if none exist
        if (computerComboBox.getItems().isEmpty()) {
            // Add some computers to the lab
            for (int i = 1; i <= 5; i++) {
                controller.addComputer(labId, "Computer " + i + " in " + selectedLab);
            }
            
            // Reload the list
            for (Computer computer : controller.getComputersInLab(labId)) {
                computerComboBox.getItems().add("PC" + computer.getComputerID());
            }
        }
    }
    
    private void submitRequest() {
        String selectedLab = labComboBox.getValue();
        String selectedComputer = computerComboBox.getValue();
        String softwareName = softwareNameField.getText().trim();
        String version = versionField.getText().trim();
        String reason = reasonField.getText().trim();
        
        if (selectedLab == null || selectedComputer == null || softwareName.isEmpty() || reason.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
            return;
        }
        
        // Extract computer ID
        int computerId;
        try {
            computerId = Integer.parseInt(selectedComputer.replace("PC", ""));
        } catch (NumberFormatException e) {
            computerId = 101; // Default
        }
        
        // Create software object
        Software software = new Software(softwareName + " " + version, LocalDate.now());
        
        // Submit request
        controller.requestSoftware(user.getUserID(), software, computerId);
        
        // Show success message
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Software installation request submitted successfully.");
        
        // Clear form
        softwareNameField.clear();
        versionField.clear();
        reasonField.clear();
    }
}
