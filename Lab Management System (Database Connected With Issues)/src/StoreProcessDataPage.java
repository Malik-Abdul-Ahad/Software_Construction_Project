import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StoreProcessDataPage {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    
    private ComboBox<String> operationTypeComboBox;
    private TextArea inputArea;
    private TextField criteriaField;
    private Label resultLabel;
    private TextArea outputArea;
    
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
        operationTypeComboBox = new ComboBox<>();
        operationTypeComboBox.getItems().addAll("Store Data", "Process Data");
        operationTypeComboBox.setPromptText("Select Operation");

        inputArea = new TextArea();
        inputArea.setPromptText("Enter data to be stored (Hardware, Software, Complaints, etc.)...");
        inputArea.setWrapText(true);

        criteriaField = new TextField();
        criteriaField.setPromptText("Enter criteria for processing (e.g., lab usage)");

        Button performBtn = new Button("Execute Operation");
        performBtn.setStyle("-fx-background-color: #fdb31e;-fx-font-weight: bold;-fx-background-radius: 25;-fx-padding: 6 20 6 20;");

        resultLabel = new Label();
        resultLabel.setFont(Font.font("Arial", 14));
        resultLabel.setTextFill(Color.FIREBRICK);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Processed report or confirmation will appear here...");
        outputArea.setWrapText(true);
        
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

        VBox root = new VBox(10,
                operationTypeComboBox,
                inputArea,
                criteriaField,
                performBtn,
                resultLabel,
                outputArea,
                backButton
        );
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: white;");

        performBtn.setOnAction(e -> performOperation());

        Scene scene = new Scene(root,  900, 550);
        stage.setScene(scene);
        stage.setTitle("Store and Process Data");
        stage.show();
    }

    private void performOperation() {
        String operation = operationTypeComboBox.getValue();
        String inputData = inputArea.getText().trim();
        String criteria = criteriaField.getText().trim();
        resultLabel.setTextFill(Color.FIREBRICK);
        outputArea.clear();

        if (operation == null) {
            resultLabel.setText("Please select an operation.");
            return;
        }

        if ("Store Data".equals(operation)) {
            if (inputData.isEmpty()) {
                resultLabel.setText("Invalid data format. Please upload a valid file or enter data correctly.");
            } else {
                // Simulate successful data storage
                resultLabel.setTextFill(Color.GREEN);
                resultLabel.setText("Data stored successfully.");
                outputArea.setText("Stored Data:\n\n" + inputData);
            }

        } else if ("Process Data".equals(operation)) {
            if (criteria.isEmpty()) {
                resultLabel.setText("Please enter processing criteria.");
            } else {
                // Simulate processing
                if (criteria.toLowerCase().contains("error")) {
                    resultLabel.setText("Error: Unable to process data at this time. Please try again later.");
                } else {
                    resultLabel.setTextFill(Color.GREEN);
                    resultLabel.setText("Data processed successfully.");
                    outputArea.setText("Report for criteria: " + criteria + "\n\nSample Analytics:\n- Total Complaints: 12\n- Resolved: 9\n- Lab Usage High in: Lab-3\n- Frequent Software: IntelliJ, MATLAB");
                }
            }
        }
    }
}
