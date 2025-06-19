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

public class StudentTeacherDashboard extends Application {

    private boolean sidebarVisible = false;

    @Override
    public void start(Stage stage) {
        String userType = "Student";
        String userName = "Abdul Ahad";

        // --- Sidebar ---
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: #0077B6; -fx-padding: 30;");
        sidebar.setPrefWidth(280); // Increased width for full button text
        sidebar.setPrefHeight(500);

        sidebar.setAlignment(Pos.TOP_LEFT);
        sidebar.setSpacing(20);

        Label dashboardTitle = new Label("Dashboard");
        dashboardTitle.setFont(Font.font("Arial", 22));
        dashboardTitle.setTextFill(Color.WHITE);
        dashboardTitle.setAlignment(Pos.CENTER);
        dashboardTitle.setMaxWidth(Double.MAX_VALUE); // Fill width for center alignment


        Button viewComplaintBtn = new Button("View Complaint Status");
        Button registerComplaintBtn = new Button("Register Complaint");
        Button requestSoftwareBtn = new Button("Request Software Installation");

        for (Button btn : new Button[]{viewComplaintBtn, registerComplaintBtn, requestSoftwareBtn}) {
            btn.setPrefWidth(250); // Enough width to show full text
            btn.setStyle(
                "-fx-background-color: #FDB31E;" +
                "-fx-text-fill: black;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10 20 10 20;"
            );
            btn.setWrapText(true);
        }

        // --- Logout button (added separately at bottom) ---
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(220);
        logoutButton.setStyle(
            "-fx-background-color: #FDB31E;" +
            "-fx-text-fill: black;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20 10 20;"
        );

        VBox topButtons = new VBox(20, dashboardTitle, viewComplaintBtn, registerComplaintBtn, requestSoftwareBtn);
        VBox.setVgrow(topButtons, Priority.ALWAYS);

        sidebar.getChildren().addAll(topButtons, logoutButton);
        sidebar.setTranslateX(-260); // Slide from left, based on width

        // --- Sidebar wrapper that sticks from top to bottom ---
        AnchorPane sidebarWrapper = new AnchorPane(sidebar);
        AnchorPane.setTopAnchor(sidebar, 0.0);
        AnchorPane.setLeftAnchor(sidebar, 0.0);
        AnchorPane.setBottomAnchor(sidebar, 0.0);

        sidebarWrapper.setOnMouseEntered(e -> animateSidebar(sidebar, true));
        sidebarWrapper.setOnMouseExited(e -> animateSidebar(sidebar, false));

        // --- Top-right User Info ---
        String iconPath = userType.equals("Teacher") ? "file:resources/teacher_icon.png" : "file:resources/student_icon.png";
        ImageView userIcon = new ImageView(new Image(iconPath));
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

        // --- StackPane Layer: Sidebar overlays everything ---
        StackPane root = new StackPane(mainContent, sidebarWrapper);

        Scene scene = new Scene(root, 900, 500);
        stage.setScene(scene);
        stage.setTitle("Student/Teacher Dashboard");
        stage.show();
    }

    private void animateSidebar(VBox sidebar, boolean show) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        transition.setToX(show ? 0 : -260); // Updated width
        transition.play();
        sidebarVisible = show;
    }

    public static void main(String[] args) {
        launch();
    }
}
