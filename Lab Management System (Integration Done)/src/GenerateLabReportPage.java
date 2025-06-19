import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class GenerateLabReportPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    private ComboBox<String> labComboBox;
    private TextArea reportContentArea;
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
        Label titleLabel = new Label("Generate Lab Report");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.DARKSLATEGRAY);
        
        // Lab selection
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
        
        // Generate button
        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        generateBtn.setOnAction(e -> generateReport());
        
        // Print button
        Button printBtn = new Button("Print Report");
        printBtn.setStyle("-fx-background-color: #4CAF50;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        printBtn.setOnAction(e -> printReport());
        printBtn.setDisable(true); // Initially disabled until report is generated
        
        // Report content area
        reportContentArea = new TextArea();
        reportContentArea.setEditable(false);
        reportContentArea.setPrefRowCount(20);
        reportContentArea.setWrapText(true);
        reportContentArea.setPromptText("Generated report will appear here");
        
        // Status label for feedback
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setTextFill(Color.FIREBRICK);
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #0077B6;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        backButton.setOnAction(e -> mainApp.showLabAssistantDashboard(user));
        
        // Layout for lab selection
        HBox selectionBox = new HBox(10, labLabel, labComboBox, generateBtn, printBtn);
        selectionBox.setAlignment(Pos.CENTER_LEFT);
        selectionBox.setPadding(new Insets(10));
        
        // Button bar
        HBox buttonBar = new HBox(10, backButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10));
        
        // Main content layout
        VBox contentBox = new VBox(10, selectionBox, reportContentArea);
        contentBox.setPadding(new Insets(20));
        
        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(20, 0, 20, 20));
        mainLayout.setCenter(contentBox);
        mainLayout.setBottom(new VBox(10, statusLabel, buttonBar));
        BorderPane.setMargin(statusLabel, new Insets(10, 0, 0, 20));
        
        // Set white background and create scene
        mainLayout.setStyle("-fx-background-color: white;");
        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setTitle("Generate Lab Report");
        stage.setScene(scene);
        stage.show();
    }

    private void generateReport() {
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
        
        // Generate the lab report
        Report report = controller.generateLabReport(labId);
        
        // Build a formatted report text
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("LAB REPORT\n");
        reportBuilder.append("=========================================\n\n");
        reportBuilder.append("Lab: ").append(selectedLab).append("\n");
        reportBuilder.append("Report ID: ").append(report.getReportID()).append("\n");
        reportBuilder.append("Date Generated: ").append(LocalDate.now()).append("\n");
        reportBuilder.append("Generated By: ").append(user.getUsername()).append("\n\n");
        
        reportBuilder.append("COMPUTERS IN LAB\n");
        reportBuilder.append("-----------------------------------------\n");
        List<Computer> computers = controller.getComputersInLab(labId);
        if (computers.isEmpty()) {
            reportBuilder.append("No computers found in this lab.\n");
        } else {
            for (Computer computer : computers) {
                reportBuilder.append("Computer ID: PC").append(computer.getComputerID()).append("\n");
                reportBuilder.append("Hardware Details: ").append(computer.getHardwareDetails()).append("\n");
                
                // List installed software
                List<Software> softwareList = controller.getInstalledSoftware(computer.getComputerID());
                reportBuilder.append("Installed Software: ");
                if (softwareList.isEmpty()) {
                    reportBuilder.append("None\n");
                } else {
                    reportBuilder.append("\n");
                    for (Software software : softwareList) {
                        reportBuilder.append("  - ").append(software.getSoftwareName());
                        reportBuilder.append(" (Installed on: ").append(software.getInstallationDate()).append(")\n");
                    }
                }
                reportBuilder.append("-----------------------------------------\n");
            }
        }
        
        reportBuilder.append("\nEND OF REPORT");
        
        // Display the report
        reportContentArea.setText(reportBuilder.toString());
        
        // Update status
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Lab report generated successfully");
    }
    
    private void printReport() {
        // Simulate printing the report
        if (reportContentArea.getText().isEmpty()) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("No report to print. Generate a report first.");
            return;
        }
        
        // In a real application, this would connect to the printer
        // For now, we'll just show a success message
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Report sent to printer successfully");
    }
}
