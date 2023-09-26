import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Student {
    private String name;
    private String studentId;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }
}

// AVLNode class for the AVL tree
class AVLNode {
    Course course;
    int height;
    AVLNode left;
    AVLNode right;

    public AVLNode(Course course) {
        this.course = course;
        this.height = 1;
        this.left = null;
        this.right = null;
    }
}

// AVLTree class for managing courses
class AVLTree {
    private AVLNode root;

    // Constructor
    public AVLTree() {
        this.root = null;
    }

    // Get height of a node
    private int getHeight(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    // Get balance factor of a node
    private int getBalanceFactor(AVLNode node) {
        if (node == null)
            return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    // Update height of a node
    private void updateHeight(AVLNode node) {
        if (node != null)
            node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    // Rotate right
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Rotate left
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Insert a course into the AVL tree
    public AVLNode insert(AVLNode node, Course course) {
        if (node == null)
            return new AVLNode(course);

        if (course.getCourseCode().compareTo(node.course.getCourseCode()) < 0)
            node.left = insert(node.left, course);
        else if (course.getCourseCode().compareTo(node.course.getCourseCode()) > 0)
            node.right = insert(node.right, course);
        else // Duplicate course codes not allowed
            return node;

        updateHeight(node);

        int balance = getBalanceFactor(node);

        // Left Heavy
        if (balance > 1) {
            if (course.getCourseCode().compareTo(node.left.course.getCourseCode()) < 0) {
                return rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }

        // Right Heavy
        if (balance < -1) {
            if (course.getCourseCode().compareTo(node.right.course.getCourseCode()) > 0) {
                return rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }

        return node;
    }

    // Wrapper function to insert a course
    public void insert(Course course) {
        root = insert(root, course);
    }

    // Search for a course by course code
    public Course search(String courseCode) {
        return search(root, courseCode);
    }

    // Helper function to search for a course
    private Course search(AVLNode node, String courseCode) {
        if (node == null)
            return null;

        if (courseCode.compareTo(node.course.getCourseCode()) < 0)
            return search(node.left, courseCode);
        else if (courseCode.compareTo(node.course.getCourseCode()) > 0)
            return search(node.right, courseCode);
        else
            return node.course;
    }

    // Inorder traversal to display courses
    public void inorder() {
        inorder(root);
    }

    private void inorder(AVLNode node) {
        if (node != null) {
            inorder(node.left);
            System.out.println("Course Code: " + node.course.getCourseCode());
            System.out.println("Course Name: " + node.course.getName());
            System.out.println("Schedule: " + node.course.getSchedule());
            System.out.println();
            inorder(node.right);
        }
    }
}

class Course {
    private String courseCode;
    private String name;
    private String schedule;
    private int maxEnrollment;
    private List<String> enrolledStudents;

    public Course(String courseCode, String name, String schedule, int maxEnrollment) {
        this.courseCode = courseCode;
        this.name = name;
        this.schedule = schedule;
        this.maxEnrollment = maxEnrollment;
        this.enrolledStudents = new ArrayList<>();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getName() {
        return name;
    }

    public String getSchedule() {
        return schedule;
    }

    public int getMaxEnrollment() {
        return maxEnrollment;
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }
}

public class StudentCourseManagementSystem {
    private HashMap<String, Student> students;
    private AVLTree courseTree; // AVL tree for courses
    private HashMap<String, List<String>> studentSchedules;

    public StudentCourseManagementSystem() {
        students = new HashMap<>();
        courseTree = new AVLTree();
        studentSchedules = new HashMap<>();
    }

    public void addStudent(String studentId, String name) {
        Student student = new Student(name, studentId);
        students.put(studentId, student);
    }

    public void addCourse(String courseCode, String name, String schedule, int maxEnrollment) {
        Course course = new Course(courseCode, name, schedule, maxEnrollment);
        courseTree.insert(course);
    }

    public void enrollStudentInCourse(String studentId, String courseCode) {
        Student student = students.get(studentId);
        Course course = courseTree.search(courseCode);

        if (student != null && course != null) {
            if (!studentSchedules.containsKey(studentId)) {
                studentSchedules.put(studentId, new ArrayList<>());
            }

            List<String> studentSchedule = studentSchedules.get(studentId);

            if (!studentSchedule.contains(courseCode)) {
                boolean hasScheduleConflict = checkScheduleConflict(studentId, course);

                if (!hasScheduleConflict) {
                    if (course.getEnrolledStudents().size() < course.getMaxEnrollment()) {
                        studentSchedule.add(courseCode);
                        course.getEnrolledStudents().add(studentId);
                        System.out.println("Enrollment successful: Student " + student.getName() +
                                " enrolled in course " + course.getName());
                    } else {
                        System.out.println("Enrollment failed: Course " + course.getName() + " is full.");
                    }
                } else {
                    System.out.println("Enrollment failed: Schedule conflict with another course.");
                }
            } else {
                System.out.println("Enrollment failed: Student " + student.getName() +
                        " is already enrolled in course " + course.getName());
            }
        } else {
            System.out.println("Enrollment failed: Student or course not found.");
        }
    }

    public void dropStudentFromCourse(String studentId, String courseCode) {
        Student student = students.get(studentId);
        Course course = courseTree.search(courseCode);

        if (student != null && course != null) {
            if (studentSchedules.containsKey(studentId)) {
                List<String> studentSchedule = studentSchedules.get(studentId);

                if (studentSchedule.contains(courseCode)) {
                    if (course.getEnrolledStudents().remove(studentId)) {
                        studentSchedule.remove(courseCode);
                        System.out.println("Course dropped: Student " + student.getName() +
                                " dropped course " + course.getName());
                    } else {
                        System.out.println("Drop failed: Student " + student.getName() +
                                " was not enrolled in course " + course.getName());
                    }
                } else {
                    System.out.println("Drop failed: Student " + student.getName() +
                            " is not enrolled in course " + course.getName());
                }
            } else {
                System.out.println("Drop failed: Student " + student.getName() +
                        " is not enrolled in any courses.");
            }
        } else {
            System.out.println("Drop failed: Student or course not found.");
        }
    }

    public void viewStudentSchedule(String studentId) {
        Student student = students.get(studentId);

        if (student != null) {
            if (studentSchedules.containsKey(studentId)) {
                List<String> studentSchedule = studentSchedules.get(studentId);

                if (!studentSchedule.isEmpty()) {
                    System.out.println("Schedule for Student " + student.getName() + ":");
                    for (String courseCode : studentSchedule) {
                        Course course = courseTree.search(courseCode);
                        if (course != null) {
                            System.out.println("Course Code: " + courseCode);
                            System.out.println("Course Name: " + course.getName());
                            System.out.println("Schedule: " + course.getSchedule());
                            System.out.println();
                        } else {
                            System.out.println("Course information not found for code: " + courseCode);
                        }
                    }
                } else {
                    System.out.println("Student " + student.getName() + " is not enrolled in any courses.");
                }
            } else {
                System.out.println("Student " + student.getName() + " is not enrolled in any courses.");
            }
        } else {
            System.out.println("Student not found.");
        }
    }

    // Helper method to check for schedule conflicts
    private boolean checkScheduleConflict(String studentId, Course newCourse) {
        List<String> studentSchedule = studentSchedules.get(studentId);

        for (String courseCode : studentSchedule) {
            Course enrolledCourse = courseTree.search(courseCode);

            if (enrolledCourse != null && haveScheduleConflict(newCourse, enrolledCourse)) {
                return true; // Schedule conflict found
            }
        }

        return false; // No schedule conflicts
    }

    // Helper method to check if two courses have schedule conflicts
    private boolean haveScheduleConflict(Course course1, Course course2) {
        // Split the schedule strings to extract day and time
        String[] schedule1Parts = course1.getSchedule().split(" ");
        String[] schedule2Parts = course2.getSchedule().split(" ");

        if (schedule1Parts.length != 3 || schedule2Parts.length != 3) {
            return false; // Invalid schedule format
        }

        String day1 = schedule1Parts[0];
        String time1 = schedule1Parts[1] + " " + schedule1Parts[2];
        String day2 = schedule2Parts[0];
        String time2 = schedule2Parts[1] + " " + schedule2Parts[2];

        // Check if the courses are on the same day and the times overlap
        return day1.equals(day2) && doTimesOverlap(time1, time2);
    }

    // Helper method to check if two times overlap
    private boolean doTimesOverlap(String time1, String time2) {
        // Parse time strings into Date objects for comparison
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date startTime1 = sdf.parse(time1);
            Date endTime1 = new Date(startTime1.getTime() + 60 * 60 * 1000); // Add 1 hour
            Date startTime2 = sdf.parse(time2);
            Date endTime2 = new Date(startTime2.getTime() + 60 * 60 * 1000); // Add 1 hour

            // Check for overlap
            return startTime1.before(endTime2) && startTime2.before(endTime1);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Invalid time format
        }
    }

    public void generateCourseRosters() {
        for (Map.Entry<String, Student> studentEntry : students.entrySet()) {
            String studentId = studentEntry.getKey();
            Student student = studentEntry.getValue();
            if (studentSchedules.containsKey(studentId)) {
                List<String> studentSchedule = studentSchedules.get(studentId);

                for (String courseCode : studentSchedule) {
                    Course course = courseTree.search(courseCode);

                    if (course != null) {
                        System.out.println("Course Code: " + courseCode);
                        System.out.println("Course Name: " + course.getName());
                        System.out.println("Enrolled Students:");


                        if (!course.getEnrolledStudents().isEmpty()) {
                            for (String enrolledStudentId : course.getEnrolledStudents()) {
                                Student enrolledStudent = students.get(enrolledStudentId);
                                if (enrolledStudent != null) {
                                    System.out.println("Student ID: " + enrolledStudentId);
                                    System.out.println("Student Name: " + enrolledStudent.getName());
                                    System.out.println();
                                }
                            }
                        }
                    }
                }
            }
            else {
                System.out.println("No Students enrolled in this course");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentCourseManagementSystem system = new StudentCourseManagementSystem();

        while (true) {
            System.out.println("\nStudent Course Management System");
            System.out.println("1. Add Student ID");
            System.out.println("2. Add Course");
            System.out.println("3. Enroll Student in Course");
            System.out.println("4. Drop Student from Course");
            System.out.println("5. View Student Schedule");
            System.out.println("6. Generate Course Rosters");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter student ID: ");
                    String studentId = scanner.nextLine();
                    System.out.print("Enter student name: ");
                    String studentName = scanner.nextLine();
                    system.addStudent(studentId, studentName);
                    System.out.println("Student added.");
                    break;
                case 2:
                    System.out.print("Enter course code: ");
                    String courseCode = scanner.nextLine();
                    System.out.print("Enter course name: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Enter course schedule (e.g., 'Monday 9:00 AM'): ");
                    String courseSchedule = scanner.nextLine();
                    System.out.print("Enter maximum enrollment for the course: ");
                    int maxEnrollment = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    system.addCourse(courseCode, courseName, courseSchedule, maxEnrollment);
                    System.out.println("Course added.");
                    break;
                case 3:
                    System.out.print("Enter student ID: ");
                    studentId = scanner.nextLine();
                    System.out.print("Enter course code: ");
                    courseCode = scanner.nextLine();
                    system.enrollStudentInCourse(studentId, courseCode);
                    break;
                case 4:
                    System.out.print("Enter student ID: ");
                    studentId = scanner.nextLine();
                    System.out.print("Enter course code: ");
                    courseCode = scanner.nextLine();
                    system.dropStudentFromCourse(studentId, courseCode);
                    break;
                case 5:
                    System.out.print("Enter student ID: ");
                    studentId = scanner.nextLine();
                    system.viewStudentSchedule(studentId);
                    break;
                case 6:
                    system.generateCourseRosters();
                    break;
                case 7:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
