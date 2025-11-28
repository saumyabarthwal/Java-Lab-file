import java.util.*;


// 1. ABSTRACT CLASS PERSON
abstract class Person {
    protected String name;
    protected String email;

    public Person() {}

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Abstract method
    public abstract void displayInfo();
}

// 2. STUDENT CLASS (EXTENDS PERSON)

class Student extends Person {

    int rollNo;
    String course;
    double marks;
    char grade;

    public Student() {}

    public Student(int rollNo, String name, String email, String course, double marks) {
        super(name, email);
        this.rollNo = rollNo;
        this.course = course;
        this.marks = marks;
        calculateGrade();
    }

    // Method Overloading
    public void displayInfo(String header) {
        System.out.println(header);
        displayInfo();
    }

    // Overriding abstract method (No @Override needed)
    public void displayInfo() {
        System.out.println("Student Info:");
        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + grade);
        System.out.println("----------------------------------");
    }

    // Method to determine Grade
    private void calculateGrade() {
        if (marks >= 90) grade = 'A';
        else if (marks >= 75) grade = 'B';
        else if (marks >= 60) grade = 'C';
        else grade = 'D';
    }
}


// FINAL CLASS WITH FINAL METHOD

final class FinalClass {
    public final void showFinalMessage() {
        System.out.println("This is a final method in a final class.");
    }
}


// INTERFACE FOR CRUD OPERATIONS

interface RecordActions {
    void addStudent(Student s);
    void deleteStudent(int rollNo);
    void updateStudent(int rollNo, Student newData);
    Student searchStudent(int rollNo);
    void viewAllStudents();
}


// STUDENT MANAGER IMPLEMENTING THE INTERFACE

class StudentManager implements RecordActions {

    private Map<Integer, Student> studentMap = new HashMap<>();

    public void addStudent(Student s) {
        if (studentMap.containsKey(s.rollNo)) {
            System.out.println("Duplicate Roll Number! Student NOT added.");
            return;
        }
        studentMap.put(s.rollNo, s);
        System.out.println("Student added successfully.");
    }

    public void deleteStudent(int rollNo) {
        if (studentMap.remove(rollNo) != null)
            System.out.println("Student removed successfully.");
        else
            System.out.println("Student not found.");
    }

    public void updateStudent(int rollNo, Student newData) {
        if (!studentMap.containsKey(rollNo)) {
            System.out.println("Student not found!");
            return;
        }
        studentMap.put(rollNo, newData);
        System.out.println("Student updated successfully.");
    }

    public Student searchStudent(int rollNo) {
        return studentMap.get(rollNo);
    }

    public void viewAllStudents() {
        if (studentMap.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Student s : studentMap.values()) {
            s.displayInfo();
        }
    }

    // finalize method (NO @Override)
    protected void finalize() throws Throwable {
        System.out.println("Finalize method called before object is garbage collected.");
    }
}


// MAIN CLASS

public class StudentManagementSystem {

    public static void main(String[] args) {

        StudentManager manager = new StudentManager();

        // Creating Students
        Student s1 = new Student(101, "Ankit", "ankit@mail.com", "B.Tech", 85);
        Student s2 = new Student(102, "Riya", "riya@mail.com", "M.Tech", 91);

        // ADD Students
        manager.addStudent(s1);
        manager.addStudent(s2);

        // VIEW Students
        manager.viewAllStudents();

        // Method Overloading Demonstration
        s1.displayInfo("[Note] Overloaded Display Method:");

        // Final Class & Final Method
        FinalClass f = new FinalClass();
        f.showFinalMessage();

        // Trigger finalize()
        manager = null;
        System.gc();
    }
}
