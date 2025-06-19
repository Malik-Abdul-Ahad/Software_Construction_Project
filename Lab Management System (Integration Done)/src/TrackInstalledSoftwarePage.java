import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class TrackInstalledSoftwarePage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    private ComboBox<String> labComboBox;
    private ComboBox<String> computerComboBox;
    private TableView<Software> softwareTable;
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
        Label titleLabel = new Label("Track Installed Software");
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
        
        // Button to load software
        Button loadSoftwareBtn = new Button("Load Software");
        loadSoftwareBtn.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        loadSoftwareBtn.setOnAction(e -> loadInstalledSoftware());
        
        // Create software table
        softwareTable = new TableView<>();
        softwareTable.setEditable(false);
        
        TableColumn<Software, Integer> idCol = new TableColumn<>("Software ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("softwareID"));
        idCol.setPrefWidth(100);

        TableColumn<Software, String> nameCol = new TableColumn<>("Software Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("softwareName"));
        nameCol.setPrefWidth(300);

        TableColumn<Software, LocalDate> dateCol = new TableColumn<>("Installation Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("installationDate"));
        dateCol.setPrefWidth(150);
        
        softwareTable.getColumns().addAll(idCol, nameCol, dateCol);
        softwareTable.setPrefHeight(250);
        
        // Add default software if none exists
        ensureSampleSoftwareExists();
        
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
        
        // Set column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70);
        formGrid.getColumnConstraints().addAll(column1, column2);
        
        // Software table container
        VBox tableContainer = new VBox(10, softwareTable);
        tableContainer.setPadding(new Insets(10));
        
        // Button bar
        HBox selectionBar = new HBox(10, loadSoftwareBtn);
        selectionBar.setAlignment(Pos.CENTER_LEFT);
        selectionBar.setPadding(new Insets(10));
        
        HBox buttonBar = new HBox(10, backButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10));
        
        // Main layout
        VBox mainLayout = new VBox(10, titleLabel, formGrid, selectionBar, tableContainer, statusLabel, buttonBar);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setTitle("Track Installed Software");
        stage.setScene(scene);
        stage.show();
    }

    // Ensure some sample software exists in the system
    private void ensureSampleSoftwareExists() {
        List<Software> existingSoftware = controller.getInstalledSoftware(1);
        
        if (existingSoftware.isEmpty()) {
            // Add dummy computers and software for demonstration
            controller.addComputer(1, "Demo Computer");
            
            // Manually create Software objects with the Application_Layer Software class
            controller.requestSoftware(user.getUserID(), new Software("Microsoft Office 2021", LocalDate.now().minusDays(30)), 1);
            controller.requestSoftware(user.getUserID(), new Software("Adobe Photoshop CC", LocalDate.now().minusDays(20)), 1);
            controller.requestSoftware(user.getUserID(), new Software("Visual Studio Code", LocalDate.now().minusDays(10)), 1);
            controller.requestSoftware(user.getUserID(), new Software("IntelliJ IDEA", LocalDate.now().minusDays(5)), 1);
            controller.requestSoftware(user.getUserID(), new Software("Chrome Browser", LocalDate.now()), 1);
        }
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
    
    private void loadInstalledSoftware() {
        String selectedComputer = computerComboBox.getValue();
        if (selectedComputer == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a computer first");
            return;
        }
        
        // Extract computer ID
        int computerId;
        try {
            computerId = Integer.parseInt(selectedComputer.replace("PC", ""));
        } catch (NumberFormatException e) {
            computerId = 1; // Default
        }
        
        // Clear the table
        softwareTable.getItems().clear();
        
        // Load software for selected computer
        List<Software> softwareList = controller.getInstalledSoftware(computerId);
        
        if (softwareList.isEmpty()) {
            statusLabel.setTextFill(Color.BLUE);
            statusLabel.setText("No software installed on this computer");
        } else {
            softwareTable.getItems().addAll(softwareList);
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Loaded " + softwareList.size() + " software items");
        }
    }
}
