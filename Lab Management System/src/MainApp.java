import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Controller controller;  // Shared controller instance
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            MainApp.primaryStage = primaryStage;
            controller = new Controller();  // Initialize your Controller class

            showLoginScreen();  // Load the login screen first
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setMainApp(this);
            loginController.setController(controller);

            primaryStage.setTitle("Lab Management System - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showStudentDashboard(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentDashboard.fxml"));
            Parent root = loader.load();

            StudentDashboardController controller = loader.getController();
            controller.setMainApp(this);
            controller.setStudent(student);

            primaryStage.setTitle("Student Dashboard");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTeacherDashboard(Teacher teacher) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TeacherDashboard.fxml"));
            Parent root = loader.load();

            TeacherDashboardController controller = loader.getController();
            controller.setMainApp(this);
            controller.setTeacher(teacher);

            primaryStage.setTitle("Teacher Dashboard");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLabAssistantDashboard(LabAssistant labAssistant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LabAssistantDashboard.fxml"));
            Parent root = loader.load();

            LabAssistantDashboardController controller = loader.getController();
            controller.setMainApp(this);
            controller.setLabAssistant(labAssistant);

            primaryStage.setTitle("Lab Assistant Dashboard");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Controller getController() {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
