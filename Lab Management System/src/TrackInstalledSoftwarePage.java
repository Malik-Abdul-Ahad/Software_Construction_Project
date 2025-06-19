import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TrackInstalledSoftwarePage extends Application {

    private TableView<Software> table;
    private ObservableList<Software> softwareList;
    private VBox detailBox;

    @Override
    public void start(Stage stage) {
        // Sample software data
        softwareList = FXCollections.observableArrayList(
                new Software("PC01", "NetBeans", "8.2", "2023-03-14", "Licensed"),
                new Software("PC01", "MS Office", "2019", "2022-08-10", "Licensed"),
                new Software("PC02", "Eclipse", "2024-06", "2024-01-05", "Free"),
                new Software("PC03", "MATLAB", "2022b", "2022-12-21", "Licensed"),
                new Software("PC01", "Chrome", "113.0", "2023-11-02", "Free")
        );

        ComboBox<String> labComboBox = new ComboBox<>();
        labComboBox.getItems().addAll("Lab A", "Lab B", "Lab C");
        labComboBox.setPromptText("Select Lab");
        labComboBox.setMaxWidth(200);

        // --- Search Controls ---
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

        HBox searchBox = new HBox(10, labComboBox,computerIdField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(20, 0, 0, 0));

        // --- TableView Setup ---
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(250);

        TableColumn<Software, String> nameCol = new TableColumn<>("Software Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Software, String> versionCol = new TableColumn<>("Version");
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));

        TableColumn<Software, String> dateCol = new TableColumn<>("Installation Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("installationDate"));

        TableColumn<Software, String> licenseCol = new TableColumn<>("License Status");
        licenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseStatus"));

        table.getColumns().addAll(nameCol, versionCol, dateCol, licenseCol);

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
            String compId = computerIdField.getText().trim().toLowerCase();

            if (compId.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a Computer ID to search.");
                return;
            }

            ObservableList<Software> filtered = FXCollections.observableArrayList();
            for (Software sw : softwareList) {
                if (sw.getComputerId().equalsIgnoreCase(compId)) {
                    filtered.add(sw);
                }
            }

            if (filtered.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Invalid Computer ID. Please check and try again.");
                table.setItems(FXCollections.observableArrayList());
                detailBox.setVisible(false);
            } else {
                table.setItems(filtered);
                detailBox.setVisible(false);
            }
        });

        VBox mainLayout = new VBox(20, searchBox, table, detailBox);
        mainLayout.setStyle("-fx-background-color: white;");
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(30));

        Scene scene = new Scene(mainLayout, 900, 500);
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

    public static void main(String[] args) {
        launch();
    }

    // --- Software Model Class ---
    public static class Software {
        private final String computerId, name, version, installationDate, licenseStatus;

        public Software(String computerId, String name, String version, String installationDate, String licenseStatus) {
            this.computerId = computerId;
            this.name = name;
            this.version = version;
            this.installationDate = installationDate;
            this.licenseStatus = licenseStatus;
        }

        public String getComputerId() {
            return computerId;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getInstallationDate() {
            return installationDate;
        }

        public String getLicenseStatus() {
            return licenseStatus;
        }
    }
}
