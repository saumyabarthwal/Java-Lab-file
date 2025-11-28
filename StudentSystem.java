import java.util.*;

// CUSTOM EXCEPTION
class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String msg) {
        super(msg);
    }
}

// LOADER CLASS (THREAD)
class Loader implements Runnable {

    public void run() {
        try {
            System.out.print("Loading");
            for (int i = 0; i < 5; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Loading interrupted.");
        }
    }
}

// INTERFACE FOR OPERATIONS
interface RecordActions {
    void addStudent(Student s);
    void viewAll();
    Student search(int rollNo) throws StudentNotFoundException;
}

// STUDENT CLASS
class Student {
    int rollNo;
    String name;
    String email;
    String course;
    double marks;
    char grade;

    public Student(int rollNo, String name, String email, String course, double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.course = course;
        this.marks = marks;
        calculateGrade();
    }

    // Calculate grade
    private void calculateGrade() {
        if (marks >= 90) grade = 'A';
        else if (marks >= 75) grade = 'B';
        else if (marks >= 60) grade = 'C';
        else grade = 'D';
    }

    public void display() {
        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + grade);
        System.out.println("--------------------------------");
    }
}

// STUDENT MANAGER
class StudentManager implements RecordActions {

    private Map<Integer, Student> map = new HashMap<>();

    public void addStudent(Student s) {

        // Start loader thread
        Thread t = new Thread(new Loader());
        t.start();

        try {
            t.join();  // wait for loader to finish
        } catch (Exception e) {}

        if (map.containsKey(s.rollNo)) {
            System.out.println("Duplicate Roll No! Cannot add.");
        } else {
            map.put(s.rollNo, s);
            System.out.println("Student Added Successfully!");
        }
    }

    public void viewAll() {
        if (map.isEmpty()) {
            System.out.println("No Records Found.");
            return;
        }

        for (Student s : map.values()) {
            s.display();
        }
    }

    public Student search(int rollNo) throws StudentNotFoundException {
        if (!map.containsKey(rollNo))
            throw new StudentNotFoundException("Student with Roll No " + rollNo + " not found.");
        return map.get(rollNo);
    }
}
// MAIN CLASS
public class StudentSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager();

        try {
            // Wrapper classes usage
            System.out.print("Enter Roll No (Integer): ");
            Integer roll = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            if (name.trim().isEmpty()) throw new Exception("Name cannot be empty");

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Course: ");
            String course = sc.nextLine();
            if (course.trim().isEmpty()) throw new Exception("Course cannot be empty");

            System.out.print("Enter Marks (Double): ");
            Double marks = Double.parseDouble(sc.nextLine());
            if (marks < 0 || marks > 100) throw new Exception("Marks must be between 0 and 100");

            Student s = new Student(roll, name, email, course, marks);

            manager.addStudent(s);

            System.out.println();
            manager.viewAll();

        } catch (StudentNotFoundException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            System.out.println("Program execution completed.");
        }

    }
}
