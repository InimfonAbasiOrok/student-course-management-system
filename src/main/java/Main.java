

import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
public class Main {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in); ArrayList<Course> courses = new ArrayList<>();

int choice = 0;

while (choice != 8) {
    System.out.println("===== STUDENT COURSE MANAGEMENT SYSTEM =====");
System.out.println("1. Add Course");
System.out.println("2. View All Courses");
System.out.println("3. Search Course by Code");
System.out.println("4. Compute Total Units");
System.out.println("5. Save Courses to File");
System.out.println("6. Load Courses from File");
System.out.println("7. Delete Course");
System.out.println("8. Exit");

System.out.print("Enter your choice: ");

if (input.hasNextInt()) {
    choice = input.nextInt();
} else {
    System.out.println("Invalid input! Please enter a number from 1 to 8.");
    input.next();
    continue;
}

switch (choice) {
    
    case 1:
        input.nextLine();

        String code;
        boolean duplicate;

        do {
            duplicate = false;

            System.out.print("Enter course code: ");
            code = input.nextLine().trim();

            
if (code.isEmpty()) {
    System.out.println("Course code cannot be empty. Please try again.");
    duplicate = true;
} else if (!code.matches("([A-Z]{3} ?[0-9]{3}|MIVA-[A-Z]{3} [0-9]{3})")) {
    System.out.println("Invalid course code format!");
    System.out.println("Use XXX999, XXX 999, or MIVA-XXX 999.");
    duplicate = true;
} else {

                for (Course course : courses) {
                    if (course.code.replaceAll("\\s+", "").equalsIgnoreCase(code.replaceAll("\\s+", ""))) {
                        System.out.println("Course code already exists. Please enter a different code.");
                        duplicate = true;
                        break;
                    }
                }
            }
        } while (duplicate);

        
String name;
do {
    System.out.print("Enter course name: ");
    name = input.nextLine().trim();

    if (name.isEmpty()) {
        System.out.println("Course name cannot be empty. Please try again.");
    } else if (!name.matches("[A-Za-z]+(\\s+[A-Za-z]+)*")) {
        System.out.println("Invalid course name! Use letters and spaces only.");
    }
} while (name.isEmpty() || !name.matches("[A-Za-z]+(\\s+[A-Za-z]+)*"));


        int units;
        while (true) {
            System.out.print("Enter course units: ");

            if (input.hasNextInt()) {
                units = input.nextInt();

                if (units > 0) {
                    break;
                } else {
                    System.out.println("Invalid units! Please enter a positive number.");
                }
            } else {
                System.out.println("Invalid input! Please enter a whole number.");
                input.next();
            }
        }

        Course newCourse = new Course(code, name, units);
        courses.add(newCourse);

        System.out.println("Course added successfully!");
        break;


case 2:
    if (courses.isEmpty()) {
        System.out.println("No courses available.");
    } else {
        System.out.println("===== ALL COURSES =====");

        for (Course course : courses) {
            System.out.println("Course Code: " + course.code);
            System.out.println("Course Name: " + course.name);
            System.out.println("Course Units: " + course.units);
            System.out.println("-----------------------");
        }
    }
    break;


case 3:
    input.nextLine();

    System.out.print("Enter course code to search: ");
    String searchCode = input.nextLine();

    boolean found = false;

    for (Course course : courses) {
        
if (course.code.replaceAll("\\s+", "").equalsIgnoreCase(searchCode.replaceAll("\\s+", ""))) {

            System.out.println("Course found!");
            System.out.println("Course Code: " + course.code);
            System.out.println("Course Name: " + course.name);
            System.out.println("Course Units: " + course.units);

            found = true;
            break;
        }
    }

    if (!found) {
        System.out.println("Course not found.");
    }

    break;

   case 4:
    int totalUnits = 0;

    for (Course course : courses) {
        totalUnits += course.units;
    }

    System.out.println("Total Course Units: " + totalUnits);
    break;

case 5: 
    try (PrintWriter writer = new PrintWriter(new FileWriter("courses.txt"))) {
        for (Course course : courses) {
            writer.println(course.code + "," + course.name + "," + course.units);
        }

        System.out.println("Courses saved successfully!");
    } catch (IOException e) {
        System.out.println("Error saving courses: " + e.getMessage());
    }
    break;

   case 6:

    try (Scanner fileReader = new Scanner(new File("courses.txt"))) {
        courses.clear();
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            String[] parts = line.split(",", 3);

            if (parts.length == 3) {
                String loadedCode = parts[0];
                String loadedName = parts[1];
                int loadedUnits = Integer.parseInt(parts[2]);

                Course loadedCourse = new Course(
                    loadedCode, loadedName, loadedUnits
                );

                courses.add(loadedCourse);
            }
        }

        System.out.println("Courses loaded successfully!");

    } catch (FileNotFoundException e) {
        System.out.println("No saved courses file found.");

    } catch (NumberFormatException e) {
        System.out.println("A course file contains invalid units.");
    }
    break;


case 7:
    input.nextLine();

    if (courses.isEmpty()) {
        System.out.println("No courses available to delete.");
        break;
    }

    System.out.print("Enter course code to delete: ");
    String deleteCode = input.nextLine().trim();

    boolean deleted = false;

    for (int i = 0; i < courses.size(); i++) {

        Course course = courses.get(i);

        if (course.code.replaceAll("\\s+", "")
            .equalsIgnoreCase(deleteCode.replaceAll("\\s+", ""))) {

            System.out.println("Course found!");
            System.out.println("Course Code: " + course.code);
            System.out.println("Course Name: " + course.name);
            System.out.println("Course Units: " + course.units);

            System.out.print("Are you sure you want to delete this course? (Y/N): ");
            String confirmation = input.nextLine().trim();

            if (confirmation.equalsIgnoreCase("Y")) {

                courses.remove(i);
                deleted = true;

                // Update courses.txt immediately
                try (PrintWriter writer =
                         new PrintWriter(new FileWriter("courses.txt"))) {

                    for (Course remainingCourse : courses) {
                        writer.println(
                            remainingCourse.code + "," +
                            remainingCourse.name + "," +
                            remainingCourse.units
                        );
                    }

                    System.out.println("Course deleted successfully!");
                    System.out.println("courses.txt updated successfully.");

                } catch (IOException e) {
                    System.out.println("Course deleted from memory, but the file could not be updated.");
                }

            } else {
                System.out.println("Deletion cancelled.");
            }

            break;
        }
    }

    if (!deleted) {
        System.out.println("Course not found.");
    }

    break;


    case 8:
        System.out.println("Exiting program...");
        break;

    default:
        System.out.println("Invalid choice. Please enter a number from 1 to 8.");
}

    }
input.close();

    }
}