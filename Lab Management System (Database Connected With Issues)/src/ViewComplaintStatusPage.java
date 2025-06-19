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

public class ViewComplaintStatusPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;

    private TableView<Complaint> table;
    private ObservableList<Complaint> complaints;
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
        // Sample data
        complaints = FXCollections.observableArrayList(
            new Complaint("C101", "Monitor not working", "Pending", "Student", "Display turns black after 10 minutes."),
            new Complaint("C102", "Software crash", "In Progress", "Teacher", "NetBeans crashes when saving."),
            new Complaint("C103", "Mouse issue", "Resolved", "Student", "Mouse is unresponsive."),
            new Complaint("C104", "Printer offline", "Pending", "Teacher", "Cannot connect to printer."),
            new Complaint("C105", "Slow PC", "Resolved", "Student", "System slows down while multitasking.")
        );

        // --- Search Bar ---
        TextField searchField = new TextField();
        searchField.setPromptText("Enter Complaint ID");
        searchField.setFont(Font.font("Arial", 14));
        searchField.setMaxWidth(250);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(
              "-fx-background-color: #fdb31e;" +
              "-fx-font-weight: bold;" +
              "-fx-background-radius: 25;" +
              "-fx-padding: 6 20 6 20;"
        );

        HBox searchBox = new HBox(10, searchField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(20, 0, 0, 0));

        // --- TableView ---
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(250);

        TableColumn<Complaint, String> idCol = new TableColumn<>("Complaint ID");
        idCol.setCellValueFactory(data -> data.getValue().idProperty());

        TableColumn<Complaint, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());

        TableColumn<Complaint, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());

        table.getColumns().addAll(idCol, descCol, statusCol);
        table.setItems(complaints);

        // --- Complaint Detail Section ---
        Label detailsTitle = new Label("Complaint Details");
        detailsTitle.setFont(Font.font("Arial", 16));
        detailsTitle.setTextFill(Color.web("#0047AB"));

        Label idLabel = new Label();
        Label descLabel = new Label();
        Label statusLabel = new Label();
        Label registeredByLabel = new Label();
        Label registeredDetailsLabel = new Label();

        idLabel.setFont(Font.font("Arial", 14));
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setWrapText(true);
        statusLabel.setFont(Font.font("Arial", 14));
        registeredByLabel.setFont(Font.font("Arial", 14));
        registeredDetailsLabel.setFont(Font.font("Arial", 14));
        registeredDetailsLabel.setWrapText(true);

        detailBox = new VBox(8, detailsTitle, idLabel, descLabel, statusLabel, registeredByLabel, registeredDetailsLabel);
        detailBox.setPadding(new Insets(20));
        detailBox.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-background-color: #f9f9f9;");
        detailBox.setVisible(false); // Hide until selected

        // --- Table Selection Listener ---
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                idLabel.setText("Complaint ID: " + newVal.idProperty().get());
                descLabel.setText("Description: " + newVal.descriptionProperty().get());
                statusLabel.setText("Status: " + newVal.statusProperty().get());
                registeredByLabel.setText("Registered By: " + newVal.registeredByProperty().get());
                registeredDetailsLabel.setText("Details: " + newVal.registeredDetailsProperty().get());
                detailBox.setVisible(true);
            }
        });

        // --- Search Button Action ---
        searchBtn.setOnAction(e -> {
            String searchId = searchField.getText().trim();

            if (searchId.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Required");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a Complaint ID to search.");
                alert.showAndWait();
                return;
            }

            boolean found = false;
            for (Complaint c : complaints) {
                if (c.idProperty().get().equalsIgnoreCase(searchId)) {
                    table.getSelectionModel().select(c);
                    table.scrollTo(c);
                    found = true;
                    break;
                }
            }

            if (!found) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Not Found");
                alert.setHeaderText(null);
                alert.setContentText("Complaint ID not found. Please check and try again.");
                alert.showAndWait();
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

        // --- Layout ---
        VBox mainLayout = new VBox(20, searchBox, table, detailBox, bottomBox);
        mainLayout.setStyle("-fx-background-color: white;");
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(30));

        Scene scene = new Scene(mainLayout, 900, 550);
        stage.setScene(scene);
        stage.setTitle("View Complaint Status");
        stage.show();
    }

    // --- Complaint Model (Inner Class) ---
    public static class Complaint {
        private final StringProperty id;
        private final StringProperty description;
        private final StringProperty status;
        private final StringProperty registeredBy;
        private final StringProperty registeredDetails;

        public Complaint(String id, String description, String status, String registeredBy, String registeredDetails) {
            this.id = new SimpleStringProperty(id);
            this.description = new SimpleStringProperty(description);
            this.status = new SimpleStringProperty(status);
            this.registeredBy = new SimpleStringProperty(registeredBy);
            this.registeredDetails = new SimpleStringProperty(registeredDetails);
        }

        public StringProperty idProperty() { return id; }
        public StringProperty descriptionProperty() { return description; }
        public StringProperty statusProperty() { return status; }
        public StringProperty registeredByProperty() { return registeredBy; }
        public StringProperty registeredDetailsProperty() { return registeredDetails; }
    }
}
