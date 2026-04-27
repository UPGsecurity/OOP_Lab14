package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class AuthorsManager extends Application {
    private TableView<Author> tableView = new TableView<>();
    private TextField firstNameField = new TextField();
    private TextField lastNameField = new TextField();

    @Override
    public void start(Stage primaryStage) {
        TableColumn<Author, Integer> idCol = new TableColumn<>("AuthorID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("authorID"));

        TableColumn<Author, String> firstCol = new TableColumn<>("FirstName");
        firstCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("firstName"));

        TableColumn<Author, String> lastCol = new TableColumn<>("LastName");
        lastCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("lastName"));

        tableView.getColumns().addAll(idCol, firstCol, lastCol);

        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        HBox inputBox = new HBox(10, new Label("First Name:"), firstNameField,
                                 new Label("Last Name:"), lastNameField,
                                 addBtn, updateBtn, deleteBtn);
        inputBox.setPadding(new Insets(10));

        VBox root = new VBox(10, tableView, inputBox);
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root, 550, 400);
        primaryStage.setTitle("Authors Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadAuthors();

        addBtn.setOnAction(e -> addAuthor());
        updateBtn.setOnAction(e -> updateAuthor());
        deleteBtn.setOnAction(e -> deleteAuthor());
    }

    private void loadAuthors() {
        ObservableList<Author> authors = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Authors";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                authors.add(new Author(
                    rs.getInt("AuthorID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName")
                ));
            }
            tableView.setItems(authors);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void addAuthor() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Both names are required.");
            return;
        }

        String sql = "INSERT INTO Authors (FirstName, LastName) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.executeUpdate();
            firstNameField.clear();
            lastNameField.clear();
            loadAuthors();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Insert Failed", e.getMessage());
        }
    }

    private void updateAuthor() {
        Author selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select an author to update.");
            return;
        }

        String newFirstName = firstNameField.getText().trim();
        String newLastName = lastNameField.getText().trim();
        if (newFirstName.isEmpty() || newLastName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Both names are required.");
            return;
        }

        String sql = "UPDATE Authors SET FirstName = ?, LastName = ? WHERE AuthorID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newFirstName);
            pstmt.setString(2, newLastName);
            pstmt.setInt(3, selected.getAuthorID());
            pstmt.executeUpdate();
            firstNameField.clear();
            lastNameField.clear();
            loadAuthors();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
        }
    }

    private void deleteAuthor() {
        Author selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select an author to delete.");
            return;
        }

        String sql = "DELETE FROM Authors WHERE AuthorID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selected.getAuthorID());
            pstmt.executeUpdate();
            loadAuthors();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
