import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LabAssistantDashboard extends Application {

    private boolean sidebarVisible = false;

    @Override
    public void start(Stage stage) {
        String userName = "Lab Coordinator";

        // --- Sidebar ---
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: #0077B6; -fx-padding: 30;");
        sidebar.setPrefWidth(570); // Wider sidebar to fit buttons
        sidebar.setPrefHeight(500);
        sidebar.setSpacing(20);

        // --- Dashboard Title ---
        Label dashboardTitle = new Label("Dashboard");
        dashboardTitle.setFont(Font.font("Arial", 22));
        dashboardTitle.setTextFill(Color.WHITE);
        dashboardTitle.setAlignment(Pos.CENTER);
        dashboardTitle.setMaxWidth(Double.MAX_VALUE);

        // --- GridPane for 2-column buttons ---
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(30);
        buttonGrid.setVgap(40);
        buttonGrid.setAlignment(Pos.CENTER);

        String[] useCases = {
                "Store and Process Data", "Resolve Complaints",
                "Manage Hardware Details", "Track Installed Software",
                "Approve or Reject Requests", "Manage Computer Details",
                "Generate Lab Reports", "Generate Hardware Reports"
        };

        int col = 0, row = 0;
        for (String label : useCases) {
            Button btn = new Button(label);
            btn.setPrefWidth(250); // Enough to avoid wrapping
            btn.setStyle(
                    "-fx-background-color: #FDB31E;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 20;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 10 20 10 20;");
            btn.setWrapText(false); // Prevent text wrapping

            buttonGrid.add(btn, col, row);
            col++;
            if (col == 2) {
                col = 0;
                row++;
            }
        }

        // --- Logout button ---
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(200);
        logoutButton.setStyle(
                "-fx-background-color: #FDB31E;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 20 10 20;");

        VBox.setVgrow(buttonGrid, Priority.ALWAYS);
        sidebar.getChildren().addAll(dashboardTitle, buttonGrid, logoutButton);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setTranslateX(-550); // Based on new sidebar width

        // --- Sidebar wrapper (anchors) ---
        AnchorPane sidebarWrapper = new AnchorPane(sidebar);
        AnchorPane.setTopAnchor(sidebar, 0.0);
        AnchorPane.setLeftAnchor(sidebar, 0.0);
        AnchorPane.setBottomAnchor(sidebar, 0.0);

        sidebarWrapper.setOnMouseEntered(e -> animateSidebar(sidebar, true));
        sidebarWrapper.setOnMouseExited(e -> animateSidebar(sidebar, false));

        // --- User Info Top Right ---
        ImageView userIcon = new ImageView(new Image("file:resources/lab2.jpg"));
        userIcon.setFitHeight(30);
        userIcon.setFitWidth(30);

        Label nameLabel = new Label(userName);
        nameLabel.setFont(Font.font("Arial", 14));
        nameLabel.setTextFill(Color.DARKGRAY);

        HBox userInfo = new HBox(10, userIcon, nameLabel);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.setPadding(new Insets(15, 20, 10, 10));
        userInfo.setStyle("-fx-background-color: white;");

        // --- Right Image ---
        ImageView rightImage = new ImageView(new Image("file:resources/3.jpg"));
        rightImage.setPreserveRatio(true);
        rightImage.setFitWidth(400);

        VBox imageBox = new VBox(rightImage);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(0, 30, 50, 20));

        // --- Main Content Layout ---
        BorderPane mainContent = new BorderPane();
        mainContent.setTop(userInfo);
        mainContent.setRight(imageBox);
        mainContent.setStyle("-fx-background-color: white;");

        StackPane root = new StackPane(mainContent, sidebarWrapper);

        Scene scene = new Scene(root, 1000, 550);
        stage.setScene(scene);
        stage.setTitle("Lab Assistant Dashboard");
        stage.show();
    }

    private void animateSidebar(VBox sidebar, boolean show) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        transition.setToX(show ? 0 : -550); // Match sidebar width
        transition.play();
        sidebarVisible = show;
    }

    public static void main(String[] args) {
        launch();
    }
}
