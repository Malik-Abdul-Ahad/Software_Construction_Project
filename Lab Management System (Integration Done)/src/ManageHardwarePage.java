import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ManageHardwarePage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    private ComboBox<String> labComboBox;
    private ComboBox<String> computerComboBox;
    private TextArea hardwareDetailsArea;
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
        Label titleLabel = new Label("Manage Hardware");
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
        
        // Update computer list when lab is selected
        labComboBox.setOnAction(e -> updateComputerList());
        
        // Update hardware details when computer is selected
        computerComboBox.setOnAction(e -> loadHardwareDetails());
        
        Label detailsLabel = new Label("Hardware Details:");
        hardwareDetailsArea = new TextArea();
        hardwareDetailsArea.setPrefRowCount(8);
        hardwareDetailsArea.setWrapText(true);
        
        // Update button
        Button updateButton = new Button("Update Hardware Details");
        updateButton.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        updateButton.setOnAction(e -> updateHardwareDetails());
        
        // Add new computer button
        Button addComputerButton = new Button("Add New Computer");
        addComputerButton.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        addComputerButton.setOnAction(e -> addNewComputer());
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #0077B6;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        backButton.setOnAction(e -> mainApp.showLabAssistantDashboard(user));
        
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
        formGrid.add(detailsLabel, 0, row);
        formGrid.add(hardwareDetailsArea, 1, row);
        
        // Set column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70);
        formGrid.getColumnConstraints().addAll(column1, column2);
        
        // Button bar
        HBox buttonBar = new HBox(10, updateButton, addComputerButton, backButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10));
        
        // Main layout
        VBox mainLayout = new VBox(10, titleLabel, formGrid, statusLabel, buttonBar);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: white;");
        
        Scene scene = new Scene(mainLayout, 900, 500);
        stage.setTitle("Manage Hardware");
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
                controller.addComputer(labId, "Intel Core i5, 16GB RAM, 512GB SSD, NVIDIA GTX 1050");
            }
            
            // Reload the list
            for (Computer computer : controller.getComputersInLab(labId)) {
                computerComboBox.getItems().add("PC" + computer.getComputerID());
            }
        }
    }
    
    private void loadHardwareDetails() {
        String selectedComputer = computerComboBox.getValue();
        if (selectedComputer == null) return;
        
        // Extract computer ID
        int computerId;
        try {
            computerId = Integer.parseInt(selectedComputer.replace("PC", ""));
        } catch (NumberFormatException e) {
            return;
        }
        
        // Find selected lab ID
        String selectedLab = labComboBox.getValue();
        int labId = -1;
        for (Lab lab : controller.getAllLabs()) {
            if (lab.getLabName().equals(selectedLab)) {
                labId = lab.getLabID();
                break;
            }
        }
        
        // Find computer in lab
        for (Computer computer : controller.getComputersInLab(labId)) {
            if (computer.getComputerID() == computerId) {
                hardwareDetailsArea.setText(computer.getHardwareDetails());
                return;
            }
        }
        
        hardwareDetailsArea.setText("No hardware details found for this computer.");
    }
    
    private void updateHardwareDetails() {
        String selectedComputer = computerComboBox.getValue();
        if (selectedComputer == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a computer to update");
            return;
        }
        
        String hardwareDetails = hardwareDetailsArea.getText().trim();
        if (hardwareDetails.isEmpty()) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Hardware details cannot be empty");
            return;
        }
        
        // Extract computer ID
        int computerId;
        try {
            computerId = Integer.parseInt(selectedComputer.replace("PC", ""));
        } catch (NumberFormatException e) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Invalid computer ID");
            return;
        }
        
        // Update hardware details
        controller.updateComputerDetails(computerId, hardwareDetails);
        
        // Show success message
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Hardware details updated successfully for " + selectedComputer);
    }
    
    private void addNewComputer() {
        String selectedLab = labComboBox.getValue();
        if (selectedLab == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a lab first");
            return;
        }
        
        // Find lab ID from lab name
        int labId = -1;
        for (Lab lab : controller.getAllLabs()) {
            if (lab.getLabName().equals(selectedLab)) {
                labId = lab.getLabID();
                break;
            }
        }
        
        if (labId == -1) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Lab not found");
            return;
        }
        
        // Default hardware details
        String defaultDetails = "New computer: Intel Core i5, 16GB RAM, 512GB SSD";
        
        // Add new computer
        controller.addComputer(labId, defaultDetails);
        
        // Update computer list
        updateComputerList();
        
        // Show success message
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("New computer added to " + selectedLab);
    }
}
