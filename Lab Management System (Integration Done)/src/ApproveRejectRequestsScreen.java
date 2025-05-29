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

public class ApproveRejectRequestsScreen {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    private TableView<Request> requestTable;
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
        Label titleLabel = new Label("Approve or Reject Requests");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.DARKSLATEGRAY);
        
        // Create table for requests
        requestTable = new TableView<>();
        requestTable.setEditable(false);
        
        TableColumn<Request, Integer> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestID"));
        idCol.setPrefWidth(100);
        
        TableColumn<Request, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("requestDetails"));
        detailsCol.setPrefWidth(400);
        
        TableColumn<Request, RequestStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));
        statusCol.setPrefWidth(100);
        
        requestTable.getColumns().addAll(idCol, detailsCol, statusCol);
        requestTable.setPrefHeight(300);
        
        // Create sample requests if none exist
        if (controller.getAllRequests().isEmpty()) {
            createSampleRequests();
        }
        
        // Load all requests
        loadRequests();
        
        // Approve and Reject buttons
        Button approveBtn = new Button("Approve Selected");
        approveBtn.setStyle("-fx-background-color: #fdb31e;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        approveBtn.setOnAction(e -> approveSelected());
        
        Button rejectBtn = new Button("Reject Selected");
        rejectBtn.setStyle("-fx-background-color: #F44336;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        rejectBtn.setOnAction(e -> rejectSelected());
        
        // Refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #4CAF50;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        refreshBtn.setOnAction(e -> loadRequests());
        
        // Back button - Make it more prominent
        Button backButton = new Button("Back to Dashboard");
        backButton.setPrefWidth(200); // Make button wider
        backButton.setStyle("-fx-background-color: #0077B6;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 10 20 10 20;");
        backButton.setOnAction(e -> mainApp.showLabAssistantDashboard(user));
        
        // Status label for feedback
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setTextFill(Color.FIREBRICK);
        
        // Layout for action buttons - Include back button in the main action row
        HBox actionButtons = new HBox(10, approveBtn, rejectBtn, refreshBtn, backButton);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(10));
        
        // Layout for table
        VBox tableContainer = new VBox(10, requestTable);
        tableContainer.setPadding(new Insets(10));
                
        // Main layout
        VBox contentBox = new VBox(10, actionButtons, tableContainer, statusLabel);
        contentBox.setPadding(new Insets(20));
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(20, 0, 20, 20));
        mainLayout.setCenter(contentBox);
        
        // Set white background and create scene
        mainLayout.setStyle("-fx-background-color: white;");
        Scene scene = new Scene(mainLayout, 900, 500);
        stage.setTitle("Approve or Reject Requests");
        stage.setScene(scene);
        stage.show();
    }

    private void loadRequests() {
        requestTable.getItems().clear();
        
        List<Request> requests = controller.getAllRequests();
        requestTable.getItems().addAll(requests);
        
        if (requests.isEmpty()) {
            requestTable.setPlaceholder(new Label("No pending requests"));
        }
    }
    
    private void approveSelected() {
        Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        
        if (selectedRequest == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a request to approve");
            return;
        }
        
        // Only approve if it's pending
        if (selectedRequest.getRequestStatus() == RequestStatus.PENDING) {
            controller.approveRequest(selectedRequest.getRequestID());
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Request #" + selectedRequest.getRequestID() + " has been approved");
            loadRequests();
        } else {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Request #" + selectedRequest.getRequestID() + " is already " + selectedRequest.getRequestStatus());
        }
    }
    
    private void rejectSelected() {
        Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        
        if (selectedRequest == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a request to reject");
            return;
        }
        
        // Only reject if it's pending
        if (selectedRequest.getRequestStatus() == RequestStatus.PENDING) {
            controller.rejectRequest(selectedRequest.getRequestID());
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Request #" + selectedRequest.getRequestID() + " has been rejected");
            loadRequests();
        } else {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Request #" + selectedRequest.getRequestID() + " is already " + selectedRequest.getRequestStatus());
        }
    }
    
    private void createSampleRequests() {
        // Create sample software requests
        controller.requestSoftware(1, new Software("Visual Studio 2022", LocalDate.now()), 101);
        controller.requestSoftware(2, new Software("Eclipse IDE", LocalDate.now()), 102);
        controller.requestSoftware(3, new Software("Unity Game Engine", LocalDate.now()), 103);
    }
}
