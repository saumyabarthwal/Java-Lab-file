import java.io.*;
import java.util.*;

// STUDENT CLASS
class Student {
    int rollNo;
    String name;
    String email;
    String course;
    double marks;

    public Student(int rollNo, String name, String email, String course, double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.course = course;
        this.marks = marks;
    }

    public void display() {
        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("----------------");
    }

    // Convert Student to CSV string
    public String toCSV() {
        return rollNo + "," + name + "," + email + "," + course + "," + marks;
    }

    // Create Student from CSV string
    public static Student fromCSV(String line) {
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

// FILE UTIL CLASS
class FileUtil {

    public static List<Student> readStudentsFromFile(String filename) {
        List<Student> students = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            return students; // return empty list if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    students.add(Student.fromCSV(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return students;
    }

    public static void writeStudentsToFile(String filename, List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Student s : students) {
                bw.write(s.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}

// STUDENT MANAGER
class StudentManager {
    private List<Student> students;
    private Scanner sc;

    public StudentManager() {
        students = new ArrayList<>();
        sc = new Scanner(System.in);
    }

    public void loadFromFile(String filename) {
        students = FileUtil.readStudentsFromFile(filename);
        System.out.println("Loaded students from file:");
        viewAll();
    }

    public void saveToFile(String filename) {
        FileUtil.writeStudentsToFile(filename, students);
        System.out.println("Records saved successfully!");
    }

    public void addStudent() {
        System.out.print("Enter Roll No: ");
        int roll = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Course: ");
        String course = sc.nextLine();

        System.out.print("Enter Marks: ");
        double marks = Double.parseDouble(sc.nextLine());

        students.add(new Student(roll, name, email, course, marks));
        System.out.println("Student added successfully!");
    }

    public void viewAll() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        Iterator<Student> it = students.iterator();
        while (it.hasNext()) {
            it.next().display();
        }
    }

    public void searchByName() {
        System.out.print("Enter Name to search: ");
        String name = sc.nextLine();
        boolean found = false;
        for (Student s : students) {
            if (s.name.equalsIgnoreCase(name)) {
                s.display();
                found = true;
            }
        }
        if (!found) System.out.println("No student found with name " + name);
    }

    public void deleteByName() {
        System.out.print("Enter Name to delete: ");
        String name = sc.nextLine();
        boolean removed = students.removeIf(s -> s.name.equalsIgnoreCase(name));
        if (removed) System.out.println("Student(s) deleted successfully.");
        else System.out.println("No student found with name " + name);
    }

    public void sortByMarks() {
        students.sort(Comparator.comparingDouble(s -> -s.marks)); // descending
        System.out.println("Sorted Student List by Marks:");
        viewAll();
    }

    public void menu(String filename) {
        while (true) {
            System.out.println("===== Capstone Student Menu =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search by Name");
            System.out.println("4. Delete by Name");
            System.out.println("5. Sort by Marks");
            System.out.println("6. Save and Exit");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewAll(); break;
                case 3: searchByName(); break;
                case 4: deleteByName(); break;
                case 5: sortByMarks(); break;
                case 6: saveToFile(filename); return;
                default: System.out.println("Invalid choice!"); break;
            }
        }
    }
}

// MAIN CLASS
public class StudentRecordFileSystem {

    public static void main(String[] args) {
        String filename = "students.txt";

        StudentManager manager = new StudentManager();
        manager.loadFromFile(filename);
        manager.menu(filename);
    }
}
