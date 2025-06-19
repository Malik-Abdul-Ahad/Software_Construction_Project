import javafx.animation.TranslateTransition;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LabAssistantDashboard {
    private MainApp mainApp;
    private Controller controller;
    private User user;
    private boolean sidebarVisible = false;

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
        String userName = user.getUsername();

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

        // Create buttons for each feature
        Button storeProcessDataBtn = new Button("Store and Process Data");
        Button resolveComplaintsBtn = new Button("Resolve Complaints");
        Button manageHardwareBtn = new Button("Manage Hardware Details");
        Button trackInstalledSoftwareBtn = new Button("Track Installed Software");
        Button approveRejectRequestsBtn = new Button("Approve or Reject Requests");
        Button manageComputerBtn = new Button("Manage Computer Details");
        Button generateLabReportsBtn = new Button("Generate Lab Reports");
        Button generateHardwareReportsBtn = new Button("Generate Hardware Reports");
        
        // Add actions to buttons
        storeProcessDataBtn.setOnAction(e -> mainApp.showStoreProcessDataScreen(user));
        resolveComplaintsBtn.setOnAction(e -> mainApp.showResolveComplaintsScreen(user));
        manageHardwareBtn.setOnAction(e -> mainApp.showManageHardwareScreen(user));
        trackInstalledSoftwareBtn.setOnAction(e -> mainApp.showTrackInstalledSoftwareScreen(user));
        approveRejectRequestsBtn.setOnAction(e -> mainApp.showApproveRejectRequestsScreen(user));
        manageComputerBtn.setOnAction(e -> mainApp.showManageComputerDetailsScreen(user));
        generateLabReportsBtn.setOnAction(e -> mainApp.showGenerateLabReportScreen(user));
        generateHardwareReportsBtn.setOnAction(e -> mainApp.showGenerateHardwareReportScreen(user));
        
        // Arrange buttons in grid
        buttonGrid.add(storeProcessDataBtn, 0, 0);
        buttonGrid.add(resolveComplaintsBtn, 1, 0);
        buttonGrid.add(manageHardwareBtn, 0, 1);
        buttonGrid.add(trackInstalledSoftwareBtn, 1, 1);
        buttonGrid.add(approveRejectRequestsBtn, 0, 2);
        buttonGrid.add(manageComputerBtn, 1, 2);
        buttonGrid.add(generateLabReportsBtn, 0, 3);
        buttonGrid.add(generateHardwareReportsBtn, 1, 3);

        // Style all buttons
        Button[] allButtons = {
                storeProcessDataBtn, resolveComplaintsBtn, manageHardwareBtn, trackInstalledSoftwareBtn,
                approveRejectRequestsBtn, manageComputerBtn, generateLabReportsBtn, generateHardwareReportsBtn
        };
        
        for (Button btn : allButtons) {
            btn.setPrefWidth(250); // Enough to avoid wrapping
            btn.setStyle(
                    "-fx-background-color: #FDB31E;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 20;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 10 20 10 20;");
            btn.setWrapText(false); // Prevent text wrapping
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
        logoutButton.setOnAction(e -> mainApp.showLoginScreen());

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
        
        // Add welcome message to the center
        Label welcomeLabel = new Label("Welcome, " + userName + "!");
        welcomeLabel.setFont(Font.font("Arial", 24));
        welcomeLabel.setTextFill(Color.DARKSLATEGRAY);
        
        Label roleLabel = new Label("Lab Assistant Dashboard");
        roleLabel.setFont(Font.font("Arial", 18));
        roleLabel.setTextFill(Color.GRAY);
        
        VBox centerContent = new VBox(20, welcomeLabel, roleLabel);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setStyle("-fx-padding: 50;");
        mainContent.setCenter(centerContent);

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
}
