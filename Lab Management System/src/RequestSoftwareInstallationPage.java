import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class RequestSoftwareInstallationPage extends Application {

    @Override
    public void start(Stage stage) {
        // Title
        Label title = new Label("Request Software Installation");
        title.setFont(Font.font("Arial", 20));
        title.setStyle("-fx-text-fill:rgb(0, 0, 0);");
        title.setPadding(new Insets(10));

        // Software Name
        TextField softwareNameField = new TextField();
        softwareNameField.setPromptText("Software Name");
        softwareNameField.setMaxWidth(300);

        // Version
        TextField versionField = new TextField();
        versionField.setPromptText("Version (optional)");
        versionField.setMaxWidth(300);

        // Request Type (Radio Buttons)
        ToggleGroup requestTypeGroup = new ToggleGroup();
        RadioButton singleComputerBtn = new RadioButton("Single Computer");
        RadioButton entireLabBtn = new RadioButton("Entire Lab");
        singleComputerBtn.setToggleGroup(requestTypeGroup);
        entireLabBtn.setToggleGroup(requestTypeGroup);

        HBox requestTypeBox = new HBox(15, singleComputerBtn, entireLabBtn);
        requestTypeBox.setAlignment(Pos.CENTER); //

        // Computer ID (for Single Computer)
        TextField computerIdField = new TextField();
        computerIdField.setPromptText("Computer ID");
        computerIdField.setMaxWidth(300);
        computerIdField.setDisable(true);

        // Lab Selection (for both cases)
        ComboBox<String> labComboBox = new ComboBox<>();
        labComboBox.setPromptText("Select Lab");
        labComboBox.getItems().addAll("Lab A", "Lab B", "Lab C");
        labComboBox.setMaxWidth(300);
        labComboBox.setDisable(true);

        // Justification (optional)
        TextArea justificationField = new TextArea();
        justificationField.setPromptText("Justification (optional)");
        justificationField.setWrapText(true);
        justificationField.setMaxWidth(400);
        justificationField.setPrefRowCount(4);

        // Submit Button
        Button submitBtn = new Button("Submit Request");
        submitBtn.setStyle(
                "-fx-background-color: #fdb31e;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 6 20 6 20;"
        );

        // Request Type Selection Logic
        singleComputerBtn.setOnAction(e -> {
            computerIdField.setDisable(false);
            labComboBox.setDisable(false); // 
        });

        entireLabBtn.setOnAction(e -> {
            computerIdField.setDisable(true);
            computerIdField.clear();
            labComboBox.setDisable(false); // Only lab is needed
        });

        // Submit Button Logic
        submitBtn.setOnAction(e -> {
            String softwareName = softwareNameField.getText().trim();
            String version = versionField.getText().trim();
            String computerId = computerIdField.getText().trim();
            String lab = labComboBox.getValue();
            String justification = justificationField.getText().trim();

            // Validation
            if (softwareName.isEmpty() || requestTypeGroup.getSelectedToggle() == null ||
                    (singleComputerBtn.isSelected() && (computerId.isEmpty() || lab == null)) ||
                    (entireLabBtn.isSelected() && lab == null)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Fields");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all required fields before submitting your request.");
                alert.showAndWait();
                return;
            }

            // Simulated success
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Request Submitted");
            success.setHeaderText(null);
            success.setContentText("Software installation request submitted successfully.");
            success.showAndWait();

            // Clear fields
            softwareNameField.clear();
            versionField.clear();
            computerIdField.clear();
            labComboBox.getSelectionModel().clearSelection();
            justificationField.clear();
            requestTypeGroup.selectToggle(null);
            computerIdField.setDisable(true);
            labComboBox.setDisable(true);
        });

        VBox formLayout = new VBox(15,
                title,
                softwareNameField,
                versionField,
                new Label("Request Type:"), requestTypeBox,
                computerIdField,
                labComboBox,
                justificationField,
                submitBtn
        );

        formLayout.setAlignment(Pos.TOP_CENTER);
        formLayout.setPadding(new Insets(30));
        formLayout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(formLayout, 900, 600);
        stage.setTitle("Request Software Installation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
