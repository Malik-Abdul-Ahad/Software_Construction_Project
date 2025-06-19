import javafx.application.Application;
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

public class GenerateLabReportPage extends Application {

    private TableView<LabReport> table;
    private ObservableList<LabReport> labReports;
    private VBox reportBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Generate Lab Report");
        titleLabel.setFont(Font.font("Arial", 20));
        titleLabel.setTextFill(Color.web("#0047AB"));

        ComboBox<String> reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll("Summary Report", "Detailed Report");
        reportTypeCombo.setPromptText("Select Report Type");
        reportTypeCombo.setMaxWidth(200);

        ComboBox<String> labCombo = new ComboBox<>();
        labCombo.getItems().addAll("All Labs", "Lab A", "Lab B", "Lab C", "Lab D", "Lab E");
        labCombo.setPromptText("Select Lab");
        labCombo.setMaxWidth(200);

        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle(
                "-fx-background-color: #fdb31e; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 6 20 6 20;");

        Button downloadBtn = new Button("Download Report");
        downloadBtn.setStyle(
                "-fx-background-color: #fdb31e; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 6 20 6 20;");
        downloadBtn.setDisable(true);

        Button printBtn = new Button("Print Report");
        printBtn.setStyle(
                "-fx-background-color: #fdb31e; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 6 20 6 20;");
        printBtn.setDisable(true);

        HBox filterBox = new HBox(15, reportTypeCombo, labCombo, generateBtn, downloadBtn, printBtn);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setPadding(new Insets(20, 0, 0, 0));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        labReports = FXCollections.observableArrayList();
        table.setItems(labReports);

        reportBox = new VBox(10, table);
        reportBox.setPadding(new Insets(20));
        reportBox.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-background-color: #f9f9f9;");
        reportBox.setVisible(false);

        generateBtn.setOnAction(e -> {
            String type = reportTypeCombo.getValue();
            String lab = labCombo.getValue();

            if (type == null || lab == null) {
                showAlert("Please select both Report Type and Lab.");
                return;
            }

            labReports.clear();
            table.getColumns().clear();

            if (lab.equals("Lab A") || lab.equals("All Labs")) {
                labReports.add(new LabReport("L-101", "Lab A", "Practical", "2025-05-01 10:00", "20", "Microscopes",
                        "2 hrs", "Active"));
                labReports.add(new LabReport("L-102", "Lab A", "Simulation", "2025-05-03 14:00", "15", "Simulators",
                        "3 hrs", "Closed"));
            }
            if (lab.equals("Lab B") || lab.equals("All Labs")) {
                labReports.add(new LabReport("L-201", "Lab B", "Practical", "2025-04-22 09:00", "18", "Centrifuges",
                        "2.5 hrs", "Active"));
            }
            if (lab.equals("Lab C") || lab.equals("All Labs")) {
                labReports.add(new LabReport("L-301", "Lab C", "Simulation", "2025-05-05 11:00", "22", "Simulators",
                        "4 hrs", "Active"));
            }
            if (lab.equals("Lab D") || lab.equals("All Labs")) {
                labReports.add(new LabReport("L-401", "Lab D", "Practical", "2025-05-07 13:00", "12", "3D Printers",
                        "1.5 hrs", "Under Maintenance"));
            }
            if (lab.equals("Lab E") || lab.equals("All Labs")) {
                labReports.add(new LabReport("L-501", "Lab E", "Practical", "2025-05-10 15:00", "25", "Robotics Kits",
                        "3 hrs", "Active"));
            }

            if (labReports.isEmpty()) {
                showAlert("No lab records found for the selected criteria.");
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
                if (success)
                    job.endJob();
            }
        });

        VBox layout = new VBox(20, titleLabel, filterBox, reportBox);
        layout.setStyle("-fx-background-color: white;");
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 1100, 600);
        stage.setTitle("Generate Lab Reports");
        stage.setScene(scene);
        stage.show();
    }

    private void loadColumns(String reportType) {
        table.getColumns().clear();

        TableColumn<LabReport, String> col1 = new TableColumn<>("Lab ID");
        col1.setCellValueFactory(data -> data.getValue().labIDProperty());

        TableColumn<LabReport, String> col2 = new TableColumn<>("Lab Name");
        col2.setCellValueFactory(data -> data.getValue().labNameProperty());

        TableColumn<LabReport, String> col8 = new TableColumn<>("Status");
        col8.setCellValueFactory(data -> data.getValue().statusProperty());

        if (reportType.equalsIgnoreCase("Summary Report")) {
            table.getColumns().addAll(col1, col2, col8);
        } else {
            TableColumn<LabReport, String> col3 = new TableColumn<>("Experiment Type");
            col3.setCellValueFactory(data -> data.getValue().experimentTypeProperty());

            TableColumn<LabReport, String> col4 = new TableColumn<>("Date/Time");
            col4.setCellValueFactory(data -> data.getValue().datetimeProperty());

            TableColumn<LabReport, String> col5 = new TableColumn<>("Participants");
            col5.setCellValueFactory(data -> data.getValue().participantsProperty());

            TableColumn<LabReport, String> col6 = new TableColumn<>("Equipment");
            col6.setCellValueFactory(data -> data.getValue().equipmentProperty());

            TableColumn<LabReport, String> col7 = new TableColumn<>("Usage Hours");
            col7.setCellValueFactory(data -> data.getValue().usageHoursProperty());

            table.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7, col8);
        }
    }

    private void downloadReport(Stage stage, String reportType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Lab Report");
        fileChooser.setInitialFileName("LabReport.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (LabReport r : labReports) {
                    writer.write("Lab ID: " + r.labIDProperty().get() + "\n");
                    writer.write("Lab Name: " + r.labNameProperty().get() + "\n");
                    if (reportType.equalsIgnoreCase("Detailed Report")) {
                        writer.write("Experiment Type: " + r.experimentTypeProperty().get() + "\n");
                        writer.write("Date & Time: " + r.datetimeProperty().get() + "\n");
                        writer.write("Participants: " + r.participantsProperty().get() + "\n");
                        writer.write("Equipment: " + r.equipmentProperty().get() + "\n");
                        writer.write("Usage Hours: " + r.usageHoursProperty().get() + "\n");
                    }
                    writer.write("Status: " + r.statusProperty().get() + "\n");
                    writer.write("-----------------------------------------------------\n");
                }
                showAlert("Report downloaded successfully!");
            } catch (IOException ex) {
                showAlert("Error saving report.");
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lab Report Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class LabReport {
        private final StringProperty labID;
        private final StringProperty labName;
        private final StringProperty experimentType;
        private final StringProperty datetime;
        private final StringProperty participants;
        private final StringProperty equipment;
        private final StringProperty usageHours;
        private final StringProperty status;

        public LabReport(String labID, String labName, String experimentType, String datetime,
                String participants, String equipment, String usageHours, String status) {
            this.labID = new SimpleStringProperty(labID);
            this.labName = new SimpleStringProperty(labName);
            this.experimentType = new SimpleStringProperty(experimentType);
            this.datetime = new SimpleStringProperty(datetime);
            this.participants = new SimpleStringProperty(participants);
            this.equipment = new SimpleStringProperty(equipment);
            this.usageHours = new SimpleStringProperty(usageHours);
            this.status = new SimpleStringProperty(status);
        }

        public StringProperty labIDProperty() {
            return labID;
        }

        public StringProperty labNameProperty() {
            return labName;
        }

        public StringProperty experimentTypeProperty() {
            return experimentType;
        }

        public StringProperty datetimeProperty() {
            return datetime;
        }

        public StringProperty participantsProperty() {
            return participants;
        }

        public StringProperty equipmentProperty() {
            return equipment;
        }

        public StringProperty usageHoursProperty() {
            return usageHours;
        }

        public StringProperty statusProperty() {
            return status;
        }
    }
}
