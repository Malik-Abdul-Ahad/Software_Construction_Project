import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateHardwareReportPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    
    private TableView<HardwareReport> table;
    private ObservableList<HardwareReport> hardwareReports;
    private VBox reportBox;
    
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
        ComboBox<String> reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll("Summary Report", "Detailed Report");
        reportTypeCombo.setPromptText("Select Report Type");
        reportTypeCombo.setMaxWidth(200);

        ComboBox<String> labCombo = new ComboBox<>();
        labCombo.getItems().addAll("All Labs", "Lab A", "Lab B", "Lab C", "Lab D", "Lab E");
        labCombo.setPromptText("Select Lab");
        labCombo.setMaxWidth(200);

        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #fdb31e; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 6 20 6 20;");

        Button downloadBtn = new Button("Download Report");
        downloadBtn.setStyle("-fx-background-color: #fdb31e; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 6 20 6 20;");
        downloadBtn.setDisable(true);

        Button printBtn = new Button("Print Report");
        printBtn.setStyle("-fx-background-color: #fdb31e; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 6 20 6 20;");
        printBtn.setDisable(true);

        HBox filterBox = new HBox(15, reportTypeCombo, labCombo, generateBtn, downloadBtn, printBtn);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setPadding(new Insets(20, 0, 0, 0));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        hardwareReports = FXCollections.observableArrayList();
        table.setItems(hardwareReports);

        reportBox = new VBox(10, table);
        reportBox.setPadding(new Insets(20));
        reportBox.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-background-color: #f9f9f9;");
        reportBox.setVisible(false);
        
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

        generateBtn.setOnAction(e -> {
            String type = reportTypeCombo.getValue();
            String lab = labCombo.getValue();
            
            if (type == null || lab == null) {
                showAlert("Please select both Report Type and Lab.");
                return;
            }

            hardwareReports.clear();
            table.getColumns().clear();

            // Add multiple PCs for Lab A
            if (lab.equals("Lab A") || lab.equals("All Labs")) {
                hardwareReports.add(new HardwareReport("PC001", "Lab A", "Intel i5 3.0GHz", "8GB DDR4", "SSD 256GB", "None", "Active"));
                hardwareReports.add(new HardwareReport("PC002", "Lab A", "Intel i7 3.6GHz", "16GB DDR4", "SSD 512GB", "GTX 1650", "Under Maintenance"));
                hardwareReports.add(new HardwareReport("PC003", "Lab A", "AMD Ryzen 5 3.4GHz", "8GB DDR4", "HDD 1TB", "None", "Active"));
                hardwareReports.add(new HardwareReport("PC004", "Lab A", "Intel i3 2.8GHz", "4GB DDR3", "HDD 500GB", "None", "Decommissioned"));
            }

            // Add multiple PCs for Lab B
            if (lab.equals("Lab B") || lab.equals("All Labs")) {
                hardwareReports.add(new HardwareReport("PC005", "Lab B", "Intel i5 3.2GHz", "8GB DDR4", "SSD 256GB", "NVIDIA GTX 1050", "Active"));
                hardwareReports.add(new HardwareReport("PC006", "Lab B", "Intel i7 4.0GHz", "32GB DDR4", "SSD 1TB", "NVIDIA RTX 2060", "Active"));
                hardwareReports.add(new HardwareReport("PC007", "Lab B", "AMD Ryzen 7 3.8GHz", "16GB DDR4", "SSD 512GB", "NVIDIA GTX 1660", "Under Maintenance"));
            }

            // Add multiple PCs for Lab C
            if (lab.equals("Lab C") || lab.equals("All Labs")) {
                hardwareReports.add(new HardwareReport("PC008", "Lab C", "Intel i9 4.2GHz", "64GB DDR4", "NVMe 1TB", "NVIDIA RTX 3080", "Active"));
                hardwareReports.add(new HardwareReport("PC009", "Lab C", "AMD Ryzen 9 4.1GHz", "32GB DDR4", "NVMe 512GB", "NVIDIA RTX 3070", "Active"));
                hardwareReports.add(new HardwareReport("PC010", "Lab C", "Intel Xeon 3.6GHz", "128GB DDR4", "NVMe 2TB", "None", "Under Maintenance"));
                hardwareReports.add(new HardwareReport("PC011", "Lab C", "Intel i7 3.8GHz", "16GB DDR4", "SSD 512GB", "NVIDIA GTX 1650", "Active"));
                hardwareReports.add(new HardwareReport("PC012", "Lab C", "AMD Ryzen 5 3.5GHz", "8GB DDR4", "HDD 1TB", "None", "Active"));
            }
            
            // Add multiple PCs for Lab D
            if (lab.equals("Lab D") || lab.equals("All Labs")) {
                hardwareReports.add(new HardwareReport("PC013", "Lab D", "Intel i5 3.4GHz", "16GB DDR4", "SSD 512GB", "NVIDIA GTX 1660", "Active"));
                hardwareReports.add(new HardwareReport("PC014", "Lab D", "AMD Ryzen 7 3.7GHz", "32GB DDR4", "NVMe 1TB", "NVIDIA RTX 2070", "Under Maintenance"));
            }
            
            // Add multiple PCs for Lab E
            if (lab.equals("Lab E") || lab.equals("All Labs")) {
                hardwareReports.add(new HardwareReport("PC015", "Lab E", "Intel i7 3.9GHz", "32GB DDR4", "NVMe 1TB", "NVIDIA RTX 3060", "Active"));
                hardwareReports.add(new HardwareReport("PC016", "Lab E", "AMD Ryzen 9 4.0GHz", "64GB DDR4", "NVMe 2TB", "NVIDIA RTX 3080", "Active"));
            }

            if (hardwareReports.isEmpty()) {
                showAlert("No hardware records found for the selected criteria.");
                reportBox.setVisible(false);
                downloadBtn.setDisable(true);
                printBtn.setDisable(true);
            } else {
                loadColumns(type);
                reportBox.setVisible(true);
                downloadBtn.setDisable(false);
                printBtn.setDisable(false);
            }
        });

        downloadBtn.setOnAction(e -> downloadReport(stage, reportTypeCombo.getValue()));

        printBtn.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(stage)) {
                boolean success = job.printPage(reportBox);
                if (success) job.endJob();
            }
        });

        VBox layout = new VBox(20, filterBox, reportBox, bottomBox);
        layout.setStyle("-fx-background-color: white;");
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout,  900, 550);
        stage.setTitle("Generate Hardware Reports");
        stage.setScene(scene);
        stage.show();
    }

    private void loadColumns(String reportType) {
        table.getColumns().clear();

        TableColumn<HardwareReport, String> col1 = new TableColumn<>("Computer ID");
        col1.setCellValueFactory(data -> data.getValue().computerIDProperty());

        TableColumn<HardwareReport, String> col2 = new TableColumn<>("Lab Name");
        col2.setCellValueFactory(data -> data.getValue().labNumberProperty());

        TableColumn<HardwareReport, String> col7 = new TableColumn<>("Status");
        col7.setCellValueFactory(data -> data.getValue().statusProperty());

        if (reportType.equalsIgnoreCase("Summary Report")) {
            table.getColumns().addAll(col1, col2, col7);
        } else {
            TableColumn<HardwareReport, String> col3 = new TableColumn<>("Processor");
            col3.setCellValueFactory(data -> data.getValue().processorProperty());

            TableColumn<HardwareReport, String> col4 = new TableColumn<>("RAM");
            col4.setCellValueFactory(data -> data.getValue().ramProperty());

            TableColumn<HardwareReport, String> col5 = new TableColumn<>("Storage");
            col5.setCellValueFactory(data -> data.getValue().storageProperty());

            TableColumn<HardwareReport, String> col6 = new TableColumn<>("GPU");
            col6.setCellValueFactory(data -> data.getValue().gpuProperty());

            table.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7);
        }
    }

    private void downloadReport(Stage stage, String reportType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.setInitialFileName("HardwareReport.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (HardwareReport r : hardwareReports) {
                    writer.write("Computer ID: " + r.computerIDProperty().get() + "\n");
                    writer.write("Lab Name: " + r.labNumberProperty().get() + "\n");
                    if (reportType.equalsIgnoreCase("Detailed Report")) {
                        writer.write("Processor: " + r.processorProperty().get() + "\n");
                        writer.write("RAM: " + r.ramProperty().get() + "\n");
                        writer.write("Storage: " + r.storageProperty().get() + "\n");
                        writer.write("GPU: " + r.gpuProperty().get() + "\n");
                    }
                    writer.write("Status: " + r.statusProperty().get() + "\n");
                    writer.write("---------------------------------------------\n");
                }
                showAlert("Report downloaded successfully!");
            } catch (IOException ex) {
                showAlert("Error saving report.");
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class HardwareReport {
        private final StringProperty computerID;
        private final StringProperty labName;
        private final StringProperty processor;
        private final StringProperty ram;
        private final StringProperty storage;
        private final StringProperty gpu;
        private final StringProperty status;

        public HardwareReport(String computerID, String labName, String processor, String ram,
                              String storage, String gpu, String status) {
            this.computerID = new SimpleStringProperty(computerID);
            this.labName = new SimpleStringProperty(labName);
            this.processor = new SimpleStringProperty(processor);
            this.ram = new SimpleStringProperty(ram);
            this.storage = new SimpleStringProperty(storage);
            this.gpu = new SimpleStringProperty(gpu);
            this.status = new SimpleStringProperty(status);
        }

        public StringProperty computerIDProperty() { return computerID; }
        public StringProperty labNumberProperty() { return labName; }
        public StringProperty processorProperty() { return processor; }
        public StringProperty ramProperty() { return ram; }
        public StringProperty storageProperty() { return storage; }
        public StringProperty gpuProperty() { return gpu; }
        public StringProperty statusProperty() { return status; }
    }
}
