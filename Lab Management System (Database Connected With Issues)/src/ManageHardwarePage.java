import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;

public class ManageHardwarePage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    
    private ComboBox<String> labComboBox;
    private TextField computerIdField, processorField, ramField, storageField, gpuField;
    private ComboBox<String> statusComboBox;
    private Label statusLabel;

    // Simulated data store (In real application, replace with DB calls)
    private HashMap<String, String[]> hardwareDatabase = new HashMap<>();
    
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
        labComboBox.getItems().addAll("Lab 1", "Lab 2", "Lab 3");
        labComboBox.setPromptText("Select Lab");

        computerIdField = new TextField();
        computerIdField.setPromptText("Enter Computer ID");

        processorField = new TextField();
        processorField.setPromptText("Processor");

        ramField = new TextField();
        ramField.setPromptText("RAM");

        storageField = new TextField();
        storageField.setPromptText("Storage");

        gpuField = new TextField();
        gpuField.setPromptText("GPU");

        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Working", "Faulty");
        statusComboBox.setPromptText("Select Status");

        Button loadBtn = new Button("Load");
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        for (Button btn : new Button[]{loadBtn, addBtn, updateBtn, deleteBtn}) {
            btn.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        }

        HBox buttonBox = new HBox(10, loadBtn, addBtn, updateBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);

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
                processorField,
                ramField,
                storageField,
                gpuField,
                statusComboBox,
                buttonBox,
                statusLabel,
                backButton
        );

        form.setPadding(new Insets(30));
        form.setAlignment(Pos.CENTER_LEFT);
        form.setStyle("-fx-background-color: white;");

        // Button Actions
        loadBtn.setOnAction(e -> loadHardwareDetails());
        addBtn.setOnAction(e -> manageHardware("add"));
        updateBtn.setOnAction(e -> manageHardware("update"));
        deleteBtn.setOnAction(e -> manageHardware("delete"));

        Scene scene = new Scene(form,  900, 550);
        stage.setScene(scene);
        stage.setTitle("Manage Hardware Details");
        stage.show();
    }

    private void loadHardwareDetails() {
        String key = generateKey();
        if (hardwareDatabase.containsKey(key)) {
            String[] data = hardwareDatabase.get(key);
            processorField.setText(data[0]);
            ramField.setText(data[1]);
            storageField.setText(data[2]);
            gpuField.setText(data[3]);
            statusComboBox.setValue(data[4]);
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Hardware details loaded.");
        } else {
            clearFields();
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Computer not found in the selected lab.");
        }
    }

    private void manageHardware(String action) {
        String lab = labComboBox.getValue();
        String compId = computerIdField.getText().trim();
        String processor = processorField.getText().trim();
        String ram = ramField.getText().trim();
        String storage = storageField.getText().trim();
        String gpu = gpuField.getText().trim();
        String status = statusComboBox.getValue();

        String key = generateKey();

        if (lab == null || compId.isEmpty() || (action.equals("delete") ? false :
                processor.isEmpty() || ram.isEmpty() || storage.isEmpty() || gpu.isEmpty() || status == null)) {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Please fill in all required fields with valid data.");
            return;
        }

        try {
            switch (action) {
                case "add":
                    hardwareDatabase.put(key, new String[]{processor, ram, storage, gpu, status});
                    statusLabel.setTextFill(Color.GREEN);
                    statusLabel.setText("Hardware details added successfully.");
                    break;
                case "update":
                    if (hardwareDatabase.containsKey(key)) {
                        hardwareDatabase.put(key, new String[]{processor, ram, storage, gpu, status});
                        statusLabel.setTextFill(Color.GREEN);
                        statusLabel.setText("Hardware details updated successfully.");
                    } else {
                        statusLabel.setTextFill(Color.FIREBRICK);
                        statusLabel.setText("Computer not found in the selected lab.");
                    }
                    break;
                case "delete":
                    if (hardwareDatabase.containsKey(key)) {
                        hardwareDatabase.remove(key);
                        clearFields();
                        statusLabel.setTextFill(Color.GREEN);
                        statusLabel.setText("Hardware details deleted successfully.");
                    } else {
                        statusLabel.setTextFill(Color.FIREBRICK);
                        statusLabel.setText("Computer not found in the selected lab.");
                    }
                    break;
            }
        } catch (Exception ex) {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Error: Unable to update hardware details. Please try again later.");
        }
    }

    private String generateKey() {
        String lab = labComboBox.getValue();
        String compId = computerIdField.getText().trim();
        return lab + "-" + compId;
    }

    private void clearFields() {
        processorField.clear();
        ramField.clear();
        storageField.clear();
        gpuField.clear();
        statusComboBox.getSelectionModel().clearSelection();
    }
}
