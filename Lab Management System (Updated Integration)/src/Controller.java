import java.time.LocalDate;
import java.util.*;

// No need for specific imports for User, Complaint, etc.
// since they're defined in Application_Layer.java and available in the same package

public class Controller {
    private Map<String, User> userDatabase = new HashMap<>();
    private List<Complaint> complaints = new ArrayList<>();
    private List<Software> softwareList = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();
    private List<Lab> labs = new ArrayList<>();
    private List<Computer> computers = new ArrayList<>();

    public Controller() {
        // Simulated users
        addUser(new Student("student1", "pass123", "student1@example.com"));
        addUser(new Teacher("teacher1", "pass123", "teacher1@example.com"));
        addUser(new LabAssistant("lab1", "pass123", "lab1@example.com"));
    }

    public void addUser(User user) {
        if (user == null) {
            System.out.println("ERROR: Attempted to add null user");
            return;
        }
        System.out.println("Adding user: " + user.getUsername() + " with role: " + user.getRole());
        
        // Check if user already exists
        if (userDatabase.containsKey(user.getUsername())) {
            System.out.println("WARNING: User already exists in database: " + user.getUsername());
        }
        
        userDatabase.put(user.getUsername(), user);
        System.out.println("User database size now: " + userDatabase.size());
        System.out.println("Users in database: " + userDatabase.keySet());
    }

    // Login
    public User login(String username, String password) {
        User user = userDatabase.get(username);
        System.out.println("Login attempt for: " + username);
        System.out.println("User found in database: " + (user != null));
        System.out.println("User database contains: " + userDatabase.keySet());
        
        // Special case for just checking if a username exists
        if (password.equals("checkOnly")) {
            return user; // Returns the user if found, null otherwise
        }
        
        if (user != null) {
            boolean loginSuccess = user.login(username, password);
            System.out.println("Login success: " + loginSuccess);
            return loginSuccess ? user : null;
        }
        
        System.out.println("User not found in database: " + username);
        return null;
    }

    // Complaint
    public void registerComplaint(int userID, int computerID, String details) {
        complaints.add(new Complaint(userID, computerID, details));
    }

    public List<Complaint> getComplaintsByUser(int userID) {
        List<Complaint> result = new ArrayList<>();
        for (Complaint c : complaints) {
            if (c.getUserID() == userID) result.add(c);
        }
        return result;
    }

    public void resolveComplaint(int complaintID) {
        for (Complaint c : complaints) {
            if (c.getComplaintID() == complaintID) {
                c.updateStatus(ComplaintStatus.RESOLVED);
                break;
            }
        }
    }

    // Software Requests
    public void requestSoftware(int userID, Software software, int computerID) {
        softwareList.add(software);
        requests.add(new Request("User " + userID + " requested " + software.getSoftwareName()));
    }

    public void approveRequest(int requestID) {
        for (Request r : requests) {
            if (r.getRequestID() == requestID) {
                r.setStatus(RequestStatus.APPROVED);
                break;
            }
        }
    }

    public void rejectRequest(int requestID) {
        for (Request r : requests) {
            if (r.getRequestID() == requestID) {
                r.setStatus(RequestStatus.REJECTED);
                break;
            }
        }
    }

    public List<Request> getAllRequests() {
        return requests;
    }

    // Computer Details
    public void addComputer(int labID, String hardwareDetails) {
        computers.add(new Computer(labID, hardwareDetails));
    }

    public List<Computer> getComputersInLab(int labID) {
        List<Computer> result = new ArrayList<>();
        for (Computer c : computers) {
            if (c.getLabID() == labID) result.add(c);
        }
        return result;
    }

    public void updateComputerDetails(int computerID, String newDetails) {
        for (Computer c : computers) {
            if (c.getComputerID() == computerID) {
                c.setHardwareDetails(newDetails);
                break;
            }
        }
    }

    // Software tracking
    public List<Software> getInstalledSoftware(int computerID) {
        // Currently software is not tied to computerID – simulate all for now
        return softwareList;
    }

    // Reports
    public Report generateHardwareReport() {
        Report report = new Report(ReportType.HARDWARE);
        reports.add(report);
        return report;
    }

    public Report generateLabReport(int labID) {
        Report report = new Report(ReportType.LAB_DETAILS);
        reports.add(report);
        return report;
    }

    // Labs
    public void addLab(String labName) {
        labs.add(new Lab(labName));
    }

    public List<Lab> getAllLabs() {
        return labs;
    }

    // Method to store process data
    public boolean storeProcessData(String processType, String description, LocalDate date, String time) {
        // In a real implementation, this would store the data in a database
        // For this simulation, we'll just return true to indicate success
        return true;
    }
}
