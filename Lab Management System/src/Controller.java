import java.time.LocalDate;
import java.util.*;

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

    private void addUser(User user) {
        userDatabase.put(user.getUsername(), user);
    }

    // Login
    public User login(String username, String password) {
        User user = userDatabase.get(username);
        return (user != null && user.login(username, password)) ? user : null;
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
}
