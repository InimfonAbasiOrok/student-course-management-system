
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;


public class CourseManagementGUI extends Application {

    // ==========================================
    // COURSE COLLECTION
    // ==========================================

    ArrayList<Course> courses = new ArrayList<>();

    // Dashboard labels
    private Label totalCoursesValue;
    private Label totalUnitsValue;


    // ==========================================
    // UPDATE DASHBOARD
    // ==========================================

    private void updateDashboard() {

        if (totalCoursesValue == null || totalUnitsValue == null) {
            return;
        }

        totalCoursesValue.setText(String.valueOf(courses.size()));

        int totalUnits = 0;

        for (Course course : courses) {
            totalUnits += course.units;
        }

        totalUnitsValue.setText(String.valueOf(totalUnits));
    }


    @Override
    public void start(Stage stage) {

        // ==========================================
        // MAIN WINDOW LAYOUT
        // ==========================================

        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color: #F8F6FC;");


        // ==========================================
        // 1. ADD COURSE BUTTON
        // ==========================================

        Button addCourseButton = new Button("Add Course");

        addCourseButton.setOnAction(event -> {

            TextField courseCodeField = new TextField();
            TextField courseNameField = new TextField();
            TextField courseUnitsField = new TextField();

            courseCodeField.setPromptText("Enter Course Code");
            courseNameField.setPromptText("Enter Course Name");
            courseUnitsField.setPromptText("Enter Course Units");


            VBox formLayout = new VBox(15);

            formLayout.getChildren().addAll(
                new Label("Course Code:"),
                courseCodeField,
                new Label("Course Name:"),
                courseNameField,
                new Label("Course Units:"),
                courseUnitsField
            );


            Dialog<Void> dialog = new Dialog<>();

            dialog.setTitle("Add Course");
            dialog.getDialogPane().setContent(formLayout);

            ButtonType addButtonType = new ButtonType("Add");

            dialog.getDialogPane().getButtonTypes().addAll(
                addButtonType,
                ButtonType.CLOSE
            );


            dialog.setResultConverter(buttonType -> {

                if (buttonType == addButtonType) {

                    String code = courseCodeField.getText()
                            .trim().toUpperCase();

                    String name = courseNameField.getText().trim();

                    String unitsText = courseUnitsField.getText().trim();


                    // Validate course code
                    if (code.isEmpty()) {

                        showMessage(
                            "Error",
                            "Course code cannot be empty."
                        );

                        return null;
                    }

                    if (!code.matches(
                        "([A-Z]{3} ?[0-9]{3}|MIVA-[A-Z]{3} [0-9]{3})"
                    )) {

                        showMessage(
                            "Error",
                            "Invalid course code format!"
                        );

                        return null;
                    }


                    // Check for duplicate course codes
                    for (Course course : courses) {

                        if (course.code.replaceAll("\\s+", "")
                            .equalsIgnoreCase(
                                code.replaceAll("\\s+", "")
                            )) {

                            showMessage(
                                "Error",
                                "Course code already exists."
                            );

                            return null;
                        }
                    }


                    // Validate course name
                    if (name.isEmpty() ||
                        !name.matches("[A-Za-z]+(\\s+[A-Za-z]+)*")) {

                        showMessage(
                            "Error",
                            "Course name must contain letters and spaces only."
                        );

                        return null;
                    }


                    // Validate course units
                    int units;

                    try {

                        units = Integer.parseInt(unitsText);

                        if (units < 1 || units > 3) {

                            showMessage(
                                "Error",
                                "Course units must be between 1 and 3."
                            );

                            return null;
                        }

                    } catch (NumberFormatException e) {

                        showMessage(
                            "Error",
                            "Please enter a valid whole number for course units."
                        );

                        return null;
                    }


                    // Add course to ArrayList
                    Course newCourse = new Course(code, name, units);

                    courses.add(newCourse);

                    updateDashboard();

                    showMessage(
                        "Success",
                        "Course added successfully!"
                    );
                }

                return null;
            });


            dialog.showAndWait();

        });


        // ==========================================
        // 2. VIEW COURSES BUTTON
        // ==========================================

        Button viewCoursesButton = new Button("View Courses");

        viewCoursesButton.setOnAction(event -> {

            if (courses.isEmpty()) {

                showMessage(
                    "Information",
                    "No courses available."
                );

            } else {

                javafx.scene.control.TextArea courseDisplay =
                    new javafx.scene.control.TextArea();

                courseDisplay.setEditable(false);
                courseDisplay.setWrapText(true);


                // Display all courses
                for (Course course : courses) {

                    courseDisplay.appendText(
                        "Course Code: " + course.code + "\n" +
                        "Course Name: " + course.name + "\n" +
                        "Course Units: " + course.units + "\n" +
                        "-----------------------------\n"
                    );

                }


                Dialog<Void> viewDialog = new Dialog<>();

                viewDialog.setTitle("All Courses");

                viewDialog.getDialogPane().setContent(courseDisplay);

                viewDialog.getDialogPane().getButtonTypes().add(
                    ButtonType.CLOSE
                );

                viewDialog.showAndWait();

            }

        });


        // ==========================================
        // 3. SEARCH COURSE BY CODE BUTTON
        // ==========================================

        Button searchCourseByCodeButton =
                new Button("Search Course by Code");

        searchCourseByCodeButton.setOnAction(event -> {

            TextField searchCodeField = new TextField();

            searchCodeField.setPromptText("Enter Course Code");


            Dialog<String> searchDialog = new Dialog<>();

            searchDialog.setTitle("Search Course by Code");

            searchDialog.getDialogPane().setContent(searchCodeField);

            searchDialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
            );


            searchDialog.setResultConverter(buttonType -> {

                if (buttonType == ButtonType.OK) {

                    return searchCodeField.getText()
                            .trim().toUpperCase();
                }

                return null;
            });


            searchDialog.showAndWait().ifPresent(searchCode -> {

                if (searchCode.isEmpty()) {

                    showMessage(
                        "Error",
                        "Course code cannot be empty."
                    );

                    return;
                }


                boolean found = false;


                // Search for course
                for (Course course : courses) {

                    if (course.code.replaceAll("\\s+", "")
                        .equalsIgnoreCase(
                            searchCode.replaceAll("\\s+", "")
                        )) {

                        showMessage(
                            "Course Found",
                            "Course Code: " + course.code + "\n" +
                            "Course Name: " + course.name + "\n" +
                            "Course Units: " + course.units
                        );

                        found = true;

                        break;
                    }
                }


                // Course not found
                if (!found) {

                    showMessage(
                        "Not Found",
                        "No course found with code: " + searchCode
                    );

                }

            });

        });


        // ==========================================
        // 4. COMPUTE TOTAL UNITS BUTTON
        // ==========================================

        Button computeTotalUnitsButton =
                new Button("Compute Total Units");

        computeTotalUnitsButton.setOnAction(event -> {

            if (courses.isEmpty()) {

                showMessage(
                    "Information",
                    "No courses available to compute total units."
                );

            } else {

                int totalUnits = 0;


                // Calculate total units
                for (Course course : courses) {

                    totalUnits += course.units;

                }


                showMessage(
                    "Total Units",
                    "The total units for all courses is: "
                    + totalUnits
                );

            }

        });


        // ==========================================
        // 5. SAVE COURSES TO FILE BUTTON
        // ==========================================

        Button saveCoursestoFileButton =
                new Button("Save Courses to File");

        saveCoursestoFileButton.setOnAction(event -> {

            if (courses.isEmpty()) {

                showMessage(
                    "Information",
                    "There are no courses to save."
                );

            } else {

                try (PrintWriter writer =
                         new PrintWriter(new FileWriter("courses.txt"))) {

                    // Write each course to the file
                    for (Course course : courses) {

                        writer.println(
                            course.code + "," +
                            course.name + "," +
                            course.units
                        );

                    }


                    showMessage(
                        "Success",
                        "Courses saved successfully to courses.txt!"
                    );

                } catch (IOException e) {

                    showMessage(
                        "Error",
                        "An error occurred while saving courses."
                    );

                }

            }

        });


        // ==========================================
        // 6. LOAD COURSES FROM FILE BUTTON
        // ==========================================

        Button loadCoursesFromFileButton =
                new Button("Load Courses from File");

        loadCoursesFromFileButton.setOnAction(event -> {

            File file = new File("courses.txt");


            // Check if the file exists
            if (!file.exists()) {

                showMessage(
                    "Error",
                    "courses.txt does not exist. Please save courses first."
                );

            } else {

                try (Scanner fileScanner = new Scanner(file)) {

                    // Clear current course list
                    courses.clear();


                    // Read each line
                    while (fileScanner.hasNextLine()) {

                        String line = fileScanner.nextLine();

                        String[] data = line.split(",");


                        // Check required data
                        if (data.length == 3) {

                            String code = data[0].trim();
                            String name = data[1].trim();

                            int units = Integer.parseInt(
                                data[2].trim()
                            );


                            Course course =
                                    new Course(code, name, units);

                            courses.add(course);

                        }

                    }


                    // Update dashboard
                    updateDashboard();


                    showMessage(
                        "Success",
                        "Courses loaded successfully from courses.txt!"
                    );

                } catch (FileNotFoundException e) {

                    showMessage(
                        "Error",
                        "Unable to find courses.txt."
                    );

                } catch (NumberFormatException e) {

                    updateDashboard();

                    showMessage(
                        "Error",
                        "The file contains invalid course units."
                    );

                }

            }

        });


        // ==========================================
        // 7. DELETE BUTTON
        // ==========================================

        Button deleteButton = new Button("Delete");

        deleteButton.setOnAction(event -> {

            Dialog<String> dialog = new Dialog<>();

            dialog.setTitle("Delete Course");

            dialog.setHeaderText(
                "Enter the course code you want to delete:"
            );


            TextField courseCodeField = new TextField();

            courseCodeField.setPromptText("e.g. CSC 106");


            dialog.getDialogPane().setContent(courseCodeField);

            dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
            );


            dialog.setResultConverter(button -> {

                if (button == ButtonType.OK) {

                    return courseCodeField.getText();
                }

                return null;
            });


            String codeToDelete =
                    dialog.showAndWait().orElse(null);


            // Stop if cancelled or empty
            if (codeToDelete == null ||
                codeToDelete.trim().isEmpty()) {

                return;
            }


            boolean courseFound = false;


            // Search for the course
            for (int i = 0; i < courses.size(); i++) {

                Course course = courses.get(i);


                if (course.code.replaceAll("\\s+", "")
                    .equalsIgnoreCase(
                        codeToDelete.replaceAll("\\s+", "")
                    )) {


                    // Ask for confirmation
                    Alert confirmation =
                            new Alert(Alert.AlertType.CONFIRMATION);

                    confirmation.setTitle("Confirm Deletion");

                    confirmation.setHeaderText(
                        "Delete " + course.code + "?"
                    );

                    confirmation.setContentText(
                        "Are you sure you want to delete this course?"
                    );


                    ButtonType choice = confirmation.showAndWait()
                            .orElse(ButtonType.CANCEL);


                    if (choice == ButtonType.OK) {

                        // Remove course
                        courses.remove(i);

                        // Update dashboard
                        updateDashboard();


                        // Update courses.txt
                        try (PrintWriter writer =
                                new PrintWriter(
                                    new FileWriter("courses.txt")
                                )) {

                            for (Course remainingCourse : courses) {

                                writer.println(
                                    remainingCourse.code + "," +
                                    remainingCourse.name + "," +
                                    remainingCourse.units
                                );

                            }

                        } catch (IOException e) {

                            e.printStackTrace();

                        }


                        // Display success message
                        Alert success =
                                new Alert(Alert.AlertType.INFORMATION);

                        success.setTitle("Success");

                        success.setHeaderText("Course Deleted");

                        success.setContentText(
                            course.code +
                            " has been deleted successfully."
                        );

                        success.showAndWait();

                    }


                    courseFound = true;

                    break;
                }
            }


            // Course not found
            if (!courseFound) {

                Alert notFound =
                        new Alert(Alert.AlertType.INFORMATION);

                notFound.setTitle("Course Not Found");

                notFound.setHeaderText("No matching course");

                notFound.setContentText(
                    "The course code you entered does not exist."
                );

                notFound.showAndWait();

            }

        });


        // ==========================================
        // 8. EXIT BUTTON
        // ==========================================

        Button exitButton = new Button("Exit");

        exitButton.setOnAction(event -> {

            stage.close();

        });


        // ==========================================
        // MAIN WINDOW DISPLAY CONFIGURATION
        // ==========================================

        // HEADER
        HBox header = new HBox(15);

        header.setPadding(new Insets(20, 30, 20, 30));

        header.setAlignment(Pos.CENTER_LEFT);

        header.setStyle("-fx-background-color: #35205E;");


        Label logo = new Label("STUDENT HUB");

        logo.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label("COURSE MANAGEMENT SYSTEM");

        subtitle.setStyle(
            "-fx-text-fill: #D8C9F3;" +
            "-fx-font-size: 11px;"
        );


        VBox brand = new VBox(4);

        brand.getChildren().addAll(logo, subtitle);

        header.getChildren().add(brand);


        // MAIN CONTENT
        VBox content = new VBox(25);

        content.setPadding(new Insets(35));

        content.setAlignment(Pos.TOP_LEFT);


        // DASHBOARD TITLE
        Label dashboardTitle = new Label("Dashboard");

        dashboardTitle.setStyle(
            "-fx-text-fill: #35205E;" +
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;"
        );


        Label overviewLabel = new Label("OVERVIEW");

        overviewLabel.setStyle(
            "-fx-text-fill: #7546B5;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );


        // ==========================================
        // SUMMARY CARDS
        // ==========================================

        totalCoursesValue = new Label("0");

        totalUnitsValue = new Label("0");


        Label totalCoursesTitle =
                new Label("TOTAL COURSES");

        Label totalUnitsTitle =
                new Label("TOTAL UNITS");


        // COURSES CARD
        VBox coursesCard = new VBox(10);

        coursesCard.setPadding(new Insets(20));

        coursesCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;"
        );


        totalCoursesTitle.setStyle(
            "-fx-text-fill: #7546B5;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );


        totalCoursesValue.setStyle(
            "-fx-text-fill: #35205E;" +
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;"
        );


        coursesCard.getChildren().addAll(
            totalCoursesTitle,
            totalCoursesValue
        );


        // UNITS CARD
        VBox unitsCard = new VBox(10);

        unitsCard.setPadding(new Insets(20));

        unitsCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;"
        );


        totalUnitsTitle.setStyle(
            "-fx-text-fill: #7546B5;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );


        totalUnitsValue.setStyle(
            "-fx-text-fill: #35205E;" +
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;"
        );


        unitsCard.getChildren().addAll(
            totalUnitsTitle,
            totalUnitsValue
        );


        // SUMMARY CARDS LAYOUT
        HBox summaryCards = new HBox(25);

        coursesCard.setPrefWidth(400);

        unitsCard.setPrefWidth(400);


        summaryCards.getChildren().addAll(
            coursesCard,
            unitsCard
        );


        // ==========================================
        // BUTTON STYLING
        // ==========================================

        // ==========================================
// BUTTON STYLING
// ==========================================

String buttonStyle =
    "-fx-background-color: #78689A;" +
    "-fx-text-fill: white;" +
    "-fx-font-size: 14px;" +
    "-fx-background-radius: 10;" +
    "-fx-padding: 12 20 12 20;" +
    "-fx-cursor: hand;";


        addCourseButton.setStyle(buttonStyle);

        viewCoursesButton.setStyle(buttonStyle);

        searchCourseByCodeButton.setStyle(buttonStyle);

        computeTotalUnitsButton.setStyle(buttonStyle);

        saveCoursestoFileButton.setStyle(buttonStyle);

        loadCoursesFromFileButton.setStyle(buttonStyle);

        deleteButton.setStyle(buttonStyle);


        exitButton.setStyle(
            "-fx-background-color: #D9534F;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 12 20 12 20;" +
            "-fx-cursor: hand;"
        );


        // ==========================================
        // QUICK ACTIONS CARD
        // ==========================================

        Label quickActionsTitle =
                new Label("QUICK ACTIONS");

        quickActionsTitle.setStyle(
            "-fx-text-fill: #35205E;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );


        // FIRST ACTION ROW
        HBox firstActionRow = new HBox(15);

        firstActionRow.getChildren().addAll(
            addCourseButton,
            viewCoursesButton,
            searchCourseByCodeButton,
            deleteButton
        );


        // SECOND ACTION ROW
        HBox secondActionRow = new HBox(15);

        secondActionRow.getChildren().addAll(
            computeTotalUnitsButton,
            saveCoursestoFileButton,
            loadCoursesFromFileButton,
            exitButton
        );


        // QUICK ACTIONS CONTAINER
        VBox quickActionsCard = new VBox(20);

        quickActionsCard.setPadding(new Insets(25));

        quickActionsCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;"
        );


        quickActionsCard.getChildren().addAll(
            quickActionsTitle,
            firstActionRow,
            secondActionRow
        );


        // ==========================================
        // ADD CONTENT TO DASHBOARD
        // ==========================================

        content.getChildren().addAll(
            dashboardTitle,
            overviewLabel,
            summaryCards,
            quickActionsCard
        );


        // ==========================================
        // SCROLLABLE CONTENT
        // ==========================================

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
            "-fx-background: #F8F6FC;" +
            "-fx-background-color: #F8F6FC;"
        );


        // ==========================================
        // SET MAIN LAYOUT
        // ==========================================

        root.setTop(header);

        root.setCenter(scrollPane);


        // ==========================================
        // CREATE SCENE
        // ==========================================

        Scene scene = new Scene(root, 1000, 650);


        // ==========================================
        // CONFIGURE WINDOW
        // ==========================================

        stage.setTitle("Student Course Management System");

        stage.setScene(scene);


        // ==========================================
        // DISPLAY WINDOW
        // ==========================================

        stage.show();

    }


    // ==========================================
    // REUSABLE MESSAGE FUNCTION
    // ==========================================

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();

    }

}
