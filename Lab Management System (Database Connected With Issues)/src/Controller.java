import java.time.LocalDate;
import java.util.*;

// No need for specific imports for User, Complaint, etc.
// since they're defined in Application_Layer.java and available in the same package

public class Controller {
    private Map<String, User> userCache = new HashMap<>();
    private List<Complaint> complaintCache = new ArrayList<>();
    private List<Software> softwareCache = new ArrayList<>();
    private List<Request> requestCache = new ArrayList<>();
    private List<Report> reportCache = new ArrayList<>();
    private List<Lab> labCache = new ArrayList<>();
    private List<Computer> computerCache = new ArrayList<>();
    private boolean dbInitialized = false;

    public Controller() {
        // Initialize database
        try {
            initializeDB();
            
            // Check if default users exist in database
            User student = DatabaseManager.getUserByUsername("student1");
            User teacher = DatabaseManager.getUserByUsername("teacher1");
            User labAssistant = DatabaseManager.getUserByUsername("lab1");
            
            // Add default users if they don't exist
            if (student == null) {
                System.out.println("Creating default student user");
                addUser(new Student("student1", "pass123", "student1@example.com"));
            } else {
                userCache.put("student1", student);
            }
            
            if (teacher == null) {
                System.out.println("Creating default teacher user");
                addUser(new Teacher("teacher1", "pass123", "teacher1@example.com"));
            } else {
                userCache.put("teacher1", teacher);
            }
            
            if (labAssistant == null) {
                System.out.println("Creating default lab assistant user");
                addUser(new LabAssistant("lab1", "pass123", "lab1@example.com"));
            } else {
                userCache.put("lab1", labAssistant);
            }
        } catch (Exception e) {
            System.err.println("Error initializing controller: " + e.getMessage());
            e.printStackTrace();
            
            // Add default users to in-memory cache if database initialization fails
            userCache.put("student1", new Student("student1", "pass123", "student1@example.com"));
            userCache.put("teacher1", new Teacher("teacher1", "pass123", "teacher1@example.com"));
            userCache.put("lab1", new LabAssistant("lab1", "pass123", "lab1@example.com"));
        }
    }
    
    private void initializeDB() {
        try {
            DatabaseManager.initialize();
            
            // Verify database connection and structure
            boolean dbVerified = DatabaseManager.verifyDatabase();
            dbInitialized = DatabaseManager.isConnected() && dbVerified;
            
            if (dbInitialized) {
                System.out.println("Database initialized and verified successfully in Controller");
            } else {
                System.out.println("Database connection failed or verification failed, falling back to in-memory storage");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            dbInitialized = false;
        }
    }

    public void addUser(User user) {
        if (user == null) {
            System.out.println("ERROR: Attempted to add null user");
            return;
        }
        System.out.println("Adding user: " + user.getUsername() + " with role: " + user.getRole());
        
        // Always try to save to database first
        boolean addedToDb = false;
        
        // Make sure database is initialized
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        // Check if user already exists
        User existingUser = DatabaseManager.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            System.out.println("WARNING: User already exists in database: " + user.getUsername());
            return;
        }
        
        // Store in database
        addedToDb = DatabaseManager.addUser(user);
        if (addedToDb) {
            System.out.println("User added successfully to database: " + user.getUsername());
            // Only add to cache if database save was successful
            userCache.put(user.getUsername(), user);
        } else {
            System.out.println("CRITICAL ERROR: Failed to add user to database: " + user.getUsername());
        }
    }

    // Login
    public User login(String username, String password) {
        // Ensure database connection is available
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        System.out.println("Login attempt for: " + username);
        
        User user = null;
        
        // Special case for just checking if a username exists
        if (password.equals("checkOnly")) {
            // Always check database first
            if (DatabaseManager.isConnected()) {
                user = DatabaseManager.getUserByUsername(username);
                if (user != null) {
                    // Update cache with the latest from database
                    userCache.put(username, user);
                    return user;
                }
            }
            
            // Only fall back to cache if database check failed
            return userCache.get(username);
        }
        
        // For actual login, always try database first
        if (DatabaseManager.isConnected()) {
            try {
                user = DatabaseManager.getUserByUsername(username);
                if (user != null) {
                    System.out.println("User found in database: " + username);
                    // Update cache for future use
                    userCache.put(username, user);
                }
            } catch (Exception e) {
                System.err.println("Error retrieving user from database: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Only fall back to cache if database lookup failed
        if (user == null) {
            user = userCache.get(username);
            if (user != null) {
                System.out.println("User found in cache (not in database): " + username);
            }
        }
        
        if (user != null) {
            try {
                boolean loginSuccess = user.login(username, password);
                System.out.println("Login success: " + loginSuccess);
                if (loginSuccess) {
                    return user;
                }
            } catch (Exception e) {
                System.err.println("Error during login validation: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Login failed for user: " + username);
        return null;
    }

    // Complaint
    public void registerComplaint(int userID, int computerID, String details) {
        Complaint complaint = new Complaint(userID, computerID, details);
        
        // Always try to save to database
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        boolean saved = DatabaseManager.addComplaint(complaint);
        if (saved) {
            System.out.println("Complaint saved to database successfully");
        } else {
            System.err.println("Failed to save complaint to database");
            // Only add to cache if database save failed
            complaintCache.add(complaint);
        }
    }

    public List<Complaint> getComplaintsByUser(int userID) {
        // Always try database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        try {
            List<Complaint> complaints = DatabaseManager.getComplaintsByUserID(userID);
            if (complaints != null && !complaints.isEmpty()) {
                return complaints;
            }
        } catch (Exception e) {
            System.err.println("Error retrieving complaints from database: " + e.getMessage());
        }
        
        // Fall back to cache only if database retrieval fails
        List<Complaint> result = new ArrayList<>();
        for (Complaint c : complaintCache) {
            if (c.getUserID() == userID) result.add(c);
        }
        return result;
    }

    public void resolveComplaint(int complaintID) {
        // Always update database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        boolean updated = DatabaseManager.updateComplaintStatus(complaintID, ComplaintStatus.RESOLVED);
        if (updated) {
            System.out.println("Complaint status updated in database successfully");
        } else {
            System.err.println("Failed to update complaint status in database");
        }
        
        // Also update in cache for consistency
        for (Complaint c : complaintCache) {
            if (c.getComplaintID() == complaintID) {
                c.updateStatus(ComplaintStatus.RESOLVED);
                break;
            }
        }
    }

    // Software Requests
    public void requestSoftware(int userID, Software software, int computerID) {
        // Always try to save to database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        boolean saved = DatabaseManager.addSoftwareRequest(userID, software);
        if (saved) {
            System.out.println("Software request saved to database successfully");
            // Only add to cache if database save was successful
            Request request = new Request("User " + userID + " requested " + software.getSoftwareName());
            requestCache.add(request);
            softwareCache.add(software);
        } else {
            System.err.println("Failed to save software request to database");
        }
    }

    public void approveRequest(int requestID) {
        // Always update database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        boolean updated = DatabaseManager.updateRequestStatus(requestID, RequestStatus.APPROVED);
        if (updated) {
            System.out.println("Request status updated in database successfully");
        } else {
            System.err.println("Failed to update request status in database");
        }
        
        // Also update in cache for consistency
        for (Request r : requestCache) {
            if (r.getRequestID() == requestID) {
                r.setStatus(RequestStatus.APPROVED);
                break;
            }
        }
    }

    public void rejectRequest(int requestID) {
        // Always update database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        boolean updated = DatabaseManager.updateRequestStatus(requestID, RequestStatus.REJECTED);
        if (updated) {
            System.out.println("Request status updated in database successfully");
        } else {
            System.err.println("Failed to update request status in database");
        }
        
        // Also update in cache for consistency
        for (Request r : requestCache) {
            if (r.getRequestID() == requestID) {
                r.setStatus(RequestStatus.REJECTED);
                break;
            }
        }
    }

    public List<Request> getAllRequests() {
        // Always try database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        try {
            List<Request> requests = DatabaseManager.getAllRequests();
            if (requests != null && !requests.isEmpty()) {
                // Update cache with database results
                requestCache.clear();
                requestCache.addAll(requests);
                return requests;
            }
        } catch (Exception e) {
            System.err.println("Error retrieving requests from database: " + e.getMessage());
        }
        
        // Fall back to cache only if database retrieval fails
        return requestCache;
    }

    // Computer Details
    public void addComputer(int labID, String hardwareDetails) {
        Computer computer = new Computer(labID, hardwareDetails);
        
        // Always try to save to database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        boolean saved = DatabaseManager.addComputer(computer);
        if (saved) {
            System.out.println("Computer added to database successfully");
            // Add to cache only if database save was successful
            computerCache.add(computer);
        } else {
            System.err.println("Failed to add computer to database");
        }
    }

    public List<Computer> getComputersInLab(int labID) {
        // Always try database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        try {
            List<Computer> computers = DatabaseManager.getComputersByLabID(labID);
            if (computers != null && !computers.isEmpty()) {
                return computers;
            }
        } catch (Exception e) {
            System.err.println("Error retrieving computers from database: " + e.getMessage());
        }
        
        // Fall back to cache only if database retrieval fails
        List<Computer> result = new ArrayList<>();
        for (Computer c : computerCache) {
            if (c.getLabID() == labID) result.add(c);
        }
        return result;
    }

    public void updateComputerDetails(int computerID, String newDetails) {
        // Always update database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        boolean updated = DatabaseManager.updateComputerDetails(computerID, newDetails);
        if (updated) {
            System.out.println("Computer details updated in database successfully");
        } else {
            System.err.println("Failed to update computer details in database");
        }
        
        // Also update in cache for consistency
        for (Computer c : computerCache) {
            if (c.getComputerID() == computerID) {
                c.setHardwareDetails(newDetails);
                break;
            }
        }
    }

    // Software tracking
    public List<Software> getInstalledSoftware(int computerID) {
        // Always try database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        try {
            List<Software> software = DatabaseManager.getInstalledSoftware(computerID);
            if (software != null && !software.isEmpty()) {
                return software;
            }
        } catch (Exception e) {
            System.err.println("Error retrieving installed software from database: " + e.getMessage());
        }
        
        // Fall back to cache only if database retrieval fails
        return softwareCache;
    }

    // Reports
    public Report generateHardwareReport() {
        Report report = new Report(ReportType.HARDWARE);
        
        // Always try to save to database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        // Assuming current user ID is 1 for simplicity
        boolean saved = DatabaseManager.addReport(report, 1);
        if (saved) {
            System.out.println("Hardware report saved to database successfully");
            // Add to cache only if database save was successful
            reportCache.add(report);
        } else {
            System.err.println("Failed to save hardware report to database");
        }
        
        return report;
    }

    public Report generateLabReport(int labID) {
        Report report = new Report(ReportType.LAB_DETAILS);
        
        // Always try to save to database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        // Assuming current user ID is 1 for simplicity
        boolean saved = DatabaseManager.addReport(report, 1);
        if (saved) {
            System.out.println("Lab report saved to database successfully");
            // Add to cache only if database save was successful
            reportCache.add(report);
        } else {
            System.err.println("Failed to save lab report to database");
        }
        
        return report;
    }

    // Labs
    public void addLab(String labName) {
        Lab lab = new Lab(labName);
        
        // Always try to save to database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        // Assuming incharge ID is 1 for simplicity
        boolean saved = DatabaseManager.addLab(lab, 1);
        if (saved) {
            System.out.println("Lab added to database successfully");
            // Add to cache only if database save was successful
            labCache.add(lab);
        } else {
            System.err.println("Failed to add lab to database");
        }
    }

    public List<Lab> getAllLabs() {
        // Always try database first
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        try {
            List<Lab> labs = DatabaseManager.getAllLabs();
            if (labs != null && !labs.isEmpty()) {
                // Update cache with database results
                labCache.clear();
                labCache.addAll(labs);
                return labs;
            }
        } catch (Exception e) {
            System.err.println("Error retrieving labs from database: " + e.getMessage());
        }
        
        // Fall back to cache only if database retrieval fails
        return labCache;
    }

    // Method to store process data
    public boolean storeProcessData(String processType, String description, LocalDate date, String time) {
        // Always try to save to database
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        // Assuming current user ID is 1 for simplicity
        return DatabaseManager.storeProcessData(processType, description, date, time, 1);
    }

    // Initialize all required objects
    public void initialize() {
        // Initialize the database if not already initialized
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
        
        // The rest of the initialization logic
        // ... existing code ...
    }
}
