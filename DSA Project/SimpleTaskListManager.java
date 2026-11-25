import java.util.Scanner;
import java.io.*;

class Task {
    String id;
    String title;
    String priority;
    String status;

    Task(String id, String title, String priority) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = "Pending";
    }
}

class Node {
    Task data;
    Node next;

    Node(Task data) {
        this.data = data;
        this.next = null;
    }
}

public class SimpleTaskListManager {

    static Node head = null;
    static int nextId = 101;

    static Scanner sc = new Scanner(System.in);

    // Add Task
    static void addTask() {
        System.out.print("Enter task title: ");
        String title = sc.nextLine().trim();

        if (title.isEmpty() || title.contains("|")) {
            System.out.println("Invalid task title.");
            return;
        }

        System.out.print("Enter priority (High/Medium/Low): ");
        String priority = sc.nextLine().trim();

        if (!priority.equalsIgnoreCase("High")
                && !priority.equalsIgnoreCase("Medium")
                && !priority.equalsIgnoreCase("Low")) {
            System.out.println(
                "Invalid priority. Choose High, Medium or Low.");
            return;
        }

        // Standardize priority
        if (priority.equalsIgnoreCase("High")) {
            priority = "High";
        } else if (priority.equalsIgnoreCase("Medium")) {
            priority = "Medium";
        } else {
            priority = "Low";
        }

        String id = "TK" + nextId++;
        Task task = new Task(id, title, priority);

        Node newNode = new Node(task);

        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;

            while (temp.next != null) {
                temp = temp.next;
            }

            temp.next = newNode;
        }

        System.out.println("Task added. ID: " + id
                + " | " + title
                + " | Priority: " + priority
                + " | Status: Pending");
    }

    // Display All Tasks
    static void displayTasks() {
        if (head == null) {
            System.out.println("No tasks available.");
            return;
        }

        Node temp = head;
        int count = 1;

        while (temp != null) {
            Task t = temp.data;

            System.out.println(count + ". " + t.id
                    + " | " + t.title
                    + " | " + t.priority
                    + " | " + t.status);

            temp = temp.next;
            count++;
        }
    }

    // Get priority value
    static int getPriorityValue(String priority) {
        if (priority.equals("High")) {
            return 1;
        } else if (priority.equals("Medium")) {
            return 2;
        } else {
            return 3;
        }
    }

    // Sort Tasks by Priority (Bubble Sort)
    static void sortTasks() {
        if (head == null) {
            System.out.println("No tasks available to sort.");
            return;
        }

        boolean swapped;

        do {
            swapped = false;
            Node current = head;

            while (current.next != null) {
                int p1 = getPriorityValue(
                        current.data.priority);

                int p2 = getPriorityValue(
                        current.next.data.priority);

                if (p1 > p2) {
                    Task temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;

                    swapped = true;
                }

                current = current.next;
            }

        } while (swapped);

        System.out.println("Tasks sorted by priority.");
        displayTasks();
    }

    // Search Task by ID
    static Node searchTask(String id) {
        Node temp = head;

        while (temp != null) {
            if (temp.data.id.equalsIgnoreCase(id)) {
                return temp;
            }

            temp = temp.next;
        }

        return null;
    }

    // Mark Task as Done
    static void markDone() {
        System.out.print("Enter Task ID: ");
        String id = sc.nextLine().trim();

        Node found = searchTask(id);

        if (found == null) {
            System.out.println("Task ID not found.");
        } else {
            found.data.status = "Done";
            System.out.println("Task " + found.data.id
                    + " marked Done.");
        }
    }

    // Delete Task
    static void deleteTask() {
        System.out.print("Enter Task ID to delete: ");
        String id = sc.nextLine().trim();

        if (head == null) {
            System.out.println("No tasks available.");
            return;
        }

        if (head.data.id.equalsIgnoreCase(id)) {
            head = head.next;
            System.out.println("Task deleted successfully.");
            return;
        }

        Node temp = head;

        while (temp.next != null) {
            if (temp.next.data.id.equalsIgnoreCase(id)) {
                temp.next = temp.next.next;
                System.out.println("Task deleted successfully.");
                return;
            }

            temp = temp.next;
        }

        System.out.println("Task ID not found.");
    }

    // Load Tasks from File
    static void loadTasks() {
        File file = new File("tasks.txt");

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);

                if (parts.length == 4) {
                    String id = parts[0];
                    String title = parts[1];
                    String priority = parts[2];
                    String status = parts[3];

                    if (title.isEmpty()
                            || (!priority.equals("High")
                            && !priority.equals("Medium")
                            && !priority.equals("Low"))
                            || (!status.equals("Pending")
                            && !status.equals("Done"))) {
                        continue;
                    }

                    Task task = new Task(id, title, priority);
                    task.status = status;

                    Node newNode = new Node(task);

                    if (head == null) {
                        head = newNode;
                    } else {
                        Node temp = head;

                        while (temp.next != null) {
                            temp = temp.next;
                        }

                        temp.next = newNode;
                    }

                    // Generate next ID
                    if (id.startsWith("TK")) {
                        try {
                            int number = Integer.parseInt(
                                    id.substring(2));

                            if (number >= nextId) {
                                nextId = number + 1;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore invalid ID number
                        }
                    }
                }
            }

            System.out.println("Tasks loaded from file.");

        } catch (IOException e) {
            System.out.println("Error loading tasks: "
                    + e.getMessage());
        }
    }

    // Save Tasks to File
    static void saveTasks() {
        try (BufferedWriter bw =
                new BufferedWriter(
                    new FileWriter("tasks.txt"))) {

            Node temp = head;

            while (temp != null) {
                Task t = temp.data;

                bw.write(t.id + "|" + t.title + "|"
                        + t.priority + "|" + t.status);

                bw.newLine();
                temp = temp.next;
            }

            System.out.println("Tasks saved to tasks.txt.");

        } catch (IOException e) {
            System.out.println("Error saving tasks: "
                    + e.getMessage());
        }
    }

    // Main Method
    public static void main(String[] args) {

        loadTasks();

        int choice;

        do {
            System.out.println("\n===== SIMPLE TASK LIST MANAGER =====");
            System.out.println("1. Add Task");
            System.out.println("2. Display All Tasks");
            System.out.println("3. Sort by Priority");
            System.out.println("4. Mark Task as Done");
            System.out.println("5. Delete Task");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                choice = 0;
                continue;
            }

            switch (choice) {
                case 1:
                    addTask();
                    break;

                case 2:
                    displayTasks();
                    break;

                case 3:
                    sortTasks();
                    break;

                case 4:
                    markDone();
                    break;

                case 5:
                    deleteTask();
                    break;

                case 6:
                    saveTasks();
                    System.out.println("Thank you for using Task Manager!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 6);

        sc.close();
    }
}