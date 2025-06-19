import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;

public class ResolveComplaintsPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    
    private ComboBox<String> complaintIdComboBox;
    private TextArea complaintDetailsArea;
    private ComboBox<String> resolutionStatusComboBox;
    private TextArea resolutionNotesArea;
    private Label statusLabel;

    // Simulated complaint database (Complaint ID -> [Details, Status])
    private HashMap<String, String[]> complaintDatabase = new HashMap<>();
    
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
        seedComplaintData(); // Sample complaints

        // Page title
        Label titleLabel = new Label("Resolve Complaints");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.DARKSLATEGRAY);

        complaintIdComboBox = new ComboBox<>();
        complaintIdComboBox.getItems().addAll(complaintDatabase.keySet());
        complaintIdComboBox.setPromptText("Select Complaint ID");

        complaintDetailsArea = new TextArea();
        complaintDetailsArea.setPromptText("Complaint Details...");
        complaintDetailsArea.setEditable(false);
        complaintDetailsArea.setWrapText(true);

        resolutionStatusComboBox = new ComboBox<>();
        resolutionStatusComboBox.getItems().addAll("Resolved", "In Progress", "Rejected");
        resolutionStatusComboBox.setPromptText("Select Resolution Status");

        resolutionNotesArea = new TextArea();
        resolutionNotesArea.setPromptText("Enter resolution notes or actions taken...");
        resolutionNotesArea.setWrapText(true);

        Button loadBtn = new Button("Load Complaint");
        Button updateBtn = new Button("Update Status");
        Button backBtn = new Button("Back to Dashboard");

        for (Button btn : new Button[]{loadBtn, updateBtn}) {
            btn.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        }
        
        backBtn.setStyle("-fx-background-color: #0077B6;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");
        backBtn.setOnAction(e -> mainApp.showLabAssistantDashboard(user));

        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setTextFill(Color.FIREBRICK);

        // Layout
        HBox topBox = new HBox(10, complaintIdComboBox, loadBtn);
        topBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox contentBox = new VBox(10,
                topBox,
                new Label("Complaint Details:"),
                complaintDetailsArea,
                new Label("Resolution:"),
                resolutionStatusComboBox,
                new Label("Resolution Notes:"),
                resolutionNotesArea,
                updateBtn
        );
        contentBox.setPadding(new Insets(15));
        
        HBox buttonBar = new HBox(10, backBtn);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10));
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(20, 0, 20, 20));
        mainLayout.setCenter(contentBox);
        mainLayout.setBottom(new VBox(10, statusLabel, buttonBar));
        BorderPane.setMargin(statusLabel, new Insets(10, 0, 0, 20));
        mainLayout.setStyle("-fx-background-color: white;");

        // Actions
        loadBtn.setOnAction(e -> loadComplaintDetails());
        updateBtn.setOnAction(e -> updateComplaintStatus());

        Scene scene = new Scene(mainLayout, 900, 500);
        stage.setScene(scene);
        stage.setTitle("Resolve Complaints");
        stage.show();
    }

    private void loadComplaintDetails() {
        String selectedId = complaintIdComboBox.getValue();
        if (selectedId == null || !complaintDatabase.containsKey(selectedId)) {
            complaintDetailsArea.clear();
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Complaint not found.");
            return;
        }

        String[] data = complaintDatabase.get(selectedId);
        String details = data[0];
        String currentStatus = data[1];

        complaintDetailsArea.setText("Complaint ID: " + selectedId + "\nStatus: " + currentStatus + "\n\n" + details);

        if ("Resolved".equalsIgnoreCase(currentStatus)) {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("This complaint has already been marked as resolved.");
        } else {
            statusLabel.setText("");
        }
    }

    private void updateComplaintStatus() {
        String complaintId = complaintIdComboBox.getValue();
        String resolutionStatus = resolutionStatusComboBox.getValue();
        String resolutionNotes = resolutionNotesArea.getText().trim();

        if (complaintId == null || !complaintDatabase.containsKey(complaintId)) {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Complaint not found.");
            return;
        }

        if (resolutionStatus == null || resolutionNotes.isEmpty()) {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Please select a status and enter resolution notes.");
            return;
        }

        String[] data = complaintDatabase.get(complaintId);
        if ("Resolved".equalsIgnoreCase(data[1])) {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("This complaint has already been marked as resolved.");
            return;
        }

        try {
            data[1] = resolutionStatus;
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Complaint updated successfully.\nNotification sent to complainant: \"Your complaint has been updated to " + resolutionStatus + ".\"");
            complaintDetailsArea.setText("Complaint ID: " + complaintId + "\nStatus: " + resolutionStatus + "\n\n" + data[0] + "\n\nResolution Notes:\n" + resolutionNotes);
        } catch (Exception e) {
            statusLabel.setTextFill(Color.FIREBRICK);
            statusLabel.setText("Error: Unable to update the complaint status. Please try again later.");
        }
    }

    private void seedComplaintData() {
        complaintDatabase.put("C101", new String[]{
                "Computer ID: PC-03\nIssue: Monitor not working", "Pending"
        });
        complaintDatabase.put("C102", new String[]{
                "Computer ID: PC-09\nIssue: System not booting", "In Progress"
        });
        complaintDatabase.put("C103", new String[]{
                "Computer ID: PC-12\nIssue: Keyboard keys not responding", "Pending"
        });
    }
}
