import java.io.*;
import java.util.*;

// Custom Exception
class StudentNotFoundException extends Exception {
    StudentNotFoundException(String message) {
        super(message);
    }
}

// Abstract Class Person
abstract class Person {
    protected String name;
    protected String email;

    Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public abstract void displayInfo();  // will override in Student
}

// Student Class
class Student extends Person {
    private int rollNo;
    private String course;
    private double marks;
    private String grade;

    public Student(int rollNo, String name, String email, String course, double marks) {
        super(name, email);
        this.rollNo = rollNo;
        this.course = course;
        this.marks = marks;
        calculateGrade();
    }

    public int getRollNo() { return rollNo; }
    public String getName() { return name; }
    public double getMarks() { return marks; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setCourse(String course) { this.course = course; }
    public void setMarks(double marks) { 
        this.marks = marks; 
        calculateGrade();
    }

    public void calculateGrade() {
        if (marks >= 90) grade = "A+";
        else if (marks >= 80) grade = "A";
        else if (marks >= 70) grade = "B+";
        else if (marks >= 60) grade = "B";
        else grade = "C";
    }

    public void displayDetails() {
        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + grade);
        System.out.println("------------------");
    }

    public void displayInfo() {
        displayDetails();
    }

    public String toFileString() {
        return rollNo + "," + name + "," + email + "," + course + "," + marks;
    }

    public static Student fromFileString(String line) {
        String[] parts = line.split(",");
        return new Student(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            parts[3],
            Double.parseDouble(parts[4])
        );
    }
}

// Interface for Student Actions
interface RecordActions {
    void addStudent() throws IOException;
    void deleteStudent() throws IOException;
    void updateStudent() throws IOException;
    void searchStudent();
    void viewAllStudents();
    void sortByMarks();
}

// Loader Class for Multithreading
class Loader implements Runnable {
    public void run() {
        System.out.print("Loading");
        try {
            for(int i=0;i<3;i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
        System.out.println();
    }
}

// Student Manager Class
class StudentManager implements RecordActions {
    private List<Student> students = new ArrayList<>();
    private Map<Integer, Student> studentMap = new HashMap<>();
    private final String FILE_NAME = "students.txt";
    private Scanner sc = new Scanner(System.in);

    public StudentManager() {
        loadFromFile();
    }

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromFileString(line);
                students.add(s);
                studentMap.put(s.getRollNo(), s);
            }
        } catch (IOException e) {
            System.out.println("No previous data found, starting fresh.");
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                bw.write(s.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    public void addStudent() {
        try {
            System.out.print("Enter Roll No: ");
            int rollNo = Integer.parseInt(sc.nextLine());
            if(studentMap.containsKey(rollNo)) {
                System.out.println("Roll No already exists!");
                return;
            }

            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            System.out.print("Enter Course: ");
            String course = sc.nextLine();
            System.out.print("Enter Marks: ");
            double marks = Double.parseDouble(sc.nextLine());
            if(marks < 0 || marks > 100) {
                System.out.println("Invalid marks!");
                return;
            }

            Thread loader = new Thread(new Loader());
            loader.start();
            loader.join();

            Student s = new Student(rollNo, name, email, course, marks);
            students.add(s);
            studentMap.put(rollNo, s);
            System.out.println("Student added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
    }

    public void deleteStudent() {
        try {
            System.out.print("Enter name to delete: ");
            String name = sc.nextLine();
            Student found = null;
            for (Student s : students) {
                if (s.getName().equalsIgnoreCase(name)) {
                    found = s;
                    break;
                }
            }
            if (found == null) throw new StudentNotFoundException("Student not found!");

            Thread loader = new Thread(new Loader());
            loader.start();
            loader.join();

            students.remove(found);
            studentMap.remove(found.getRollNo());
            System.out.println("Student record deleted.");
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
    }

    public void updateStudent() {
        try {
            System.out.print("Enter roll number to update: ");
            int rollNo = Integer.parseInt(sc.nextLine());
            Student s = studentMap.get(rollNo);
            if (s == null) throw new StudentNotFoundException("Student not found!");

            System.out.print("Enter new Name: ");
            s.setName(sc.nextLine());
            System.out.print("Enter new Email: ");
            s.setEmail(sc.nextLine());
            System.out.print("Enter new Course: ");
            s.setCourse(sc.nextLine());
            System.out.print("Enter new Marks: ");
            double marks = Double.parseDouble(sc.nextLine());
            if(marks < 0 || marks > 100) {
                System.out.println("Invalid marks!");
                return;
            }
            s.setMarks(marks);

            Thread loader = new Thread(new Loader());
            loader.start();
            loader.join();

            System.out.println("Student record updated.");
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
    }

    public void searchStudent() {
        System.out.print("Enter name to search: ");
        String name = sc.nextLine();
        boolean found = false;
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                System.out.println("Student Info:");
                s.displayDetails();
                found = true;
            }
        }
        if(!found) System.out.println("Student not found!");
    }

    public void viewAllStudents() {
        if(students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        for (Student s : students) s.displayDetails();
    }

    public void sortByMarks() {
        students.sort((s1, s2) -> Double.compare(s2.getMarks(), s1.getMarks()));
        System.out.println("Sorted Student List by Marks:");
        for (Student s : students) s.displayDetails();
    }

    public void saveAndExit() {
        Thread loader = new Thread(new Loader());
        try {
            loader.start();
            loader.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
        saveToFile();
        System.out.println("Saved and exiting.");
    }
}

// Main Class
public class StudentRecord{
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("===== Capstone Student Menu =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search by Name");
            System.out.println("4. Delete by Name");
            System.out.println("5. Sort by Marks");
            System.out.println("6. Update Student");
            System.out.println("7. Save and Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> manager.addStudent();
                case 2 -> manager.viewAllStudents();
                case 3 -> manager.searchStudent();
                case 4 -> manager.deleteStudent();
                case 5 -> manager.sortByMarks();
                case 6 -> manager.updateStudent();
                case 7 -> manager.saveAndExit();
                default -> System.out.println("Invalid choice!");
            }

        } while(choice != 7);
    }
}
