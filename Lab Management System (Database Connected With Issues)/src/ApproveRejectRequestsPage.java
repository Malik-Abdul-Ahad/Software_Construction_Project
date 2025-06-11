import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ApproveRejectRequestsPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;

    private TableView<Request> table;
    private ObservableList<Request> requests;
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
        // --- Sample Pending Requests ---
        requests = FXCollections.observableArrayList(
                new Request("R101", "Install Python", "Student"),
                new Request("R102", "Update Java SDK", "Teacher"),
                new Request("R103", "Install VS Code", "Student"),
                new Request("R104", "Antivirus issue", "Teacher")
        );

        // --- Table Setup ---
        table = new TableView<>();
        table.setPrefHeight(250);
        
        // Set equal column width policy
        table.setColumnResizePolicy(param -> true);

        TableColumn<Request, String> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(data -> data.getValue().idProperty());
        idCol.setPrefWidth(300);
        idCol.setMinWidth(150);

        TableColumn<Request, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descCol.setPrefWidth(300);
        descCol.setMinWidth(200);

        TableColumn<Request, String> byCol = new TableColumn<>("Requested By");
        byCol.setCellValueFactory(data -> data.getValue().requestedByProperty());
        byCol.setPrefWidth(300);
        byCol.setMinWidth(150);

        table.getColumns().addAll(idCol, descCol, byCol);
        table.setItems(requests);

        // --- Detail Section ---
        Label title = new Label("Request Details");
        title.setFont(Font.font("Arial", 16));
        title.setTextFill(Color.web("#0047AB"));

        Label idLabel = new Label();
        Label descLabel = new Label();
        Label byLabel = new Label();

        idLabel.setFont(Font.font("Arial", 14));
        descLabel.setFont(Font.font("Arial", 14));
        byLabel.setFont(Font.font("Arial", 14));

        TextField reasonField = new TextField();
        reasonField.setPromptText("Enter reason if rejecting");
        reasonField.setFont(Font.font("Arial", 13));
        reasonField.setMaxWidth(300);
        reasonField.setVisible(false);

        Button approveBtn = new Button("Approve");
        Button rejectBtn = new Button("Reject");

        approveBtn.setStyle(
                "-fx-background-color: #fdb31e;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 6 20 6 20;");
        rejectBtn.setStyle(
                "-fx-background-color: #f44336;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25;" +
                "-fx-padding: 6 20 6 20;");

        HBox btnBox = new HBox(10, approveBtn, rejectBtn);
        btnBox.setAlignment(Pos.CENTER);

        detailBox = new VBox(10, title, idLabel, descLabel, byLabel, reasonField, btnBox);
        detailBox.setPadding(new Insets(20));
        detailBox.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-background-color: #f9f9f9;");
        detailBox.setVisible(false);

        // --- Table Selection Listener ---
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                idLabel.setText("Request ID: " + newVal.getId());
                descLabel.setText("Description: " + newVal.getDescription());
                byLabel.setText("Requested By: " + newVal.getRequestedBy());
                reasonField.setText("");
                reasonField.setVisible(false);
                detailBox.setVisible(true);
            }
        });

        // --- Approve Button Action ---
        approveBtn.setOnAction(e -> {
            Request selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Request Approved");
                alert.setHeaderText(null);
                alert.setContentText("Request " + selected.getId() + " has been approved.");
                alert.showAndWait();
                requests.remove(selected);
                detailBox.setVisible(false);
            }
        });

        // --- Reject Button Action ---
        rejectBtn.setOnAction(e -> {
            Request selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (!reasonField.isVisible()) {
                    reasonField.setVisible(true);
                    reasonField.requestFocus();
                } else if (reasonField.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Rejection Reason Required");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a reason for rejection.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Request Rejected");
                    alert.setHeaderText(null);
                    alert.setContentText("Request " + selected.getId() + " has been rejected.\nReason: " + reasonField.getText().trim());
                    alert.showAndWait();
                    requests.remove(selected);
                    detailBox.setVisible(false);
                }
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

        // --- Main Layout ---
        VBox mainLayout = new VBox(20);
        mainLayout.getChildren().addAll(table, detailBox, bottomBox);
        mainLayout.setStyle("-fx-background-color: white;");
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(30));

        Scene scene = new Scene(mainLayout, 900, 550);
        stage.setScene(scene);
        stage.setTitle("Approve or Reject Requests");
        stage.show();
    }

    // --- Inner Class for Request ---
    public static class Request {
        private final StringProperty id;
        private final StringProperty description;
        private final StringProperty requestedBy;

        public Request(String id, String description, String requestedBy) {
            this.id = new SimpleStringProperty(id);
            this.description = new SimpleStringProperty(description);
            this.requestedBy = new SimpleStringProperty(requestedBy);
        }

        public String getId() { return id.get(); }
        public StringProperty idProperty() { return id; }

        public String getDescription() { return description.get(); }
        public StringProperty descriptionProperty() { return description; }

        public String getRequestedBy() { return requestedBy.get(); }
        public StringProperty requestedByProperty() { return requestedBy; }
    }
} 