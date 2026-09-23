import java.util.List;
import java.util.Scanner;

/**
 * Main.java
 * ALL MEMBERS RESPONSIBILITY: Integration, menu-driven console interface,
 * input validation.
 *
 * This class wires together everyone's components:
 *   Member 1 -> StudentLinkedList
 *   Member 2 -> ActionStack, ServiceQueue      (this file's author on your team)
 *   Member 3 -> StudentBST, StudentHashTable
 *   Member 4 -> CampusGraph
 *
 * Requirement 13: menu-driven console with input validation.
 * Requirement 14: handles invalid inputs, duplicates, missing records, etc.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);

    // one shared instance of every data structure
    private static final StudentLinkedList linkedList = new StudentLinkedList();
    private static final ActionStack actionStack = new ActionStack();
    private static final ServiceQueue serviceQueue = new ServiceQueue();
    private static final StudentBST bst = new StudentBST();
    private static final StudentHashTable hashTable = new StudentHashTable();
    private static final CampusGraph graph = new CampusGraph();

    public static void main(String[] args) {
        boolean running = true;
        System.out.println("=========================================================");
        System.out.println(" University Student Record & Campus Route Management System");
        System.out.println("=========================================================");

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> updateStudent();
                case 3 -> deleteStudent();
                case 4 -> linkedList.displayAll();
                case 5 -> addServiceRequest();
                case 6 -> processServiceRequest();
                case 7 -> actionStack.displayRecentActions();
                case 8 -> bst.displayInOrder();
                case 9 -> searchStudentByHash();
                case 10 -> addLocation();
                case 11 -> removeLocation();
                case 12 -> addConnection();
                case 13 -> removeConnection();
                case 14 -> graph.displayConnections();
                case 15 -> traverseGraph();
                case 16 -> {
                    running = false;
                    System.out.println("Exiting... Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please select a number between 1 and 16.");
            }
            System.out.println();
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("----------------------- MENU -----------------------");
        System.out.println("1.  Add Student Record");
        System.out.println("2.  Update Student Record");
        System.out.println("3.  Delete Student Record");
        System.out.println("4.  Display All Records using Linked List");
        System.out.println("5.  Add Service Request to Queue");
        System.out.println("6.  Process Next Service Request");
        System.out.println("7.  Display Recent Actions using Stack");
        System.out.println("8.  Display Students using BST/AVL");
        System.out.println("9.  Search Student using Hashing");
        System.out.println("10. Add Campus Location");
        System.out.println("11. Remove Campus Location");
        System.out.println("12. Add Campus Connection/Road");
        System.out.println("13. Remove Campus Connection/Road");
        System.out.println("14. Display Campus Connections");
        System.out.println("15. Traverse Campus Locations using BFS or DFS");
        System.out.println("16. Exit");
        System.out.println("------------------------------------------------------");
    }

    // ---------------- Student record operations (Member 1 core, Member 2/3 sync) ----------------

    private static void addStudent() {
        String id = readNonEmpty("Enter Student ID: ");
        String name = readNonEmpty("Enter Name: ");
        String programme = readNonEmpty("Enter Programme: ");
        double marks = readDouble("Enter Marks (0-100): ", 0, 100);

        Student s = new Student(id, name, programme, marks);
        boolean added = linkedList.addStudent(s);
        if (added) {
            bst.insert(s);
            hashTable.put(id, s);
            actionStack.push("ADD", "Added student " + id + " (" + name + ")");
            System.out.println("Student added successfully.");
        }
    }

    private static void updateStudent() {
        String id = readNonEmpty("Enter Student ID to update: ");
        if (linkedList.search(id) == null) {
            System.out.println("ERROR: No student found with ID " + id);
            return;
        }
        String name = readOptional("Enter new Name (leave blank to keep unchanged): ");
        String programme = readOptional("Enter new Programme (leave blank to keep unchanged): ");
        double marks = readDoubleOptional("Enter new Marks (leave blank to keep unchanged): ");

        boolean updated = linkedList.updateStudent(id, name, programme, marks);
        if (updated) {
            Student s = linkedList.search(id);
            bst.insert(s);            // re-insert refreshes existing key
            hashTable.put(id, s);
            actionStack.push("UPDATE", "Updated student " + id);
            System.out.println("Student updated successfully.");
        }
    }

    private static void deleteStudent() {
        String id = readNonEmpty("Enter Student ID to delete: ");
        Student removed = linkedList.deleteStudent(id);
        if (removed == null) {
            System.out.println("ERROR: No student found with ID " + id);
            return;
        }
        bst.delete(id);
        hashTable.remove(id);
        actionStack.push("DELETE", "Deleted student " + id + " (" + removed.getName() + ")");
        System.out.println("Student deleted. (Logged to Recent Actions stack.)");
    }

    // ---------------- Queue / Stack operations (Member 2) ----------------

    private static void addServiceRequest() {
        String id = readNonEmpty("Enter Student ID making the request: ");
        String desc = readNonEmpty("Enter request description (e.g. 'Transcript request'): ");
        serviceQueue.enqueue(id, desc);
        actionStack.push("QUEUE", "Service request added for student " + id);
        System.out.println("Service request added to the queue.");
    }

    private static void processServiceRequest() {
        if (serviceQueue.isEmpty()) {
            System.out.println("No pending service requests to process.");
            return;
        }
        ServiceQueue.ServiceRequest req = serviceQueue.dequeue();
        actionStack.push("PROCESS", "Processed request: " + req);
        System.out.println("Processed: " + req);
    }

    // ---------------- Hashing search (Member 3) ----------------

    private static void searchStudentByHash() {
        String id = readNonEmpty("Enter Student ID to search: ");
        Student s = hashTable.get(id);
        if (s == null) System.out.println("No student found with ID " + id);
        else System.out.println("Found: " + s);
    }

    // ---------------- Graph operations (Member 4) ----------------

    private static void addLocation() {
        String loc = readNonEmpty("Enter new campus location name: ");
        if (graph.addLocation(loc)) System.out.println("Location added.");
    }

    private static void removeLocation() {
        String loc = readNonEmpty("Enter campus location to remove: ");
        if (graph.removeLocation(loc)) System.out.println("Location removed.");
    }

    private static void addConnection() {
        String a = readNonEmpty("Enter first location: ");
        String b = readNonEmpty("Enter second location: ");
        if (graph.addConnection(a, b)) System.out.println("Connection added between " + a + " and " + b + ".");
    }

    private static void removeConnection() {
        String a = readNonEmpty("Enter first location: ");
        String b = readNonEmpty("Enter second location: ");
        if (graph.removeConnection(a, b)) System.out.println("Connection removed between " + a + " and " + b + ".");
    }

    private static void traverseGraph() {
        if (graph.getAllLocations().isEmpty()) {
            System.out.println("No campus locations to traverse.");
            return;
        }
        String start = readNonEmpty("Enter starting location: ");
        System.out.println("1. BFS   2. DFS");
        int mode = readInt("Choose traversal type: ");
        List<String> order = (mode == 2) ? graph.dfs(start) : graph.bfs(start);
        if (!order.isEmpty()) {
            System.out.println((mode == 2 ? "DFS" : "BFS") + " order: " + order);
        }
    }

    // ---------------- Input validation helpers (Requirement 13/14) ----------------

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int val = Integer.parseInt(sc.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val < min || val > max) {
                    System.out.println("Value must be between " + min + " and " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double readDoubleOptional(String prompt) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return -1; // sentinel meaning "no change"
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered - keeping previous marks.");
            return -1;
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private static String readOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
