import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;

public class ViewComplaintStatusPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    private TableView<Complaint> complaintTable;
    
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
        Label titleLabel = new Label("View Complaint Status");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.DARKSLATEGRAY);
        
        // Create table for complaints
        complaintTable = new TableView<>();
        complaintTable.setEditable(false);
        
        TableColumn<Complaint, Integer> idCol = new TableColumn<>("Complaint ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("complaintID"));
        
        TableColumn<Complaint, Integer> computerCol = new TableColumn<>("Computer ID");
        computerCol.setCellValueFactory(new PropertyValueFactory<>("computerID"));
        
        TableColumn<Complaint, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("complaintDetails"));
        detailsCol.setPrefWidth(250);
        
        TableColumn<Complaint, ComplaintStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("complaintStatus"));
        
        complaintTable.getColumns().addAll(idCol, computerCol, detailsCol, statusCol);
        
        // Load complaints for current user
        loadComplaints();
        
        // Refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        refreshBtn.setOnAction(e -> loadComplaints());
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #0077B6;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        backButton.setOnAction(e -> {
            if (user.getRole() == Role.ADMIN) {
                mainApp.showLabAssistantDashboard(user);
            } else {
                mainApp.showStudentTeacherDashboard(user);
            }
        });
        
        // Layout
        VBox tableContainer = new VBox(10, complaintTable);
        tableContainer.setPadding(new Insets(10));
        
        HBox buttonBar = new HBox(10, refreshBtn, backButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10));
        
        BorderPane contentPane = new BorderPane();
        contentPane.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(20, 0, 20, 20));
        contentPane.setCenter(tableContainer);
        contentPane.setBottom(buttonBar);
        
        Scene scene = new Scene(contentPane, 900, 500);
        stage.setTitle("View Complaint Status");
        stage.setScene(scene);
        stage.show();
    }

    private void loadComplaints() {
        complaintTable.getItems().clear();
        List<Complaint> complaints = controller.getComplaintsByUser(user.getUserID());
        complaintTable.getItems().addAll(complaints);
        
        if (complaints.isEmpty()) {
            complaintTable.setPlaceholder(new Label("No complaints found"));
        }
    }
}
