public class classes {
    // user class
  public abstract class User {
    protected String username;
    protected String password;

    public abstract boolean login(String username, String password);
    public abstract void viewComplaintStatus(int userID);
    public abstract void requestSoftwareInstallation(int userID, Software software, int computerID);
    public abstract void registerComplaint(int userID, int computerID, String description);
}
  public class Student extends User {
    private int studentID;
    private String department;

    public String getDepartment() {
        return department;
    }

    @Override
    public boolean login(String username, String password) { return false; }

    @Override
    public void viewComplaintStatus(int userID) {}

    @Override
    public void requestSoftwareInstallation(int userID, Software software, int computerID) {}

    @Override
    public void registerComplaint(int userID, int computerID, String description) {}
}
public class Teacher extends User {
    private int teacherID;
    private String department;

    public String getDepartment() {
        return department;
    }

    @Override
    public boolean login(String username, String password) { return false; }

    @Override
    public void viewComplaintStatus(int userID) {}

    @Override
    public void requestSoftwareInstallation(int userID, Software software, int computerID) {}

    @Override
    public void registerComplaint(int userID, int computerID, String description) {}
}
public class LabAssistant extends User {
    private int assistantID;

    public void approveRequest(int requestID) {}
    public void rejectRequest(int requestID) {}
    public void resolveComplaint(int complaintID) {}
    public Report generateHardwareReport() { return null; }
    public Report generateLabReport(int labID) { return null; }
    public void manageComputerDetails(Computer computer) {}
    public void manageHardwareDetails(Hardware hardware) {}
    public void trackInstalledSoftware(int computerID) {}

    @Override
    public boolean login(String username, String password) { return false; }

    @Override
    public void viewComplaintStatus(int userID) {}

    @Override
    public void requestSoftwareInstallation(int userID, Software software, int computerID) {}

    @Override
    public void registerComplaint(int userID, int computerID, String description) {}
}
public class Complaint {
    private int complaintID;
    private String submittedBy;
    private int computerID;
    private String description;
    private String status;

    public void createComplaint(String submittedBy, int computerID, String description) {}
    public void updateStatus(String status) {}
    public String getComplaintDetails() { return null; }
}
public class Software {
    private int softwareID;
    private String name;
    private int version;
    private String licenseType;

    public void installOnComputer(int computerID) {}
    public String getSoftwareDetails() { return null; }
}
import java.util.List;

public class Lab {
    private int labID;
    private String location;
    private int numberOfComputers;

    public String getLabInfo() { return null; }
    public List<Computer> listComputers() { return null; }
    public void addComputer(Computer computer) {}
}
import java.util.List;

public class Computer {
    private int computerID;
    private int labNumber;
    private String specification;
    private String status;

    public String getSpecifications() { return null; }
    public void updateStatus(String status) {}
    public void installSoftware(Software software) {}
    public List<Software> listInstalledSoftware() { return null; }
}











}
