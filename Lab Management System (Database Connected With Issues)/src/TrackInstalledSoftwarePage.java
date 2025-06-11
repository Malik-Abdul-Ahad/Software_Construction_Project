import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TrackInstalledSoftwarePage {
    private MainApp mainApp;
    private Controller controller;
    private User user;

    private TableView<Software> table;
    private ObservableList<Software> softwareList;
    private VBox detailBox;
    
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
        // Sample software data
        softwareList = FXCollections.observableArrayList(
                new Software("PC001", "NetBeans", "8.2", "2023-03-14", "Licensed"),
                new Software("PC001", "MS Office", "2019", "2022-08-10", "Licensed"),
                new Software("PC002", "Eclipse", "2024-06", "2024-01-05", "Free"),
                new Software("PC003", "MATLAB", "2022b", "2022-12-21", "Licensed"),
                new Software("PC001", "Chrome", "113.0", "2023-11-02", "Free")
        );

        // --- Search Controls ---
        ComboBox<String> labComboBox = new ComboBox<>();
        labComboBox.getItems().addAll("Lab A", "Lab B", "Lab C", "Lab D", "Lab E");
        labComboBox.setPromptText("Select Lab");
        labComboBox.setMaxWidth(200);

        TextField computerIdField = new TextField();
        computerIdField.setPromptText("Computer ID - PC01");
        computerIdField.setFont(Font.font("Arial", 12));
        computerIdField.setMaxWidth(250);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(
                "-fx-background-color: #fdb31e;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 6 20 6 20;");

        HBox searchBox = new HBox(10, labComboBox, computerIdField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(20, 0, 20, 0));

        // --- TableView Setup ---
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<Software, String> nameCol = new TableColumn<>("Software Name");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<Software, String> versionCol = new TableColumn<>("Version");
        versionCol.setCellValueFactory(data -> data.getValue().versionProperty());

        TableColumn<Software, String> dateCol = new TableColumn<>("Installation Date");
        dateCol.setCellValueFactory(data -> data.getValue().installationDateProperty());

        TableColumn<Software, String> licenseCol = new TableColumn<>("License Status");
        licenseCol.setCellValueFactory(data -> data.getValue().licenseStatusProperty());

        table.getColumns().addAll(nameCol, versionCol, dateCol, licenseCol);
        table.setPlaceholder(new Label("No content in table"));

        // --- Detail Section ---
        Label detailTitle = new Label("Software Details");
        detailTitle.setFont(Font.font("Arial", 16));
        detailTitle.setTextFill(Color.web("#0047AB"));

        Label nameLabel = new Label();
        Label versionLabel = new Label();
        Label installDateLabel = new Label();
        Label licenseLabel = new Label();
        Label pcLabel = new Label();

        nameLabel.setFont(Font.font("Arial", 14));
        versionLabel.setFont(Font.font("Arial", 14));
        installDateLabel.setFont(Font.font("Arial", 14));
        licenseLabel.setFont(Font.font("Arial", 14));
        pcLabel.setFont(Font.font("Arial", 14));

        detailBox = new VBox(8, detailTitle, pcLabel, nameLabel, versionLabel, installDateLabel, licenseLabel);
        detailBox.setPadding(new Insets(20));
        detailBox.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-background-color: #f9f9f9;");
        detailBox.setVisible(false);

        // --- Table Selection Listener ---
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pcLabel.setText("Computer ID: " + newVal.getComputerId());
                nameLabel.setText("Software Name: " + newVal.getName());
                versionLabel.setText("Version: " + newVal.getVersion());
                installDateLabel.setText("Installation Date: " + newVal.getInstallationDate());
                licenseLabel.setText("License Status: " + newVal.getLicenseStatus());
                detailBox.setVisible(true);
            }
        });

        // --- Search Button Action ---
        searchBtn.setOnAction(e -> {
            String compId = computerIdField.getText().trim();
            String selectedLab = labComboBox.getValue();

            if (compId.isEmpty() && selectedLab == null) {
                showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a Computer ID or select a Lab to search.");
                return;
            }

            ObservableList<Software> filtered = FXCollections.observableArrayList();
            
            if (!compId.isEmpty()) {
                // Filter by computer ID
                for (Software sw : softwareList) {
                    if (sw.getComputerId().equalsIgnoreCase(compId)) {
                        filtered.add(sw);
                    }
                }
            } else if (selectedLab != null) {
                // Filter by lab (simulated - would connect to database in real implementation)
                if (selectedLab.equals("Lab A")) {
                    for (Software sw : softwareList) {
                        if (sw.getComputerId().startsWith("PC00")) {
                            filtered.add(sw);
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No software found for the specified criteria.");
                table.setItems(FXCollections.observableArrayList());
                detailBox.setVisible(false);
            } else {
                table.setItems(filtered);
                detailBox.setVisible(false);
            }
        });

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

        VBox mainLayout = new VBox(15, searchBox, table, detailBox, bottomBox);
        mainLayout.setStyle("-fx-background-color: white;");
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(30));

        Scene scene = new Scene(mainLayout, 900, 550);
        stage.setScene(scene);
        stage.setTitle("Track Installed Software");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- Software Model Class ---
    public static class Software {
        private final StringProperty computerId;
        private final StringProperty name;
        private final StringProperty version;
        private final StringProperty installationDate;
        private final StringProperty licenseStatus;

        public Software(String computerId, String name, String version, String installationDate, String licenseStatus) {
            this.computerId = new SimpleStringProperty(computerId);
            this.name = new SimpleStringProperty(name);
            this.version = new SimpleStringProperty(version);
            this.installationDate = new SimpleStringProperty(installationDate);
            this.licenseStatus = new SimpleStringProperty(licenseStatus);
        }

        public String getComputerId() {
            return computerId.get();
        }
        
        public StringProperty computerIdProperty() {
            return computerId;
        }

        public String getName() {
            return name.get();
        }
        
        public StringProperty nameProperty() {
            return name;
        }

        public String getVersion() {
            return version.get();
        }
        
        public StringProperty versionProperty() {
            return version;
        }

        public String getInstallationDate() {
            return installationDate.get();
        }
        
        public StringProperty installationDateProperty() {
            return installationDate;
        }

        public String getLicenseStatus() {
            return licenseStatus.get();
        }
        
        public StringProperty licenseStatusProperty() {
            return licenseStatus;
        }
    }
}
