import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Enums exactly as in data dictionary (case-insensitive for clarity)
enum Role {
    STUDENT, TEACHER, ADMIN
}

enum ComplaintStatus {
    PENDING, IN_PROGRESS, RESOLVED
}

enum RequestStatus {
    APPROVED, REJECTED, PENDING
}

enum ReportType {
    HARDWARE, LAB_DETAILS
}

// Utility for password encryption
class PasswordUtil {
    public static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encryption error", e);
        }
    }
}

// Abstract User class with all attributes and constraints
abstract class User {
    private static int idCounter = 1; // simulate auto-increment

    protected int userID;
    protected String username;
    protected String password; // stored encrypted
    protected String email;
    protected Role role;

    public User(String username, String password, String email, Role role) {
        this.userID = idCounter++;
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.role = role;
    }

    // Getters
    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    // Setters with validation
    public void setUsername(String username) {
        if (username == null || username.length() > 50)
            throw new IllegalArgumentException("Username must be non-null and max 50 chars");
        this.username = username;
    }

    public void setPassword(String password) {
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null");
        this.password = PasswordUtil.encryptPassword(password);
    }

    public void setEmail(String email) {
        if (email == null || email.length() > 100 || !isValidEmail(email))
            throw new IllegalArgumentException("Invalid email format or length");
        this.email = email;
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        // Simple regex for basic validation
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(regex, email);
    }

    // Method to compare passwords correctly
    protected boolean checkPassword(String password) {
        String encryptedInput = PasswordUtil.encryptPassword(password);
        boolean match = this.password.equals(encryptedInput);
        System.out.println("Password check: Input encrypted = " + encryptedInput.substring(0, 10) + "...");
        System.out.println("Password check: Stored password = " + this.password.substring(0, 10) + "...");
        System.out.println("Password check result: " + match);
        return match;
    }

    // Abstract methods
    public abstract boolean login(String username, String password);

    public abstract void viewComplaintStatus(int userID);

    public abstract void requestSoftwareInstallation(int userID, Software software, int computerID);

    public abstract void registerComplaint(int userID, int computerID, String description);
}

// Student class
class Student extends User {
    public Student(String username, String password, String email) {
        super(username, password, email, Role.STUDENT);
    }

    @Override
    public boolean login(String username, String password) {
        System.out.println("Student login attempt: " + username);
        return this.username.equals(username) && checkPassword(password);
    }

    @Override
    public void viewComplaintStatus(int userID) {
        /* implementation */ }

    @Override
    public void requestSoftwareInstallation(int userID, Software software, int computerID) {
        /* implementation */ }

    @Override
    public void registerComplaint(int userID, int computerID, String description) {
        /* implementation */ }
}

// Teacher class
class Teacher extends User {
    public Teacher(String username, String password, String email) {
        super(username, password, email, Role.TEACHER);
    }

    @Override
    public boolean login(String username, String password) {
        System.out.println("Teacher login attempt: " + username);
        return this.username.equals(username) && checkPassword(password);
    }

    @Override
    public void viewComplaintStatus(int userID) {
        /* implementation */ }

    @Override
    public void requestSoftwareInstallation(int userID, Software software, int computerID) {
        /* implementation */ }

    @Override
    public void registerComplaint(int userID, int computerID, String description) {
        /* implementation */ }
}

// LabAssistant class
class LabAssistant extends User {
    public LabAssistant(String username, String password, String email) {
        super(username, password, email, Role.ADMIN); // Assuming Admin role for LabAssistant
    }

    public void approveRequest(int requestID) {
        /* implementation */ }

    public void rejectRequest(int requestID) {
        /* implementation */ }

    public void resolveComplaint(int complaintID) {
        /* implementation */ }

    public Report generateHardwareReport() {
        return null;
        /* implementation */ }

    public Report generateLabReport(int labID) {
        return null;
        /* implementation */ }

    public void manageComputerDetails(Computer computer) {
        /* implementation */ }

    public void trackInstalledSoftware(int computerID) {
        /* implementation */ }

    @Override
    public boolean login(String username, String password) {
        System.out.println("Lab Assistant login attempt: " + username);
        return this.username.equals(username) && checkPassword(password);
    }

    @Override
    public void viewComplaintStatus(int userID) {
        /* implementation */ }

    @Override
    public void requestSoftwareInstallation(int userID, Software software, int computerID) {
        /* implementation */ }

    @Override
    public void registerComplaint(int userID, int computerID, String description) {
        /* implementation */ }
}

// Complaint class
class Complaint {
    private static int complaintCounter = 1;

    private int complaintID;
    private int userID;
    private int computerID;
    private String complaintDetails;
    private ComplaintStatus complaintStatus;

    public Complaint(int userID, int computerID, String complaintDetails) {
        this.complaintID = complaintCounter++;
        this.userID = userID;
        this.computerID = computerID;
        setComplaintDetails(complaintDetails);
        this.complaintStatus = ComplaintStatus.PENDING;
    }

    public int getComplaintID() {
        return complaintID;
    }

    public int getUserID() {
        return userID;
    }

    public int getComputerID() {
        return computerID;
    }

    public String getComplaintDetails() {
        return complaintDetails;
    }

    public ComplaintStatus getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintDetails(String complaintDetails) {
        if (complaintDetails == null || complaintDetails.isEmpty())
            throw new IllegalArgumentException("Complaint details cannot be empty");
        this.complaintDetails = complaintDetails;
    }

    public void updateStatus(ComplaintStatus status) {
        this.complaintStatus = status;
    }
}

// Software class
class Software {
    private static int softwareCounter = 1;

    private int softwareID;
    private String softwareName;
    private LocalDate installationDate;

    public Software(String softwareName, LocalDate installationDate) {
        this.softwareID = softwareCounter++;
        setSoftwareName(softwareName);
        setInstallationDate(installationDate);
    }

    public int getSoftwareID() {
        return softwareID;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setSoftwareName(String softwareName) {
        if (softwareName == null || softwareName.isEmpty() || softwareName.length() > 100)
            throw new IllegalArgumentException("Software name cannot be empty and must be max 100 chars");
        this.softwareName = softwareName;
    }

    public void setInstallationDate(LocalDate installationDate) {
        if (installationDate == null)
            throw new IllegalArgumentException("Installation date cannot be null");
        this.installationDate = installationDate;
    }
}

// Request class
class Request {
    private static int requestCounter = 1;

    private int requestID;
    private String requestDetails;
    private RequestStatus requestStatus;

    public Request(String requestDetails) {
        this.requestID = requestCounter++;
        setRequestDetails(requestDetails);
        this.requestStatus = RequestStatus.PENDING;
    }

    public int getRequestID() {
        return requestID;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestDetails(String details) {
        if (details == null || details.isEmpty())
            throw new IllegalArgumentException("Request details cannot be empty");
        this.requestDetails = details;
    }

    public void setStatus(RequestStatus status) {
        this.requestStatus = status;
    }
}

// Report class
class Report {
    private static int reportCounter = 1;

    private int reportID;
    private ReportType reportType;
    private LocalDateTime generatedDate;

    public Report(ReportType reportType) {
        this.reportID = reportCounter++;
        this.reportType = reportType;
        this.generatedDate = LocalDateTime.now();
    }

    public int getReportID() {
        return reportID;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public String getReportDetails() {
        return "Report Type: " + reportType + ", Generated on: " + generatedDate;
    }
}

// Lab class
class Lab {
    private static int labCounter = 1;

    private int labID;
    private String labName;

    public Lab(String labName) {
        this.labID = labCounter++;
        setLabName(labName);
    }

    public int getLabID() {
        return labID;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        if (labName == null || labName.isEmpty() || labName.length() > 50)
            throw new IllegalArgumentException("Lab name must be non-empty and max 50 chars");
        this.labName = labName;
    }
}

// Computer class
class Computer {
    private static int computerCounter = 1;

    private int computerID;
    private int labID;
    private String hardwareDetails;

    public Computer(int labID, String hardwareDetails) {
        this.computerID = computerCounter++;
        this.labID = labID;
        setHardwareDetails(hardwareDetails);
    }

    public int getComputerID() {
        return computerID;
    }

    public int getLabID() {
        return labID;
    }

    public String getHardwareDetails() {
        return hardwareDetails;
    }

    public void setHardwareDetails(String hardwareDetails) {
        if (hardwareDetails == null || hardwareDetails.isEmpty())
            throw new IllegalArgumentException("Hardware details cannot be empty");
        this.hardwareDetails = hardwareDetails;
    }
}
