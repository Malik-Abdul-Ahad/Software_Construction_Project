import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class ManageComputerDetailsPage extends Application {

    private ObservableList<Computer> computers = FXCollections.observableArrayList();

    private TextField idField;
    private TextField specField;
    private TextField labField;
    private TextField statusField;

    private Label messageLabel;

    private TableView<Computer> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Manage Computer Details");
        titleLabel.setFont(Font.font("Arial", 20));
        titleLabel.setTextFill(Color.web("#0047AB"));

        // Input fields
        idField = new TextField();
        idField.setPromptText("Computer ID");
        idField.setMaxWidth(200);
        idField.setFont(Font.font("Arial", 14));

        specField = new TextField();
        specField.setPromptText("Specifications");
        specField.setMaxWidth(300);
        specField.setFont(Font.font("Arial", 14));

        labField = new TextField();
        labField.setPromptText("Lab Number");
        labField.setMaxWidth(100);
        labField.setFont(Font.font("Arial", 14));

        statusField = new TextField();
        statusField.setPromptText("Status (Active, Maintenance, etc.)");
        statusField.setMaxWidth(150);
        statusField.setFont(Font.font("Arial", 14));

        HBox inputBox = new HBox(15, idField, specField, labField, statusField);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10, 0, 10, 0));

        // Buttons stacked vertically
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        addBtn.setStyle(buttonStyle());
        updateBtn.setStyle(buttonStyle());
        deleteBtn.setStyle(buttonStyle());

        buttonBox.getChildren().addAll(addBtn, updateBtn, deleteBtn);

        // Message Label
        messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setTextFill(Color.RED);

        // Table for existing computers
        table = new TableView<>();
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Computer, String> colId = new TableColumn<>("Computer ID");
        colId.setCellValueFactory(data -> data.getValue().idProperty());

        TableColumn<Computer, String> colSpec = new TableColumn<>("Specifications");
        colSpec.setCellValueFactory(data -> data.getValue().specProperty());

        TableColumn<Computer, String> colLab = new TableColumn<>("Lab Number");
        colLab.setCellValueFactory(data -> data.getValue().labProperty());

        TableColumn<Computer, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        table.getColumns().addAll(colId, colSpec, colLab, colStatus);
        table.setItems(computers);

        // Select row to populate fields
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idField.setText(newSel.getId());
                specField.setText(newSel.getSpec());
                labField.setText(newSel.getLab());
                statusField.setText(newSel.getStatus());
                messageLabel.setText("");
            }
        });

        // Button actions
        addBtn.setOnAction(e -> addComputer());
        updateBtn.setOnAction(e -> updateComputer());
        deleteBtn.setOnAction(e -> deleteComputer());

        VBox layout = new VBox(10, titleLabel, inputBox, buttonBox, messageLabel, table);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 900, 550);
        stage.setTitle("Manage Computer Details");
        stage.setScene(scene);
        stage.show();
    }

    private String buttonStyle() {
        return "-fx-background-color: #fdb31e; -fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 6 20 6 20;";
    }

    private void addComputer() {
        String id = idField.getText().trim();
        String spec = specField.getText().trim();
        String lab = labField.getText().trim();
        String status = statusField.getText().trim();

        if (id.isEmpty() || spec.isEmpty() || lab.isEmpty() || status.isEmpty()) {
            setMessage("All fields must be filled.", true);
            return;
        }

        if (findComputer(id) != null) {
            setMessage("Error: Duplicate Computer ID.", true);
            return;
        }

        computers.add(new Computer(id, spec, lab, status));
        setMessage("Computer details added successfully.", false);
        clearFields();
    }

    private void updateComputer() {
        String id = idField.getText().trim();
        String spec = specField.getText().trim();
        String lab = labField.getText().trim();
        String status = statusField.getText().trim();

        if (id.isEmpty()) {
            setMessage("Computer ID is required for update.", true);
            return;
        }

        Computer comp = findComputer(id);
        if (comp == null) {
            setMessage("Error: Computer ID not found.", true);
            return;
        }

        if (spec.isEmpty() || lab.isEmpty() || status.isEmpty()) {
            setMessage("All fields must be filled.", true);
            return;
        }

        comp.setSpec(spec);
        comp.setLab(lab);
        comp.setStatus(status);

        table.refresh();
        setMessage("Computer details updated successfully.", false);
        clearFields();
    }

    private void deleteComputer() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Computer ID is required for deletion.", true);
            return;
        }

        Computer comp = findComputer(id);
        if (comp == null) {
            setMessage("Error: Computer ID not found.", true);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete computer with ID: " + id + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            computers.remove(comp);
            setMessage("Computer details deleted successfully.", false);
            clearFields();
        }
    }

    private Computer findComputer(String id) {
        for (Computer c : computers) {
            if (c.getId().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    private void setMessage(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }

    private void clearFields() {
        idField.clear();
        specField.clear();
        labField.clear();
        statusField.clear();
    }

    // Computer class with JavaFX properties
    public static class Computer {
        private final StringProperty id;
        private final StringProperty spec;
        private final StringProperty lab;
        private final StringProperty status;

        public Computer(String id, String spec, String lab, String status) {
            this.id = new SimpleStringProperty(id);
            this.spec = new SimpleStringProperty(spec);
            this.lab = new SimpleStringProperty(lab);
            this.status = new SimpleStringProperty(status);
        }

        public String getId() { return id.get(); }
        public void setId(String id) { this.id.set(id); }
        public StringProperty idProperty() { return id; }

        public String getSpec() { return spec.get(); }
        public void setSpec(String spec) { this.spec.set(spec); }
        public StringProperty specProperty() { return spec; }

        public String getLab() { return lab.get(); }
        public void setLab(String lab) { this.lab.set(lab); }
        public StringProperty labProperty() { return lab; }

        public String getStatus() { return status.get(); }
        public void setStatus(String status) { this.status.set(status); }
        public StringProperty statusProperty() { return status; }
    }
}
