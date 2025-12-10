package model;

public class Employee extends User {
    private String jobTitle;
    private double salary;

    public Employee(String id, String username, String password, String jobTitle, double salary) {
        super(id, username, password, "EMPLOYEE");
        this.jobTitle = jobTitle;
        this.salary = salary;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
