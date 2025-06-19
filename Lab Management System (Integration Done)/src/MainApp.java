import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Import the User class from Application_Layer
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// This will import all classes from Application_Layer.java
// No need to use "import User" since it's not in a package
// Classes will be accessible directly

public class MainApp extends Application {

    private static Controller controller;  // Facade Controller
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            MainApp.primaryStage = primaryStage;
            controller = new Controller();  // Initialize the Controller/Facade

            // Show the login page first
            LoginPage loginPage = new LoginPage();
            loginPage.setMainApp(this);
            loginPage.setController(controller);
            loginPage.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Navigation methods
    public void showLoginScreen() {
        try {
            LoginPage loginPage = new LoginPage();
            loginPage.setMainApp(this);
            loginPage.setController(controller);
            loginPage.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showStudentTeacherDashboard(User user) {
        try {
            StudentTeacherDashboard dashboard = new StudentTeacherDashboard();
            dashboard.setMainApp(this);
            dashboard.setController(controller);
            dashboard.setUser(user);
            dashboard.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLabAssistantDashboard(User user) {
        try {
            LabAssistantDashboard dashboard = new LabAssistantDashboard();
            dashboard.setMainApp(this);
            dashboard.setController(controller);
            dashboard.setUser(user);
            dashboard.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Feature screen navigation methods
    public void showRegisterComplaintScreen(User user) {
        try {
            RegisterComplaintPage page = new RegisterComplaintPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showViewComplaintStatusScreen(User user) {
        try {
            ViewComplaintStatusPage page = new ViewComplaintStatusPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showRequestSoftwareInstallationScreen(User user) {
        try {
            RequestSoftwareInstallationPage page = new RequestSoftwareInstallationPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showResolveComplaintsScreen(User user) {
        try {
            ResolveComplaintsPage page = new ResolveComplaintsPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showManageHardwareScreen(User user) {
        try {
            ManageHardwarePage page = new ManageHardwarePage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showTrackInstalledSoftwareScreen(User user) {
        try {
            TrackInstalledSoftwarePage page = new TrackInstalledSoftwarePage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showApproveRejectRequestsScreen(User user) {
        try {
            ApproveRejectRequestsScreen page = new ApproveRejectRequestsScreen();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showManageComputerDetailsScreen(User user) {
        try {
            ManageComputerDetailsPage page = new ManageComputerDetailsPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showGenerateLabReportScreen(User user) {
        try {
            GenerateLabReportPage page = new GenerateLabReportPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showGenerateHardwareReportScreen(User user) {
        try {
            GenerateHardwareReportPage page = new GenerateHardwareReportPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showStoreProcessDataScreen(User user) {
        try {
            StoreProcessDataPage page = new StoreProcessDataPage();
            page.setMainApp(this);
            page.setController(controller);
            page.setUser(user);
            page.initialize(primaryStage);
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
