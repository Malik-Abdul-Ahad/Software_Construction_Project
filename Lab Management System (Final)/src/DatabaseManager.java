import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * DatabaseManager handles all database connectivity and operations for the Lab Management System.
 * It implements the JDBC connectivity and acts as a bridge between the application and the database.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:lms.db";
    private static Connection connection;
    private static boolean driverLoaded = false;
    
    /**
     * Initializes the database, creates tables if they don't exist
     */
    public static void initialize() {
        try {
            // Load SQLite JDBC Driver
            Class.forName("org.sqlite.JDBC");
            driverLoaded = true;
            System.out.println("SQLite JDBC Driver loaded successfully");
            
            // Check if database file exists
            boolean dbExists = new File("lms.db").exists();
            
            // Set database connection parameters
            String url = DB_URL + "?busy_timeout=10000&journal_mode=WAL&synchronous=NORMAL";
            
            // Create connection
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
            
            // Create tables if they don't exist
            createTables();
            
            if (dbExists) {
                System.out.println("Connected to existing database");
            } else {
                System.out.println("Created new database");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if database connection is available
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking connection: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reopen connection if closed
     */
    private static void ensureConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = DB_URL + "?busy_timeout=10000&journal_mode=WAL&synchronous=NORMAL";
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);
                System.out.println("Reopened database connection");
            }
        } catch (SQLException e) {
            System.err.println("Error reopening connection: " + e.getMessage());
        }
    }
    
    /**
     * Creates all database tables as per the ER diagram
     */
    private static void createTables() throws SQLException {
        if (!isConnected()) {
            System.err.println("Cannot create tables: No database connection");
            return;
        }
        
        Statement stmt = connection.createStatement();
        
        // USER table
        stmt.execute("CREATE TABLE IF NOT EXISTS USER (" +
                     "UserID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "Name TEXT NOT NULL, " +
                     "Email TEXT NOT NULL UNIQUE, " +
                     "Password TEXT NOT NULL, " +
                     "Role TEXT NOT NULL)");
        
        // LAB table
        stmt.execute("CREATE TABLE IF NOT EXISTS LAB (" +
                     "LabID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "LabName TEXT NOT NULL, " +
                     "Location TEXT, " +
                     "InchargeID INTEGER, " +
                     "FOREIGN KEY (InchargeID) REFERENCES USER(UserID))");
        
        // COMPUTER table
        stmt.execute("CREATE TABLE IF NOT EXISTS COMPUTER (" +
                     "ComputerID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "LabID INTEGER, " +
                     "Brand TEXT, " +
                     "Model TEXT, " +
                     "Status TEXT, " +
                     "FOREIGN KEY (LabID) REFERENCES LAB(LabID))");
        
        // HARDWARE table
        stmt.execute("CREATE TABLE IF NOT EXISTS HARDWARE (" +
                     "HardwareID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "ComputerID INTEGER, " +
                     "Type TEXT, " +
                     "Specification TEXT, " +
                     "Status TEXT, " +
                     "FOREIGN KEY (ComputerID) REFERENCES COMPUTER(ComputerID))");
        
        // INSTALLED_SOFTWARE table
        stmt.execute("CREATE TABLE IF NOT EXISTS INSTALLED_SOFTWARE (" +
                     "SoftwareID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "ComputerID INTEGER, " +
                     "SoftwareName TEXT, " +
                     "Version TEXT, " +
                     "InstallationDate TEXT, " +
                     "FOREIGN KEY (ComputerID) REFERENCES COMPUTER(ComputerID))");
        
        // COMPLAINTS table
        stmt.execute("CREATE TABLE IF NOT EXISTS COMPLAINTS (" +
                     "ComplaintID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "UserID INTEGER, " +
                     "ComplaintDate TEXT, " +
                     "ComplaintType TEXT, " +
                     "Description TEXT, " +
                     "Status TEXT, " +
                     "FOREIGN KEY (UserID) REFERENCES USER(UserID))");
        
        // SOFTWARE_REQUEST table
        stmt.execute("CREATE TABLE IF NOT EXISTS SOFTWARE_REQUEST (" +
                     "RequestID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "UserID INTEGER, " +
                     "SoftwareName TEXT, " +
                     "RequestDate TEXT, " +
                     "Status TEXT, " +
                     "FOREIGN KEY (UserID) REFERENCES USER(UserID))");
        
        // REQUEST_APPROVAL table
        stmt.execute("CREATE TABLE IF NOT EXISTS REQUEST_APPROVAL (" +
                     "ApprovalID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "RequestID INTEGER, " +
                     "ApprovedBy INTEGER, " +
                     "Decision TEXT, " +
                     "ApprovedDate TEXT, " +
                     "FOREIGN KEY (RequestID) REFERENCES SOFTWARE_REQUEST(RequestID), " +
                     "FOREIGN KEY (ApprovedBy) REFERENCES USER(UserID))");
        
        // REPORT table
        stmt.execute("CREATE TABLE IF NOT EXISTS REPORT (" +
                     "ReportID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "GeneratedBy INTEGER, " +
                     "ReportType TEXT, " +
                     "Date TEXT, " +
                     "Description TEXT, " +
                     "FOREIGN KEY (GeneratedBy) REFERENCES USER(UserID))");
        
        // PROCESS_DATA table
        stmt.execute("CREATE TABLE IF NOT EXISTS PROCESS_DATA (" +
                     "ProcessID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "ProcessType TEXT, " +
                     "Description TEXT, " +
                     "Date TEXT, " +
                     "Time TEXT, " +
                     "UserID INTEGER, " +
                     "FOREIGN KEY (UserID) REFERENCES USER(UserID))");
        
        stmt.close();
    }
    
    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Adds a new user to the database
     */
    public static boolean addUser(User user) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add user: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO USER (Name, Email, Password, Role) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().toString());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves a user by username
     */
    public static User getUserByUsername(String username) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot get user: No database connection");
            return null;
        }
        
        String sql = "SELECT * FROM USER WHERE Name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("UserID");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String encryptedPassword = rs.getString("Password");
                String roleStr = rs.getString("Role");
                
                if (encryptedPassword == null) {
                    System.err.println("Warning: User has null password in database: " + name);
                    return null;
                }
                
                System.out.println("Retrieved user from database: " + name + " with role: " + roleStr);
                
                // Create user object directly based on role
                User user = null;
                Role role = Role.valueOf(roleStr);
                
                try {
                    switch (role) {
                        case STUDENT:
                            user = createUserDirectly(Student.class, userID, name, email, encryptedPassword, role);
                            break;
                        case TEACHER:
                            user = createUserDirectly(Teacher.class, userID, name, email, encryptedPassword, role);
                            break;
                        case ADMIN:
                            user = createUserDirectly(LabAssistant.class, userID, name, email, encryptedPassword, role);
                            break;
                        default:
                            System.err.println("Unknown role: " + roleStr);
                            return null;
                    }
                    
                    if (user != null) {
                        System.out.println("Successfully created user object: " + name);
                        return user;
                    }
                } catch (Exception e) {
                    System.err.println("Error creating user object: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Database error getting user: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error retrieving user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Helper method to create a user object directly using reflection
     * without triggering validation in constructors
     */
    private static <T extends User> T createUserDirectly(Class<T> userClass, int userID, 
            String username, String email, String encryptedPassword, Role role) 
            throws Exception {
        
        // Create instance using no-arg constructor
        T user = userClass.getDeclaredConstructor().newInstance();
        
        // Set fields directly using reflection
        java.lang.reflect.Field idField = User.class.getDeclaredField("userID");
        idField.setAccessible(true);
        idField.set(user, userID);
        
        java.lang.reflect.Field usernameField = User.class.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, username);
        
        java.lang.reflect.Field emailField = User.class.getDeclaredField("email");
        emailField.setAccessible(true);
        emailField.set(user, email);
        
        java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
        passwordField.setAccessible(true);
        passwordField.set(user, encryptedPassword);
        
        java.lang.reflect.Field roleField = User.class.getDeclaredField("role");
        roleField.setAccessible(true);
        roleField.set(user, role);
        
        return user;
    }
    
    /**
     * Adds a new complaint to the database
     */
    public static boolean addComplaint(Complaint complaint) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add complaint: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO COMPLAINTS (UserID, ComplaintDate, ComplaintType, Description, Status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, complaint.getUserID());
            pstmt.setString(2, LocalDate.now().toString());
            pstmt.setString(3, "Hardware"); // Default type
            pstmt.setString(4, complaint.getComplaintDetails());
            pstmt.setString(5, complaint.getComplaintStatus().toString());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding complaint: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all complaints for a specific user
     */
    public static List<Complaint> getComplaintsByUserID(int userID) {
        ensureConnection();
        
        List<Complaint> complaints = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("Cannot get complaints: No database connection");
            return complaints;
        }
        
        String sql = "SELECT * FROM COMPLAINTS WHERE UserID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int complaintID = rs.getInt("ComplaintID");
                int computerID = 0; // Default value since it's not in the table
                String description = rs.getString("Description");
                String statusStr = rs.getString("Status");
                
                Complaint complaint = new Complaint(userID, computerID, description);
                
                // Use reflection to set the ID and status
                try {
                    java.lang.reflect.Field idField = Complaint.class.getDeclaredField("complaintID");
                    idField.setAccessible(true);
                    idField.set(complaint, complaintID);
                    
                    ComplaintStatus status = ComplaintStatus.valueOf(statusStr);
                    complaint.updateStatus(status);
                } catch (Exception e) {
                    System.err.println("Error setting complaint fields: " + e.getMessage());
                }
                
                complaints.add(complaint);
            }
        } catch (SQLException e) {
            System.err.println("Error getting complaints: " + e.getMessage());
        }
        
        return complaints;
    }
    
    /**
     * Adds a new software request to the database
     */
    public static boolean addSoftwareRequest(int userID, Software software) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add software request: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO SOFTWARE_REQUEST (UserID, SoftwareName, RequestDate, Status) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setString(2, software.getSoftwareName());
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.setString(4, RequestStatus.PENDING.toString());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding software request: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all software requests
     */
    public static List<Request> getAllRequests() {
        ensureConnection();
        
        List<Request> requests = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("Cannot get requests: No database connection");
            return requests;
        }
        
        String sql = "SELECT * FROM SOFTWARE_REQUEST";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int requestID = rs.getInt("RequestID");
                int userID = rs.getInt("UserID");
                String softwareName = rs.getString("SoftwareName");
                String statusStr = rs.getString("Status");
                
                String details = "User " + userID + " requested " + softwareName;
                Request request = new Request(details);
                
                // Use reflection to set the ID and status
                try {
                    java.lang.reflect.Field idField = Request.class.getDeclaredField("requestID");
                    idField.setAccessible(true);
                    idField.set(request, requestID);
                    
                    RequestStatus status = RequestStatus.valueOf(statusStr);
                    request.setStatus(status);
                } catch (Exception e) {
                    System.err.println("Error setting request fields: " + e.getMessage());
                }
                
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error getting requests: " + e.getMessage());
        }
        
        return requests;
    }
    
    /**
     * Updates the status of a software request
     */
    public static boolean updateRequestStatus(int requestID, RequestStatus status) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot update request status: No database connection");
            return false;
        }
        
        String sql = "UPDATE SOFTWARE_REQUEST SET Status = ? WHERE RequestID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status.toString());
            pstmt.setInt(2, requestID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Adds a new lab to the database
     */
    public static boolean addLab(Lab lab, int inchargeID) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add lab: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO LAB (LabName, Location, InchargeID) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lab.getLabName());
            pstmt.setString(2, "Default Location"); // Default value
            pstmt.setInt(3, inchargeID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding lab: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all labs from the database
     */
    public static List<Lab> getAllLabs() {
        ensureConnection();
        
        List<Lab> labs = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("Cannot get labs: No database connection");
            return labs;
        }
        
        String sql = "SELECT * FROM LAB";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int labID = rs.getInt("LabID");
                String labName = rs.getString("LabName");
                
                Lab lab = new Lab(labName);
                
                // Use reflection to set the lab ID
                try {
                    java.lang.reflect.Field idField = Lab.class.getDeclaredField("labID");
                    idField.setAccessible(true);
                    idField.set(lab, labID);
                } catch (Exception e) {
                    System.err.println("Error setting lab ID: " + e.getMessage());
                }
                
                labs.add(lab);
            }
        } catch (SQLException e) {
            System.err.println("Error getting labs: " + e.getMessage());
        }
        
        return labs;
    }
    
    /**
     * Adds a new computer to the database
     */
    public static boolean addComputer(Computer computer) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add computer: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO COMPUTER (LabID, Brand, Model, Status) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, computer.getLabID());
            pstmt.setString(2, "Default Brand"); // Default value
            pstmt.setString(3, "Default Model"); // Default value
            pstmt.setString(4, "Working"); // Default value
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding computer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all computers in a specific lab
     */
    public static List<Computer> getComputersByLabID(int labID) {
        ensureConnection();
        
        List<Computer> computers = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("Cannot get computers: No database connection");
            return computers;
        }
        
        String sql = "SELECT * FROM COMPUTER WHERE LabID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, labID);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int computerID = rs.getInt("ComputerID");
                String hardwareDetails = "Brand: " + rs.getString("Brand") + ", Model: " + rs.getString("Model");
                
                Computer computer = new Computer(labID, hardwareDetails);
                
                // Use reflection to set the computer ID
                try {
                    java.lang.reflect.Field idField = Computer.class.getDeclaredField("computerID");
                    idField.setAccessible(true);
                    idField.set(computer, computerID);
                } catch (Exception e) {
                    System.err.println("Error setting computer ID: " + e.getMessage());
                }
                
                computers.add(computer);
            }
        } catch (SQLException e) {
            System.err.println("Error getting computers: " + e.getMessage());
        }
        
        return computers;
    }
    
    /**
     * Adds a new report to the database
     */
    public static boolean addReport(Report report, int generatedBy) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add report: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO REPORT (GeneratedBy, ReportType, Date, Description) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, generatedBy);
            pstmt.setString(2, report.getReportType().toString());
            pstmt.setString(3, report.getGeneratedDate().toString());
            pstmt.setString(4, report.getReportDetails());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding report: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifies the database connection and structure
     * Returns true if the database is properly connected and structured
     */
    public static boolean verifyDatabase() {
        if (!isConnected()) {
            System.err.println("Database verification failed: No connection");
            return false;
        }
        
        try {
            // Check if USER table exists and has records
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USER")) {
                if (rs.next()) {
                    int userCount = rs.getInt(1);
                    System.out.println("Database verification: Found " + userCount + " users");
                    // Database exists and has tables
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("Error verifying USER table: " + e.getMessage());
                // The USER table might not exist, try to recreate tables
                System.out.println("Attempting to recreate database tables...");
                createTables();
                return false;
            }
        } catch (Exception e) {
            System.err.println("Database verification failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        return false;
    }
    
    /**
     * Updates the status of a complaint
     */
    public static boolean updateComplaintStatus(int complaintID, ComplaintStatus status) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot update complaint status: No database connection");
            return false;
        }
        
        String sql = "UPDATE COMPLAINTS SET Status = ? WHERE ComplaintID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status.toString());
            pstmt.setInt(2, complaintID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating complaint status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates computer details
     */
    public static boolean updateComputerDetails(int computerID, String newDetails) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot update computer details: No database connection");
            return false;
        }
        
        String sql = "UPDATE COMPUTER SET Model = ? WHERE ComputerID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newDetails);
            pstmt.setInt(2, computerID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating computer details: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Adds software to the installed software inventory
     */
    public static boolean addInstalledSoftware(int computerID, Software software) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add installed software: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO INSTALLED_SOFTWARE (ComputerID, SoftwareName, Version, InstallationDate) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, computerID);
            pstmt.setString(2, software.getSoftwareName());
            pstmt.setString(3, "1.0"); // Default version
            pstmt.setString(4, software.getInstallationDate().toString());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding installed software: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets all installed software for a specific computer
     */
    public static List<Software> getInstalledSoftware(int computerID) {
        ensureConnection();
        
        List<Software> softwareList = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("Cannot get installed software: No database connection");
            return softwareList;
        }
        
        String sql = "SELECT * FROM INSTALLED_SOFTWARE WHERE ComputerID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, computerID);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String softwareName = rs.getString("SoftwareName");
                String installationDateStr = rs.getString("InstallationDate");
                
                LocalDate installationDate;
                try {
                    installationDate = LocalDate.parse(installationDateStr);
                } catch (Exception e) {
                    installationDate = LocalDate.now(); // Default to today if parse fails
                }
                
                Software software = new Software(softwareName, installationDate);
                
                // Use reflection to set the software ID
                try {
                    int softwareID = rs.getInt("SoftwareID");
                    java.lang.reflect.Field idField = Software.class.getDeclaredField("softwareID");
                    idField.setAccessible(true);
                    idField.set(software, softwareID);
                } catch (Exception e) {
                    System.err.println("Error setting software ID: " + e.getMessage());
                }
                
                softwareList.add(software);
            }
        } catch (SQLException e) {
            System.err.println("Error getting installed software: " + e.getMessage());
        }
        
        return softwareList;
    }
    
    /**
     * Store process data in the database
     */
    public static boolean storeProcessData(String processType, String description, LocalDate date, String time, int userID) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot store process data: No database connection");
            return false;
        }
        
        String sql = "INSERT INTO PROCESS_DATA (ProcessType, Description, Date, Time, UserID) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, processType);
            pstmt.setString(2, description);
            pstmt.setString(3, date.toString());
            pstmt.setString(4, time);
            pstmt.setInt(5, userID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error storing process data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all complaints from the database
     */
    public static List<ViewComplaintStatusPage.Complaint> getAllComplaints() {
        ensureConnection();
        
        List<ViewComplaintStatusPage.Complaint> complaints = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("Cannot get complaints: No database connection");
            return complaints;
        }
        
        String sql = "SELECT * FROM COMPLAINTS";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String complaintID = "C" + String.format("%03d", rs.getInt("ComplaintID"));
                String description = rs.getString("ComplaintType");  // Use ComplaintType as the short description
                String status = rs.getString("Status");
                
                // Get user role from UserID
                int userID = rs.getInt("UserID");
                String registeredBy = "Student"; // Default
                
                String userSql = "SELECT Role FROM USER WHERE UserID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(userSql)) {
                    pstmt.setInt(1, userID);
                    ResultSet userRs = pstmt.executeQuery();
                    if (userRs.next()) {
                        registeredBy = userRs.getString("Role");
                    }
                }
                
                String registeredDetails = rs.getString("Description");  // Full details in the Description field
                
                ViewComplaintStatusPage.Complaint complaint = new ViewComplaintStatusPage.Complaint(
                    complaintID, description, status, registeredBy, registeredDetails
                );
                
                complaints.add(complaint);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all complaints: " + e.getMessage());
        }
        
        return complaints;
    }
    
    /**
     * Adds a complaint with specific ID format and details for the ViewComplaintStatusPage
     */
    public static boolean addFormattedComplaint(String id, String description, String status, 
                                               String registeredBy, String registeredDetails) {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot add formatted complaint: No database connection");
            return false;
        }
        
        // Extract numeric part from ID (e.g., "C001" -> 1)
        int complaintID;
        try {
            complaintID = Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            System.err.println("Invalid complaint ID format: " + id);
            return false;
        }
        
        // Get a default userID based on the registeredBy role
        int userID = 1; // Default
        String userSql = "SELECT UserID FROM USER WHERE Role = ? LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(userSql)) {
            pstmt.setString(1, registeredBy.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userID = rs.getInt("UserID");
            }
        } catch (SQLException e) {
            System.err.println("Error finding user for role " + registeredBy + ": " + e.getMessage());
        }
        
        String sql = "INSERT OR REPLACE INTO COMPLAINTS (ComplaintID, UserID, ComplaintDate, ComplaintType, Description, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, complaintID);
            pstmt.setInt(2, userID);
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.setString(4, description); // Store short description in ComplaintType
            pstmt.setString(5, registeredDetails); // Store full details in Description
            pstmt.setString(6, status);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding formatted complaint: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the next available complaint ID from the database
     */
    public static int getNextComplaintId() {
        ensureConnection();
        
        if (!isConnected()) {
            System.err.println("Cannot get next complaint ID: No database connection");
            return 100; // Default starting ID
        }
        
        String sql = "SELECT MAX(ComplaintID) as MaxID FROM COMPLAINTS";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int maxId = rs.getInt("MaxID");
                return maxId + 1;
            }
            
            // If no complaints exist yet, start with ID 1
            return 1;
        } catch (SQLException e) {
            System.err.println("Error getting next complaint ID: " + e.getMessage());
            return 100; // Default starting ID
        }
    }
} 