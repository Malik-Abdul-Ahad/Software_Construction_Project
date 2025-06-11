import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginPage {
    private MainApp mainApp;
    private Controller controller;
    private TextField loginField;
    private PasswordField passwordField;
    private Label feedbackLabel;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void initialize(Stage stage) {
        // Left Image Panel
        Image image = new Image("file:resources/3.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        StackPane leftPane = new StackPane(imageView);
        leftPane.setMinWidth(400);
        leftPane.setMaxWidth(400);
        leftPane.setStyle("-fx-background-color: #FFFFFF;");

        // Top Right Sign-up
        Label signUpPrompt = new Label("Not a member?");
        Hyperlink signUpLink = new Hyperlink("Sign up Now");

        signUpLink.setOnAction(e -> showSignUpWindow());

        signUpLink.setTextFill(Color.web("#0047AB"));
        signUpLink.setFont(Font.font("Arial", 13));
        HBox signUpBox = new HBox(signUpPrompt, signUpLink);
        signUpBox.setAlignment(Pos.TOP_RIGHT);
        signUpBox.setSpacing(5);
        signUpBox.setPadding(new Insets(10, 20, 0, 0));

        // Login Form
        loginField = new TextField();
        loginField.setPromptText("Username");
        loginField.setMaxWidth(Double.MAX_VALUE);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        Hyperlink forgotPassword = new Hyperlink("Forgot Password ?");
        forgotPassword.setOnAction(e -> showForgotPasswordWindow());
        forgotPassword.setTextFill(Color.web("#0047AB"));
        forgotPassword.setFont(Font.font("Arial", 12));
        forgotPassword.setBorder(Border.EMPTY);
        forgotPassword.setPadding(new Insets(0, 0, 0, 0));
        HBox forgotBox = new HBox(forgotPassword);
        forgotBox.setAlignment(Pos.BASELINE_RIGHT);

        feedbackLabel = new Label();
        feedbackLabel.setTextFill(Color.RED);
        
        Button loginBtn = new Button("LOGIN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle(
              "-fx-background-color: #fdb31e;" +
              "-fx-font-weight: bold;" +
              "-fx-background-radius: 25;" +
              "-fx-padding: 10 0 10 0;"
        );
        loginBtn.setOnAction(e -> handleLogin());
        

        VBox loginForm = new VBox(10, loginField, passwordField, forgotBox, feedbackLabel, loginBtn);
        loginForm.setPadding(new Insets(150, 100, 150, 100));

        // Right Panel
        VBox rightContent = new VBox();
        rightContent.setAlignment(Pos.TOP_CENTER);
        rightContent.getChildren().addAll(signUpBox, loginForm);
        rightContent.setSpacing(40);
        rightContent.setPadding(new Insets(40, 0, 0, 0));

        BorderPane rightPane = new BorderPane();
        rightPane.setTop(signUpBox);
        rightPane.setCenter(loginForm);
        rightPane.setPrefWidth(500);
        rightPane.setStyle("-fx-background-color: white;");

        // Combine Both Sides
        HBox root = new HBox(leftPane, rightPane);
        Scene scene = new Scene(root, 900, 500);
        stage.setScene(scene);
        stage.setTitle("Lab Management System");
        stage.show();
    }
    
    private void handleLogin() {
        String username = loginField.getText().trim();
        String password = passwordField.getText().trim();
        
        System.out.println("Login attempt with username: " + username);
        
        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText("Please enter both username and password.");
            return;
        }
        
        // Use the controller to authenticate the user
        User user = controller.login(username, password);
        
        if (user != null) {
            System.out.println("Login successful for user: " + username + " with role: " + user.getRole());
            
            // Determine which dashboard to show based on user role
            switch (user.getRole()) {
                case STUDENT:
                case TEACHER:
                    mainApp.showStudentTeacherDashboard(user);
                    break;
                case ADMIN:
                    mainApp.showLabAssistantDashboard(user);
                    break;
                default:
                    feedbackLabel.setText("Unknown user role.");
            }
        } else {
            feedbackLabel.setText("Invalid username or password.");
            System.out.println("Login failed for user: " + username);
        }
    }


    private void showSignUpWindow() {
    Stage signUpStage = new Stage();
    signUpStage.setTitle("Sign Up");

    
    TextField usernameField = new TextField();
    usernameField.setPromptText("Username");

    
    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Password");


    PasswordField confirmPasswordField = new PasswordField();
    confirmPasswordField.setPromptText("Confirm Password");
    
    TextField emailField = new TextField();
    emailField.setPromptText("Email");
    
    ComboBox<String> roleComboBox = new ComboBox<>();
    roleComboBox.getItems().addAll("Student", "Teacher", "Lab Assistant");
    roleComboBox.setPromptText("Select Role");

    Button submitBtn = new Button("Sign Up");
    submitBtn.setStyle(
          "-fx-background-color: #0047AB;" +
          "-fx-text-fill: white;" +
          "-fx-font-weight: bold;" +
          "-fx-background-radius: 25;" +
          "-fx-padding: 8 20 8 20;"
    );

    Label feedbackLabel = new Label();
    feedbackLabel.setTextFill(Color.RED);

    submitBtn.setOnAction(event -> {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();

        System.out.println("Sign up attempt - Username: " + user + ", Role: " + role);

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty() || email.isEmpty() || role == null) {
            feedbackLabel.setText("All fields are required.");
            return;
        } 
        
        if (!pass.equals(confirm)) {
            feedbackLabel.setText("Passwords do not match.");
            return;
        }
        
        // Check if user already exists
        if (controller.login(user, "checkOnly") != null) {
            feedbackLabel.setText("Username already exists. Please choose a different username.");
            return;
        }
        
        // Create a new user based on selected role
        User newUser = null;
        try {
            switch (role) {
                case "Student":
                    newUser = new Student(user, pass, email);
                    break;
                case "Teacher":
                    newUser = new Teacher(user, pass, email);
                    break;
                case "Lab Assistant":
                    newUser = new LabAssistant(user, pass, email);
                    break;
            }
            
            if (newUser != null) {
                // Register the user in the controller
                controller.addUser(newUser);
                
                System.out.println("User registered successfully: " + user + ", Role: " + newUser.getRole());
                
                // Set success message
                feedbackLabel.setTextFill(Color.GREEN);
                feedbackLabel.setText("Sign up successful! You can now login with username: " + user);
                
                // Don't auto-fill the login fields after registration
                // Instead, just close the signup window after a short delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> signUpStage.close());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IllegalArgumentException e) {
            feedbackLabel.setText("Error: " + e.getMessage());
            System.out.println("Error creating user: " + e.getMessage());
        }
    });

    VBox layout = new VBox(10, usernameField, passwordField,
                           confirmPasswordField, emailField, roleComboBox, submitBtn, feedbackLabel);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);

    Scene scene = new Scene(layout, 350, 400);
    signUpStage.setScene(scene);
    signUpStage.show();
}


private void showForgotPasswordWindow() {
    Stage forgotStage = new Stage();
    forgotStage.setTitle("Password Recovery");

    Label infoLabel = new Label("Enter your username or email:");
    TextField userField = new TextField();
    userField.setPromptText("Username or Email");

    Button recoverBtn = new Button("Recover Password");
    recoverBtn.setStyle(
        "-fx-background-color: #0047AB;" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 25;" +
        "-fx-padding: 8 20 8 20;"
    );

    Label resultLabel = new Label();
    resultLabel.setTextFill(Color.GREEN);

    recoverBtn.setOnAction(event -> {
        String input = userField.getText().trim();

        if (input.isEmpty()) {
            resultLabel.setTextFill(Color.RED);
            resultLabel.setText("Please enter your username or email.");
        } else {
            // Simulate password recovery result
            resultLabel.setTextFill(Color.GREEN);
            resultLabel.setText("Temporary password sent to " + input + "\n(Default: pass123)");
        }
    });

    VBox layout = new VBox(10, infoLabel, userField, recoverBtn, resultLabel);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);

    Scene scene = new Scene(layout, 300, 250);
    forgotStage.setScene(scene);
    forgotStage.show();
}
}
